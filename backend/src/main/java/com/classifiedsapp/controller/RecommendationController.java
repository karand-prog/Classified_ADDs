package com.classifiedsapp.controller;

import com.classifiedsapp.repository.FavoriteRepository;
import com.classifiedsapp.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class RecommendationController {

    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private MessageRepository messageRepository;

    @GetMapping
    public ResponseEntity<?> get(@RequestParam(required = false) Long userId) {
        List<Map<String, Object>> results = new ArrayList<>();
        // Simple heuristic: recommend categories the user favorites most, fallback to global popular
        Map<String, Long> userFavCounts = new HashMap<>();
        if (userId != null) {
            userFavCounts = favoriteRepository.findByUserId(userId).stream()
                .collect(Collectors.groupingBy(f -> f.getCategoryKey(), Collectors.counting()));
        }
        List<String> sortedUserCats = userFavCounts.entrySet().stream()
            .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
            .map(Map.Entry::getKey)
            .limit(3)
            .collect(Collectors.toList());

        if (sortedUserCats.isEmpty()) {
            // Global popularity by favorites
            Map<String, Long> global = favoriteRepository.findAll().stream()
                .collect(Collectors.groupingBy(f -> f.getCategoryKey(), Collectors.counting()));
            sortedUserCats = global.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .map(Map.Entry::getKey)
                .limit(3)
                .collect(Collectors.toList());
            if (sortedUserCats.isEmpty()) {
                sortedUserCats = List.of("job-finder", "housing", "buy-sell");
            }
        }

        for (String cat : sortedUserCats) {
            results.add(Map.of(
                "title", "Recommended in " + cat,
                "category", cat
            ));
        }
        return ResponseEntity.ok(results);
    }
}
