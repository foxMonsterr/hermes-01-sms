package com.snack.vo;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ShopOrderVO {
    private Long id;
    private String orderNo;
    private String status;
    private BigDecimal totalAmount;
    private Integer totalQuantity;
    private String receiver;
    private String phone;
    private String address;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime shipTime;
    private LocalDateTime completeTime;
    private LocalDateTime cancelTime;
    private List<ShopOrderItemVO> items;
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public String getOrderNo(){return orderNo;} public void setOrderNo(String v){orderNo=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
    public BigDecimal getTotalAmount(){return totalAmount;} public void setTotalAmount(BigDecimal v){totalAmount=v;}
    public Integer getTotalQuantity(){return totalQuantity;} public void setTotalQuantity(Integer v){totalQuantity=v;}
    public String getReceiver(){return receiver;} public void setReceiver(String v){receiver=v;}
    public String getPhone(){return phone;} public void setPhone(String v){phone=v;}
    public String getAddress(){return address;} public void setAddress(String v){address=v;}
    public String getRemark(){return remark;} public void setRemark(String v){remark=v;}
    public LocalDateTime getCreateTime(){return createTime;} public void setCreateTime(LocalDateTime v){createTime=v;}
    public LocalDateTime getShipTime(){return shipTime;} public void setShipTime(LocalDateTime v){shipTime=v;}
    public LocalDateTime getCompleteTime(){return completeTime;} public void setCompleteTime(LocalDateTime v){completeTime=v;}
    public LocalDateTime getCancelTime(){return cancelTime;} public void setCancelTime(LocalDateTime v){cancelTime=v;}
    public List<ShopOrderItemVO> getItems(){return items;} public void setItems(List<ShopOrderItemVO> v){items=v;}
}
