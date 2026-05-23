package com.snack.service;

import com.snack.vo.SystemConfigVO;

public interface SystemConfigService {
    SystemConfigVO getConfig(Long currentUserId);
    SystemConfigVO updateConfig(Long currentUserId, SystemConfigVO vo);
    SystemConfigVO resetConfig(Long currentUserId);
}
