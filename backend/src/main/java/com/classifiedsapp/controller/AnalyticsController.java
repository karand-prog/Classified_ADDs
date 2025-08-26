package com.classifiedsapp.controller;

import com.classifiedsapp.repository.FavoriteRepository;
import com.classifiedsapp.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class AnalyticsController {

    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private MessageRepository messageRepository;

    @GetMapping("/summary")
    public ResponseEntity<?> summary(@RequestParam(required = false) Long userId) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("timestamp", LocalDateTime.now().toString());
        resp.put("userId", userId);
        long totalFavorites = favoriteRepository.count();
        long totalMessages = messageRepository.count();
        long userFavorites = userId != null ? favoriteRepository.findByUserId(userId).size() : 0;
        long userUnread = userId != null ? messageRepository.findByReceiverIdAndReadFalse(userId).size() : 0;
        resp.put("totalFavorites", totalFavorites);
        resp.put("totalMessages", totalMessages);
        resp.put("userFavorites", userFavorites);
        resp.put("userUnreadMessages", userUnread);
        resp.put("views", 0);
        resp.put("topCategories", new String[]{"job-finder","housing","buy-sell"});
        return ResponseEntity.ok(resp);
    }
}
