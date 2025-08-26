package com.classifiedsapp.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class NotificationController {
    @GetMapping
    public List<Map<String, String>> getNotifications(@RequestParam(required = false) String userId, @RequestParam(required = false) String user) {
        // Use userId if provided, otherwise fall back to user
        String userParam = userId != null ? userId : user;
        
        if (userParam == null) {
            return List.of();
        }
        
        // For demo, return mock notifications
        return List.of(
            Map.of("id", "1", "message", "Welcome, User " + userParam + "!", "read", "false"),
            Map.of("id", "2", "message", "Your profile was updated successfully.", "read", "false"),
            Map.of("id", "3", "message", "You have 3 new messages.", "read", "false"),
            Map.of("id", "4", "message", "Your ad received 5 new views.", "read", "false")
        );
    }

    @PutMapping("/{id}/read")
    public Map<String, String> markAsRead(@PathVariable String id) {
        // For demo, just return success
        return Map.of("message", "Notification marked as read");
    }
} 