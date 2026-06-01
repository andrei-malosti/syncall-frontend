package com.syncallapp.dto;

import java.time.LocalDateTime;

public class MessageResponse {


    private Long id;
    private String content;
    private String sendAt;
    private String userName;
    private Long userId;
    private Long chatId;

    public MessageResponse(Long id, String content, String sendAt, String userName, Long userId, Long chatId) {
        this.id = id;
        this.content = content;
        this.sendAt = sendAt;
        this.userName = userName;
        this.userId = userId;
        this.chatId = chatId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSendAt() {
        return sendAt;
    }

    public void setSendAt(String sendAt) {
        this.sendAt = sendAt;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
}
