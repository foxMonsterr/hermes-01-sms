package com.snack.service;

import com.snack.common.PageData;
import com.snack.vo.NotificationVO;
import com.snack.vo.UnreadCountVO;

import java.util.Map;

public interface NotificationService {

    PageData<NotificationVO> page(Long currentUserId, Integer page, Integer size, String type, Integer isRead);

    UnreadCountVO unreadCount(Long currentUserId);

    Map<String, Integer> generate(Long currentUserId);

    void markRead(Long currentUserId, Long id);

    void markAllRead(Long currentUserId);

    void delete(Long currentUserId, Long id);
}
