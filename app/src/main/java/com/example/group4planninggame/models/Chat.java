package com.example.group4planninggame.models;

public class Chat {
    private String email;
    private String chatMessage;
    private long timestamp;
    private String isRead;
    private String isNotified;
    public Chat() {}

    public Chat(String email, String chatMessage, long timestamp, String isRead) {
        this.email = email;
        this.chatMessage = chatMessage;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(String chatMessage) {
        this.chatMessage = chatMessage;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getIsRead() {
        return isRead != null ? isRead : "n";
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public String getIsNotified() {
        return isNotified != null ? isNotified : "n";
    }

    public void setIsNotified(String isNotified) {
        this.isNotified = isNotified;
    }
}
