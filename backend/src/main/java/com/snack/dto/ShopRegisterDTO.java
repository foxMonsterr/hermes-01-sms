package com.snack.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ShopRegisterDTO {
    @NotBlank @Size(min=2,max=50)
    private String username;
    @NotBlank @Size(min=6,max=50)
    private String password;
    @Size(max=50)
    private String nickname;
    public String getUsername(){return username;} public void setUsername(String v){username=v;}
    public String getPassword(){return password;} public void setPassword(String v){password=v;}
    public String getNickname(){return nickname;} public void setNickname(String v){nickname=v;}
}
