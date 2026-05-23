package com.snack.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "修改密码请求")
public class PasswordUpdateDTO {

    @NotBlank
    @Schema(description = "原密码")
    private String oldPassword;

    @NotBlank
    @Size(min = 6, max = 50)
    @Schema(description = "新密码", example = "654321")
    private String newPassword;

    @NotBlank
    @Schema(description = "确认新密码")
    private String confirmPassword;

    public String getOldPassword() { return oldPassword; }
    public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}
