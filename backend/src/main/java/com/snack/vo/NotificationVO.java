package com.snack.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "消息提醒")
public class NotificationVO {

    private Long id;
    private String type;
    private String title;
    private String content;
    private Long relatedId;
    private String snackName;
    private LocalDate notifyDate;
    private Integer isRead;
    private LocalDateTime createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Long getRelatedId() { return relatedId; }
    public void setRelatedId(Long relatedId) { this.relatedId = relatedId; }
    public String getSnackName() { return snackName; }
    public void setSnackName(String snackName) { this.snackName = snackName; }
    public LocalDate getNotifyDate() { return notifyDate; }
    public void setNotifyDate(LocalDate notifyDate) { this.notifyDate = notifyDate; }
    public Integer getIsRead() { return isRead; }
    public void setIsRead(Integer isRead) { this.isRead = isRead; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
