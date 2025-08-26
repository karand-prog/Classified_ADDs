package com.classifiedsapp.repository;

import com.classifiedsapp.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserId(Long userId);
    List<Favorite> findByUserIdAndCategoryKey(Long userId, String categoryKey);
    void deleteByUserIdAndCategoryKeyAndAdId(Long userId, String categoryKey, int adId);
    Favorite findByUserIdAndCategoryKeyAndAdId(Long userId, String categoryKey, int adId);
} 