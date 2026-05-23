package com.snack.controller;

import com.snack.common.Result;
import com.snack.dto.*;
import com.snack.service.UserService;
import com.snack.vo.LoginVO;
import com.snack.vo.UserProfileVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@Tag(name = "用户认证", description = "注册、登录、用户信息、个人设置")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "webp");
    private final Path uploadRoot;
    private final UserService userService;

    public AuthController(UserService userService,
                          @Value("${app.upload.base-path:./uploads}") String basePath) {
        this.userService = userService;
        this.uploadRoot = Paths.get(basePath).toAbsolutePath().normalize();
        new File(uploadRoot.resolve("avatars").toString()).mkdirs();
    }

    @Operation(summary = "注册", description = "注册账号，自动创建 8 个默认分类")
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) {
        userService.register(dto);
        return Result.success();
    }

    @Operation(summary = "登录", description = "登录成功返回 JWT token 和用户信息")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(userService.login(dto));
    }

    @Operation(summary = "获取当前用户信息", description = "根据 JWT 返回用户基本信息")
    @GetMapping("/profile")
    public Result<UserProfileVO> profile(HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        return Result.success(userService.getProfile(currentUserId));
    }

    @Operation(summary = "修改密码", description = "v4.0: 需验证原密码，新密码最少6位")
    @PutMapping("/password")
    public Result<Void> updatePassword(@Valid @RequestBody PasswordUpdateDTO dto,
                                        HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        userService.updatePassword(currentUserId, dto);
        return Result.success("密码修改成功", null);
    }

    @Operation(summary = "修改个人资料", description = "v4.0: 修改昵称")
    @PutMapping("/profile")
    public Result<UserProfileVO> updateProfile(@Valid @RequestBody ProfileUpdateDTO dto,
                                                HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        return Result.success(userService.updateProfile(currentUserId, dto));
    }

    @Operation(summary = "上传头像", description = "v5.0: 上传用户头像，限 1MB jpg/png/webp")
    @PostMapping("/avatar")
    public Result<UserProfileVO> uploadAvatar(@RequestParam("file") MultipartFile file,
                                               HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("currentUserId");

        // 校验
        if (file.isEmpty()) throw new RuntimeException(new Exception("文件为空"));
        String orig = file.getOriginalFilename();
        String ext = orig.substring(orig.lastIndexOf('.') + 1).toLowerCase();
        if (!ALLOWED_EXT.contains(ext)) throw new RuntimeException(new Exception("仅支持 jpg/jpeg/png/webp"));
        if (file.getSize() > 1024 * 1024) throw new RuntimeException(new Exception("头像不能超过 1MB"));

        try {
            // 删除旧头像
            UserProfileVO old = userService.getProfile(currentUserId);
            if (old.getAvatarUrl() != null && old.getAvatarUrl().startsWith("/api/files/avatars/")) {
                String oldFile = old.getAvatarUrl().substring("/api/files/avatars/".length());
                Path oldPath = uploadRoot.resolve("avatars").resolve(oldFile).normalize();
                if (oldPath.startsWith(uploadRoot)) oldPath.toFile().delete();
            }

            // 保存新头像
            String filename = UUID.randomUUID().toString() + "." + ext;
            File dest = uploadRoot.resolve("avatars").resolve(filename).toFile();
            file.transferTo(dest);

            String avatarUrl = "/api/files/avatars/" + filename;
            return Result.success("头像上传成功", userService.updateAvatar(currentUserId, avatarUrl));
        } catch (Exception e) {
            throw new RuntimeException(new Exception("头像上传失败"));
        }
    }
}
