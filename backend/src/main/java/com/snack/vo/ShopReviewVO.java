package com.snack.vo;
import java.time.LocalDateTime;

public class ShopReviewVO {
    private Long id;
    private String username;
    private String content;
    private LocalDateTime createTime;
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public String getUsername(){return username;} public void setUsername(String v){username=v;}
    public String getContent(){return content;} public void setContent(String v){content=v;}
    public LocalDateTime getCreateTime(){return createTime;} public void setCreateTime(LocalDateTime v){createTime=v;}
}
