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
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
public class MessageController {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<Map<String, Object>> getMessages(@RequestParam Long userId) {
        List<Message> received = messageRepository.findByReceiverId(userId);
        List<Message> sent = messageRepository.findBySenderId(userId);
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
    }

    @GetMapping("/conversation")
    public List<Map<String, Object>> getConversation(@RequestParam Long user1, @RequestParam Long user2, @RequestParam int adId) {
        List<Message> conv1 = messageRepository.findBySenderIdAndReceiverIdAndAdId(user1, user2, adId);
        List<Message> conv2 = messageRepository.findBySenderIdAndReceiverIdAndAdId(user2, user1, adId);
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
    }

    @PostMapping
    public Message sendMessage(@RequestBody Message message) {
        message.setTimestamp(LocalDateTime.now());
        message.setRead(false);
        return messageRepository.save(message);
    }

    @PostMapping("/read")
    public void markAsRead(@RequestParam Long messageId) {
        messageRepository.findById(messageId).ifPresent(msg -> {
            msg.setRead(true);
            messageRepository.save(msg);
        });
    }
} 