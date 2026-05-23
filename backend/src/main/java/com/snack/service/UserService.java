package com.snack.service;

import com.snack.dto.*;
import com.snack.vo.LoginVO;
import com.snack.vo.UserProfileVO;

/**
 * 用户服务 v4.0
 */
public interface UserService {

    void register(RegisterDTO dto);

    LoginVO login(LoginDTO dto);

    UserProfileVO getProfile(Long currentUserId);

    /** v4.0: 修改密码 */
    void updatePassword(Long currentUserId, PasswordUpdateDTO dto);

    /** v4.0: 修改个人资料 */
    UserProfileVO updateProfile(Long currentUserId, ProfileUpdateDTO dto);

    /** v5.0: 更新头像URL */
    UserProfileVO updateAvatar(Long currentUserId, String avatarUrl);
}
