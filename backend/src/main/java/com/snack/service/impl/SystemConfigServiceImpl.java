package com.snack.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.snack.entity.SystemConfig;
import com.snack.mapper.SystemConfigMapper;
import com.snack.service.SystemConfigService;
import com.snack.vo.SystemConfigVO;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class SystemConfigServiceImpl implements SystemConfigService {
    private final SystemConfigMapper mapper;
    private static final Map<String, String> DEFAULTS = Map.of(
        "low_stock_threshold", "2", "expiry_alert_days", "30",
        "default_page_size", "10", "auto_generate_notification", "true"
    );

    public SystemConfigServiceImpl(SystemConfigMapper mapper) { this.mapper = mapper; }

    @Override
    public SystemConfigVO getConfig(Long userId) {
        List<SystemConfig> list = mapper.selectList(new LambdaQueryWrapper<SystemConfig>().eq(SystemConfig::getUserId, userId));
        SystemConfigVO vo = new SystemConfigVO();
        for (SystemConfig c : list) {
            switch (c.getConfigKey()) {
                case "low_stock_threshold": vo.setLowStockThreshold(Integer.parseInt(c.getConfigValue())); break;
                case "expiry_alert_days": vo.setExpiryAlertDays(Integer.parseInt(c.getConfigValue())); break;
                case "default_page_size": vo.setDefaultPageSize(Integer.parseInt(c.getConfigValue())); break;
                case "auto_generate_notification": vo.setAutoGenerateNotification(Boolean.parseBoolean(c.getConfigValue())); break;
            }
        }
        return vo;
    }

    @Override
    public SystemConfigVO updateConfig(Long userId, SystemConfigVO vo) {
        upsert(userId, "low_stock_threshold", String.valueOf(vo.getLowStockThreshold()));
        upsert(userId, "expiry_alert_days", String.valueOf(vo.getExpiryAlertDays()));
        upsert(userId, "default_page_size", String.valueOf(vo.getDefaultPageSize()));
        upsert(userId, "auto_generate_notification", String.valueOf(vo.getAutoGenerateNotification()));
        return getConfig(userId);
    }

    @Override
    public SystemConfigVO resetConfig(Long userId) {
        for (var e : DEFAULTS.entrySet()) upsert(userId, e.getKey(), e.getValue());
        return getConfig(userId);
    }

    private void upsert(Long userId, String key, String value) {
        SystemConfig exist = mapper.selectOne(new LambdaQueryWrapper<SystemConfig>()
            .eq(SystemConfig::getUserId, userId).eq(SystemConfig::getConfigKey, key));
        if (exist != null) { exist.setConfigValue(value); mapper.updateById(exist); }
        else { SystemConfig c = new SystemConfig(); c.setUserId(userId); c.setConfigKey(key); c.setConfigValue(value); mapper.insert(c); }
    }
}
