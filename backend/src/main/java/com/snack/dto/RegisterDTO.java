package com.snack.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 注册请求
 */
@Schema(description = "注册请求")
public class RegisterDTO {

    @NotBlank
    @Size(min = 2, max = 50)
    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    @NotBlank
    @Size(min = 6, max = 50)
    @Schema(description = "密码", example = "123456")
    private String password;

    @Size(max = 50)
    @Schema(description = "昵称", example = "张三")
    private String nickname;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
}
