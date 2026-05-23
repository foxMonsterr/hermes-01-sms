package com.snack.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 登录请求
 */
@Schema(description = "登录请求")
public class LoginDTO {

    @NotBlank
    @Size(min = 2, max = 50)
    @Schema(description = "用户名", example = "admin")
    private String username;

    @NotBlank
    @Size(min = 6, max = 50)
    @Schema(description = "密码", example = "123456")
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
