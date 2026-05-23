package com.snack.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "采购清单状态更新请求（仅允许 PENDING→CANCELLED）")
public class ShoppingListStatusDTO {

    @NotBlank
    @Schema(description = "目标状态: CANCELLED", example = "CANCELLED")
    private String status;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
