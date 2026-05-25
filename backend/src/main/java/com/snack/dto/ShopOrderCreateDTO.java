package com.snack.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class ShopOrderCreateDTO {
    @NotEmpty private List<Long> cartItemIds;
    @NotBlank private String receiver;
    @NotBlank private String phone;
    @NotBlank private String address;
    private String remark;
    public List<Long> getCartItemIds(){return cartItemIds;} public void setCartItemIds(List<Long> v){cartItemIds=v;}
    public String getReceiver(){return receiver;} public void setReceiver(String v){receiver=v;}
    public String getPhone(){return phone;} public void setPhone(String v){phone=v;}
    public String getAddress(){return address;} public void setAddress(String v){address=v;}
    public String getRemark(){return remark;} public void setRemark(String v){remark=v;}
}
