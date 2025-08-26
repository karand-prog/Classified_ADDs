package com.classifiedsapp.controller;

import com.classifiedsapp.model.Favorite;
import com.classifiedsapp.repository.FavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class FavoriteController {
    @Autowired
    private FavoriteRepository favoriteRepository;

    @GetMapping
    public List<Favorite> getFavorites(@RequestParam Long userId) {
        return favoriteRepository.findByUserId(userId);
    }

    @PostMapping
    @Transactional
    public Favorite addFavorite(@RequestBody Favorite favorite) {
        // Optionally check for duplicates
        Favorite existing = favoriteRepository.findByUserIdAndCategoryKeyAndAdId(
            favorite.getUserId(), favorite.getCategoryKey(), favorite.getAdId());
        if (existing != null) return existing;
        return favoriteRepository.save(favorite);
    }

    @DeleteMapping
    @Transactional
    public void removeFavorite(@RequestParam Long userId, @RequestParam String categoryKey, @RequestParam int adId) {
        favoriteRepository.deleteByUserIdAndCategoryKeyAndAdId(userId, categoryKey, adId);
    }
} 