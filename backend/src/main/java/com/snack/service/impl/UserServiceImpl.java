package com.snack.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.snack.common.BusinessException;
import com.snack.dto.LoginDTO;
import com.snack.dto.PasswordUpdateDTO;
import com.snack.dto.ProfileUpdateDTO;
import com.snack.dto.RegisterDTO;
import com.snack.entity.Category;
import com.snack.entity.User;
import com.snack.mapper.CategoryMapper;
import com.snack.mapper.UserMapper;
import com.snack.security.JwtUtil;
import com.snack.service.UserService;
import com.snack.vo.LoginVO;
import com.snack.vo.UserProfileVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务实现
 */
@Service
public class UserServiceImpl implements UserService {

    /** 默认分类（名称 + 图标 + 排序） */
    private static final String[][] DEFAULT_CATEGORIES = {
            {"膨化食品",   "🍿", "1"},
            {"糖果巧克力", "🍬", "2"},
            {"坚果炒货",   "🥜", "3"},
            {"饼干糕点",   "🍪", "4"},
            {"饮料",       "🥤", "5"},
            {"肉干肉脯",   "🍖", "6"},
            {"果脯蜜饯",   "🍑", "7"},
            {"其他",       "📦", "8"},
    };

    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserMapper userMapper, CategoryMapper categoryMapper,
                           PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.categoryMapper = categoryMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional
    public void register(RegisterDTO dto) {
        // 1. 检查用户名唯一性
        boolean exists = userMapper.exists(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername())
        );
        if (exists) {
            throw BusinessException.conflict("用户名已存在");
        }

        // 2. 构建用户
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());

        userMapper.insert(user);

        // 3. 创建 8 条默认分类（事务内）
        for (String[] cat : DEFAULT_CATEGORIES) {
            Category category = new Category();
            category.setUserId(user.getId());
            category.setName(cat[0]);
            category.setIcon(cat[1]);
            category.setSortOrder(Integer.parseInt(cat[2]));
            categoryMapper.insert(category);
        }
    }

    @Override
    public LoginVO login(LoginDTO dto) {
        // 查询用户
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername())
        );

        // 用户不存在或密码错误 → 统一返回
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw BusinessException.badRequest("用户名或密码错误");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        LoginVO vo = new LoginVO(user.getId(), token, user.getUsername(), user.getNickname());
        vo.setAvatarUrl(user.getAvatarUrl());
        return vo;
    }

    @Override
    public UserProfileVO getProfile(Long currentUserId) {
        User user = userMapper.selectById(currentUserId);
        if (user == null) {
            throw BusinessException.notFound("用户不存在");
        }
        UserProfileVO vo = new UserProfileVO(user.getId(), user.getUsername(), user.getNickname());
        vo.setAvatarUrl(user.getAvatarUrl());
        return vo;
    }

    @Override
    public void updatePassword(Long currentUserId, PasswordUpdateDTO dto) {
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw BusinessException.badRequest("两次新密码不一致");
        }
        User user = userMapper.selectById(currentUserId);
        if (user == null) {
            throw BusinessException.notFound("用户不存在");
        }
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw BusinessException.badRequest("原密码错误");
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userMapper.updateById(user);
    }

    @Override
    public UserProfileVO updateProfile(Long currentUserId, ProfileUpdateDTO dto) {
        User user = userMapper.selectById(currentUserId);
        if (user == null) {
            throw BusinessException.notFound("用户不存在");
        }
        if (dto.getNickname() != null && !dto.getNickname().isBlank()) {
            user.setNickname(dto.getNickname());
            userMapper.updateById(user);
        }
        UserProfileVO vo = new UserProfileVO(user.getId(), user.getUsername(), user.getNickname());
        vo.setAvatarUrl(user.getAvatarUrl());
        return vo;
    }

    @Override
    public UserProfileVO updateAvatar(Long currentUserId, String avatarUrl) {
        User user = userMapper.selectById(currentUserId);
        if (user == null) throw BusinessException.notFound("用户不存在");
        user.setAvatarUrl(avatarUrl);
        userMapper.updateById(user);
        UserProfileVO vo = new UserProfileVO(user.getId(), user.getUsername(), user.getNickname());
        vo.setAvatarUrl(avatarUrl);
        return vo;
    }
}
