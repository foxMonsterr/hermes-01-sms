package com.snack.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * 库存增减请求
 */
@Schema(description = "库存增减请求")
public class QuantityUpdateDTO {

    @NotNull
    @Schema(description = "增减量，正数增加负数减少，不能为 0", example = "-1")
    private Integer delta;

    public Integer getDelta() { return delta; }
    public void setDelta(Integer delta) { this.delta = delta; }
}
