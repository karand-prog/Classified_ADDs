package com.classifiedsapp.controller;

import com.classifiedsapp.model.Message;
import com.classifiedsapp.repository.MessageRepository;
import com.classifiedsapp.repository.UserRepository;
import com.classifiedsapp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
public class MessageController {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<Map<String, Object>> getMessages(@RequestParam String userId) {
        try {
            Long userIdLong = Long.parseLong(userId);
            List<Message> received = messageRepository.findByReceiverId(userIdLong);
            List<Message> sent = messageRepository.findBySenderId(userIdLong);
            received.addAll(sent);
            List<Map<String, Object>> result = new ArrayList<>();
            for (Message m : received) {
                User sender = userRepository.findById(m.getSenderId()).orElse(null);
                User receiver = userRepository.findById(m.getReceiverId()).orElse(null);
                Map<String, Object> map = new java.util.HashMap<>();
                map.put("id", m.getId());
                map.put("senderId", m.getSenderId());
                map.put("receiverId", m.getReceiverId());
                map.put("adId", m.getAdId());
                map.put("content", m.getContent());
                map.put("timestamp", m.getTimestamp());
                map.put("read", m.isRead());
                map.put("senderUsername", sender != null ? sender.getUsername() : "");
                map.put("receiverUsername", receiver != null ? receiver.getUsername() : "");
                map.put("senderAvatar", sender != null ? sender.getAvatar() : null);
                map.put("receiverAvatar", receiver != null ? receiver.getAvatar() : null);
                map.put("adTitle", null); // Optionally add ad title if available
                result.add(map);
            }
            return result;
        } catch (NumberFormatException e) {
            return new ArrayList<>(); // Return empty list if userId is not a valid number
        }
    }

    @GetMapping("/conversation")
    public List<Map<String, Object>> getConversation(@RequestParam String user1, @RequestParam String user2, @RequestParam int adId) {
        try {
            Long user1Long = Long.parseLong(user1);
            Long user2Long = Long.parseLong(user2);
            List<Message> conv1 = messageRepository.findBySenderIdAndReceiverIdAndAdId(user1Long, user2Long, adId);
            List<Message> conv2 = messageRepository.findBySenderIdAndReceiverIdAndAdId(user2Long, user1Long, adId);
            conv1.addAll(conv2);
            conv1.sort((a, b) -> a.getTimestamp().compareTo(b.getTimestamp()));
            List<Map<String, Object>> result = new ArrayList<>();
            for (Message m : conv1) {
                User sender = userRepository.findById(m.getSenderId()).orElse(null);
                User receiver = userRepository.findById(m.getReceiverId()).orElse(null);
                Map<String, Object> map = new java.util.HashMap<>();
                map.put("id", m.getId());
                map.put("senderId", m.getSenderId());
                map.put("receiverId", m.getReceiverId());
                map.put("adId", m.getAdId());
                map.put("content", m.getContent());
                map.put("timestamp", m.getTimestamp());
                map.put("read", m.isRead());
                map.put("senderUsername", sender != null ? sender.getUsername() : "");
                map.put("receiverUsername", receiver != null ? receiver.getUsername() : "");
                map.put("senderAvatar", sender != null ? sender.getAvatar() : null);
                map.put("receiverAvatar", receiver != null ? receiver.getAvatar() : null);
                map.put("adTitle", null); // Optionally add ad title if available
                result.add(map);
            }
            return result;
        } catch (NumberFormatException e) {
            return new ArrayList<>(); // Return empty list if userIds are not valid numbers
        }
    }

    @PostMapping
    public Message sendMessage(@RequestBody Message message) {
        message.setTimestamp(LocalDateTime.now());
        message.setRead(false);
        return messageRepository.save(message);
    }

    @PostMapping("/read")
    public void markAsRead(@RequestParam String messageId) {
        try {
            Long messageIdLong = Long.parseLong(messageId);
            messageRepository.findById(messageIdLong).ifPresent(msg -> {
                msg.setRead(true);
                messageRepository.save(msg);
            });
        } catch (NumberFormatException e) {
            // Ignore invalid messageId
        }
    }
} 