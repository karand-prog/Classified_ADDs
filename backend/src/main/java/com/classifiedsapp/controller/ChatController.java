package com.classifiedsapp.controller;

import com.classifiedsapp.model.ChatMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    
    // In-memory storage for demo purposes (in production, use database)
    private static final Map<String, List<ChatMessage>> chatHistory = new ConcurrentHashMap<>();
    private static final Map<String, String> userSessions = new ConcurrentHashMap<>();

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // Handle incoming chat messages
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        chatMessage.setTimestamp(LocalDateTime.now());
        chatMessage.setId(UUID.randomUUID().toString());
        
        // Store message in chat history
        String chatKey = getChatKey(chatMessage.getSenderId(), chatMessage.getReceiverId(), chatMessage.getAdId());
        chatHistory.computeIfAbsent(chatKey, k -> new ArrayList<>()).add(chatMessage);
        
        // Send to specific user
        messagingTemplate.convertAndSendToUser(
            chatMessage.getReceiverId(),
            "/queue/messages",
            chatMessage
        );
        
        // Send to sender for confirmation
        messagingTemplate.convertAndSendToUser(
            chatMessage.getSenderId(),
            "/queue/messages",
            chatMessage
        );
        
        System.out.println("[CHAT] Message sent: " + chatMessage);
        return chatMessage;
    }

    // Handle user joining chat
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        // Add username to web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSenderId());
        headerAccessor.getSessionAttributes().put("userId", chatMessage.getSenderId());
        
        // Store user session
        userSessions.put(chatMessage.getSenderId(), headerAccessor.getSessionId());
        
        // Send join notification
        ChatMessage joinMessage = new ChatMessage();
        joinMessage.setType(ChatMessage.MessageType.JOIN);
        joinMessage.setSenderId(chatMessage.getSenderId());
        joinMessage.setContent(chatMessage.getSenderName() + " joined the chat");
        joinMessage.setTimestamp(LocalDateTime.now());
        
        System.out.println("[CHAT] User joined: " + chatMessage.getSenderId());
        return joinMessage;
    }

    // Handle typing indicators
    @MessageMapping("/chat.typing")
    public void handleTyping(@Payload ChatMessage chatMessage) {
        messagingTemplate.convertAndSendToUser(
            chatMessage.getReceiverId(),
            "/queue/typing",
            chatMessage
        );
    }

    // Handle stop typing
    @MessageMapping("/chat.stopTyping")
    public void handleStopTyping(@Payload ChatMessage chatMessage) {
        messagingTemplate.convertAndSendToUser(
            chatMessage.getReceiverId(),
            "/queue/stopTyping",
            chatMessage
        );
    }

    // REST endpoint to get chat history
    @GetMapping("/history")
    public ResponseEntity<?> getChatHistory(
            @RequestParam String userId1,
            @RequestParam String userId2,
            @RequestParam String adId) {
        try {
            String chatKey = getChatKey(userId1, userId2, adId);
            List<ChatMessage> history = chatHistory.getOrDefault(chatKey, new ArrayList<>());
            
            // Sort by timestamp
            history.sort(Comparator.comparing(ChatMessage::getTimestamp));
            
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // REST endpoint to get user conversations
    @GetMapping("/conversations/{userId}")
    public ResponseEntity<?> getUserConversations(@PathVariable String userId) {
        try {
            Set<String> conversations = new HashSet<>();
            
            for (String chatKey : chatHistory.keySet()) {
                if (chatKey.contains(userId)) {
                    conversations.add(chatKey);
                }
            }
            
            List<Map<String, Object>> conversationList = new ArrayList<>();
            for (String chatKey : conversations) {
                String[] parts = chatKey.split("_");
                if (parts.length >= 3) {
                    String otherUserId = parts[0].equals(userId) ? parts[1] : parts[0];
                    String adId = parts[2];
                    
                    List<ChatMessage> messages = chatHistory.get(chatKey);
                    if (!messages.isEmpty()) {
                        ChatMessage lastMessage = messages.get(messages.size() - 1);
                        
                        Map<String, Object> conversation = new HashMap<>();
                        conversation.put("otherUserId", otherUserId);
                        conversation.put("adId", adId);
                        conversation.put("lastMessage", lastMessage.getContent());
                        conversation.put("lastMessageTime", lastMessage.getTimestamp());
                        conversation.put("unreadCount", messages.stream()
                            .filter(m -> !m.isRead() && !m.getSenderId().equals(userId))
                            .count());
                        
                        conversationList.add(conversation);
                    }
                }
            }
            
            // Sort by last message time
            conversationList.sort((a, b) -> 
                ((LocalDateTime) b.get("lastMessageTime"))
                    .compareTo((LocalDateTime) a.get("lastMessageTime"))
            );
            
            return ResponseEntity.ok(conversationList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // REST endpoint to mark messages as read
    @PutMapping("/markAsRead")
    public ResponseEntity<?> markAsRead(
            @RequestParam String userId1,
            @RequestParam String userId2,
            @RequestParam String adId) {
        try {
            String chatKey = getChatKey(userId1, userId2, adId);
            List<ChatMessage> messages = chatHistory.get(chatKey);
            
            if (messages != null) {
                for (ChatMessage message : messages) {
                    if (message.getReceiverId().equals(userId1)) {
                        message.setRead(true);
                    }
                }
            }
            
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // REST endpoint to update a message
    @PutMapping("/messages/{messageId}")
    public ResponseEntity<?> updateMessage(
            @PathVariable String messageId,
            @RequestBody Map<String, String> updateRequest) {
        try {
            String newContent = updateRequest.get("content");
            if (newContent == null || newContent.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Message content cannot be empty"));
            }

            boolean messageUpdated = false;
            for (List<ChatMessage> messages : chatHistory.values()) {
                for (ChatMessage message : messages) {
                    if (message.getId().equals(messageId)) {
                        message.setContent(newContent.trim());
                        message.setTimestamp(LocalDateTime.now());
                        messageUpdated = true;
                        break;
                    }
                }
                if (messageUpdated) break;
            }

            if (messageUpdated) {
                return ResponseEntity.ok(Map.of("success", true, "message", "Message updated successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // REST endpoint to delete a message
    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable String messageId) {
        try {
            boolean messageDeleted = false;
            for (List<ChatMessage> messages : chatHistory.values()) {
                if (messages.removeIf(msg -> msg.getId().equals(messageId))) {
                    messageDeleted = true;
                    break;
                }
            }

            if (messageDeleted) {
                return ResponseEntity.ok(Map.of("success", true, "message", "Message deleted successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Helper method to generate chat key
    private String getChatKey(String userId1, String userId2, String adId) {
        // Sort user IDs to ensure consistent chat key regardless of sender/receiver order
        List<String> userIds = Arrays.asList(userId1, userId2);
        Collections.sort(userIds);
        return userIds.get(0) + "_" + userIds.get(1) + "_" + adId;
    }
}
