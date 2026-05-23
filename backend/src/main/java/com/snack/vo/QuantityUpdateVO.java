package com.snack.vo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "库存更新结果")
public class QuantityUpdateVO {

    @Schema(description = "零食ID", example = "1")
    private Long id;

    @Schema(description = "当前库存数量", example = "10")
    private Integer quantity;

    public QuantityUpdateVO() {}

    public QuantityUpdateVO(Long id, Integer quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
