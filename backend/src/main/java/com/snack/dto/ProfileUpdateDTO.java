package com.snack.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "修改个人资料请求")
public class ProfileUpdateDTO {

    @Size(max = 50)
    @Schema(description = "新昵称", example = "新昵称")
    private String nickname;

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
}
