package com.snack.controller.shop;
import com.snack.common.Result;
import com.snack.dto.ShopLoginDTO;
import com.snack.dto.ShopRegisterDTO;
import com.snack.service.ShopUserService;
import com.snack.vo.LoginVO;
import com.snack.vo.UserProfileVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shop/auth")
public class ShopAuthController {
    private final ShopUserService shopUserService;
    public ShopAuthController(ShopUserService shopUserService) { this.shopUserService = shopUserService; }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody ShopRegisterDTO dto) {
        shopUserService.register(dto);
        return Result.success("注册成功", null);
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody ShopLoginDTO dto) {
        return Result.success(shopUserService.login(dto.getUsername(), dto.getPassword()));
    }

    @GetMapping("/profile")
    public Result<UserProfileVO> profile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("shopUserId");
        return Result.success(shopUserService.getProfile(userId));
    }

    @PutMapping("/profile")
    public Result<UserProfileVO> updateProfile(@RequestBody ShopRegisterDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("shopUserId");
        return Result.success(shopUserService.updateProfile(userId, dto.getNickname()));
    }

    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestBody java.util.Map<String,String> body, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("shopUserId");
        shopUserService.updatePassword(userId, body.get("oldPassword"), body.get("newPassword"));
        return Result.success("密码修改成功", null);
    }
}
