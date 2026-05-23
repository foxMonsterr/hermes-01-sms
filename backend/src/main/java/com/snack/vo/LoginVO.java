package com.snack.vo;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 登录响应
 */
@Schema(description = "登录响应")
public class LoginVO {

    @Schema(description = "用户ID", example = "1")
    private Long id;

    @Schema(description = "JWT token")
    private String token;

    @Schema(description = "用户名", example = "admin")
    private String username;

    @Schema(description = "昵称", example = "管理员")
    private String nickname;

    @Schema(description = "头像URL", example = "/api/files/avatars/xxx.jpg")
    private String avatarUrl;

    public LoginVO() {}

    public LoginVO(Long id, String token, String username, String nickname) {
        this.id = id;
        this.token = token;
        this.username = username;
        this.nickname = nickname;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
}
