package com.snack.service;
import com.snack.dto.ShopRegisterDTO;
import com.snack.vo.LoginVO;
import com.snack.vo.UserProfileVO;

public interface ShopUserService {
    void register(ShopRegisterDTO dto);
    LoginVO login(String username, String password);
    UserProfileVO getProfile(Long userId);
    UserProfileVO updateProfile(Long userId, String nickname);
    void updatePassword(Long userId, String oldPwd, String newPwd);
}
