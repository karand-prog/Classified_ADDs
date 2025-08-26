package com.classifiedsapp.model;

import java.time.LocalDateTime;

public class ChatMessage {
    private String id;
    private String content;
    private String senderId;
    private String receiverId;
    private String adId;
    private String adTitle;
    private MessageType type;
    private LocalDateTime timestamp;
    private String senderName;
    private String receiverName;
    private boolean read;

    public enum MessageType {
        CHAT, JOIN, LEAVE, TYPING, STOP_TYPING
    }

    // Default constructor
    public ChatMessage() {
        this.timestamp = LocalDateTime.now();
        this.read = false;
    }

    // Constructor with required fields
    public ChatMessage(String content, String senderId, String receiverId, String adId) {
        this();
        this.content = content;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.adId = adId;
        this.type = MessageType.CHAT;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public String getAdTitle() {
        return adTitle;
    }

    public void setAdTitle(String adTitle) {
        this.adTitle = adTitle;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", senderId='" + senderId + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", adId='" + adId + '\'' +
                ", type=" + type +
                ", timestamp=" + timestamp +
                ", read=" + read +
                '}';
    }
}
