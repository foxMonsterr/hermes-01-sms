package com.snack.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.snack.common.BusinessException;
import com.snack.dto.ShopRegisterDTO;
import com.snack.entity.ShopUser;
import com.snack.mapper.ShopUserMapper;
import com.snack.security.ShopJwtUtil;
import com.snack.service.ShopUserService;
import com.snack.vo.LoginVO;
import com.snack.vo.UserProfileVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ShopUserServiceImpl implements ShopUserService {
    private final ShopUserMapper shopUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final ShopJwtUtil shopJwtUtil;

    public ShopUserServiceImpl(ShopUserMapper shopUserMapper, PasswordEncoder passwordEncoder, ShopJwtUtil shopJwtUtil) {
        this.shopUserMapper = shopUserMapper; this.passwordEncoder = passwordEncoder; this.shopJwtUtil = shopJwtUtil;
    }

    @Override
    public void register(ShopRegisterDTO dto) {
        boolean exists = shopUserMapper.exists(new LambdaQueryWrapper<ShopUser>().eq(ShopUser::getUsername, dto.getUsername()));
        if (exists) throw BusinessException.conflict("用户名已存在");
        ShopUser u = new ShopUser();
        u.setUsername(dto.getUsername());
        u.setPassword(passwordEncoder.encode(dto.getPassword()));
        u.setNickname(dto.getNickname()!=null?dto.getNickname():dto.getUsername());
        shopUserMapper.insert(u);
    }

    @Override
    public LoginVO login(String username, String password) {
        ShopUser u = shopUserMapper.selectOne(new LambdaQueryWrapper<ShopUser>().eq(ShopUser::getUsername, username));
        if (u==null||!passwordEncoder.matches(password,u.getPassword())) throw BusinessException.badRequest("用户名或密码错误");
        String token = shopJwtUtil.generateToken(u.getId(), u.getUsername());
        LoginVO vo = new LoginVO(u.getId(), token, u.getUsername(), u.getNickname());
        vo.setAvatarUrl(u.getAvatarUrl());
        return vo;
    }

    @Override
    public UserProfileVO getProfile(Long userId) {
        ShopUser u = shopUserMapper.selectById(userId);
        if (u==null) throw BusinessException.notFound("用户不存在");
        UserProfileVO vo = new UserProfileVO(u.getId(), u.getUsername(), u.getNickname());
        vo.setAvatarUrl(u.getAvatarUrl());
        return vo;
    }

    @Override
    public UserProfileVO updateProfile(Long userId, String nickname) {
        ShopUser u = shopUserMapper.selectById(userId);
        if (u==null) throw BusinessException.notFound("用户不存在");
        u.setNickname(nickname); shopUserMapper.updateById(u);
        UserProfileVO vo = new UserProfileVO(u.getId(), u.getUsername(), u.getNickname());
        vo.setAvatarUrl(u.getAvatarUrl());
        return vo;
    }

    @Override
    public void updatePassword(Long userId, String oldPwd, String newPwd) {
        ShopUser u = shopUserMapper.selectById(userId);
        if (u==null) throw BusinessException.notFound("用户不存在");
        if (!passwordEncoder.matches(oldPwd, u.getPassword())) throw BusinessException.badRequest("原密码错误");
        u.setPassword(passwordEncoder.encode(newPwd)); shopUserMapper.updateById(u);
    }
}
