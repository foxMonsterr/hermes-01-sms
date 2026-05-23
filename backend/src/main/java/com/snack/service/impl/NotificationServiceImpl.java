package com.snack.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.snack.common.BusinessException;
import com.snack.common.PageData;
import com.snack.entity.Notification;
import com.snack.entity.Snack;
import com.snack.mapper.NotificationMapper;
import com.snack.mapper.SnackMapper;
import com.snack.service.NotificationService;
import com.snack.vo.NotificationVO;
import com.snack.vo.UnreadCountVO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;
    private final SnackMapper snackMapper;

    public NotificationServiceImpl(NotificationMapper notificationMapper, SnackMapper snackMapper) {
        this.notificationMapper = notificationMapper;
        this.snackMapper = snackMapper;
    }

    @Override
    public PageData<NotificationVO> page(Long currentUserId, Integer page, Integer size,
                                          String type, Integer isRead) {
        Page<Notification> mpPage = new Page<>(page != null ? page : 1, size != null ? size : 10);

        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, currentUserId);

        if (type != null && !type.isBlank()) {
            if (!type.matches("EXPIRY_SOON|EXPIRED|LOW_STOCK|STOCK_OUT")) {
                throw BusinessException.badRequest("type 参数不合法");
            }
            wrapper.eq(Notification::getType, type);
        }
        if (isRead != null) {
            wrapper.eq(Notification::getIsRead, isRead);
        }
        wrapper.orderByDesc(Notification::getCreateTime);

        Page<Notification> result = notificationMapper.selectPage(mpPage, wrapper);
        List<NotificationVO> records = result.getRecords().stream().map(this::toVO).toList();

        PageData<NotificationVO> pageData = new PageData<>();
        pageData.setRecords(records);
        pageData.setTotal(result.getTotal());
        pageData.setPage(result.getCurrent());
        pageData.setSize(result.getSize());
        return pageData;
    }

    @Override
    public UnreadCountVO unreadCount(Long currentUserId) {
        List<Notification> unread = notificationMapper.selectList(
                new LambdaQueryWrapper<Notification>()
                        .eq(Notification::getUserId, currentUserId)
                        .eq(Notification::getIsRead, 0)
        );

        UnreadCountVO vo = new UnreadCountVO();
        vo.setCount((long) unread.size());
        vo.setExpirySoonCount(unread.stream().filter(n -> "EXPIRY_SOON".equals(n.getType())).count());
        vo.setExpiredCount(unread.stream().filter(n -> "EXPIRED".equals(n.getType())).count());
        vo.setLowStockCount(unread.stream().filter(n -> "LOW_STOCK".equals(n.getType())).count());
        vo.setStockOutCount(unread.stream().filter(n -> "STOCK_OUT".equals(n.getType())).count());
        return vo;
    }

    @Override
    public Map<String, Integer> generate(Long currentUserId) {
        List<Snack> snacks = snackMapper.selectList(
                new LambdaQueryWrapper<Snack>()
                        .eq(Snack::getUserId, currentUserId)
        );

        LocalDate today = LocalDate.now();
        int created = 0;

        for (Snack s : snacks) {
            // 过期提醒
            if (s.getExpiryDate() != null) {
                long days = ChronoUnit.DAYS.between(today, s.getExpiryDate());
                String expType = null;
                String expTitle = null;
                if (days < 0) {
                    expType = "EXPIRED";
                    expTitle = "零食已过期";
                } else if (days <= 30) {
                    expType = "EXPIRY_SOON";
                    expTitle = "零食即将过期";
                }
                if (expType != null) {
                    if (tryInsert(currentUserId, expType, expTitle,
                            s.getName() + " " + (days < 0 ? "已过期" + Math.abs(days) + "天" : "将在" + days + "天后过期"),
                            s.getId(), today))
                        created++;
                }
            }

            // 低库存提醒
            String stockType = null;
            String stockTitle = null;
            if (s.getQuantity() == 0) {
                stockType = "STOCK_OUT";
                stockTitle = "零食已缺货";
            } else if (s.getQuantity() <= 2) {
                stockType = "LOW_STOCK";
                stockTitle = "零食库存不足";
            }
            if (stockType != null) {
                if (tryInsert(currentUserId, stockType, stockTitle,
                        s.getName() + " 当前库存仅剩 " + s.getQuantity() + " 件",
                        s.getId(), today))
                    created++;
            }
        }

        Map<String, Integer> result = new HashMap<>();
        result.put("createdCount", created);
        return result;
    }

    @Override
    public void markRead(Long currentUserId, Long id) {
        Notification n = notificationMapper.selectById(id);
        if (n == null || !n.getUserId().equals(currentUserId)) {
            throw BusinessException.notFound("提醒不存在");
        }
        n.setIsRead(1);
        notificationMapper.updateById(n);
    }

    @Override
    public void markAllRead(Long currentUserId) {
        List<Notification> unread = notificationMapper.selectList(
                new LambdaQueryWrapper<Notification>()
                        .eq(Notification::getUserId, currentUserId)
                        .eq(Notification::getIsRead, 0)
        );
        for (Notification n : unread) {
            n.setIsRead(1);
            notificationMapper.updateById(n);
        }
    }

    @Override
    public void delete(Long currentUserId, Long id) {
        Notification n = notificationMapper.selectById(id);
        if (n == null || !n.getUserId().equals(currentUserId)) {
            throw BusinessException.notFound("提醒不存在");
        }
        notificationMapper.deleteById(id);
    }

    // ---------- private ----------

    /** 尝试插入提醒，利用唯一索引跳过重复 */
    private boolean tryInsert(Long userId, String type, String title, String content,
                               Long relatedId, LocalDate notifyDate) {
        try {
            Notification n = new Notification();
            n.setUserId(userId);
            n.setType(type);
            n.setTitle(title);
            n.setContent(content);
            n.setRelatedId(relatedId);
            n.setNotifyDate(notifyDate);
            n.setIsRead(0);
            notificationMapper.insert(n);
            return true;
        } catch (Exception e) {
            // 唯一索引冲突，跳过
            return false;
        }
    }

    private NotificationVO toVO(Notification n) {
        NotificationVO vo = new NotificationVO();
        vo.setId(n.getId());
        vo.setType(n.getType());
        vo.setTitle(n.getTitle());
        vo.setContent(n.getContent());
        vo.setRelatedId(n.getRelatedId());
        vo.setNotifyDate(n.getNotifyDate());
        vo.setIsRead(n.getIsRead());
        vo.setCreateTime(n.getCreateTime());

        Snack snack = snackMapper.selectById(n.getRelatedId());
        vo.setSnackName(snack != null ? snack.getName() : "已删除");
        return vo;
    }
}
