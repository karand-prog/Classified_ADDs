package com.classifiedsapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "favorites")
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String categoryKey;

    @Column(nullable = false)
    private int adId;

    @Column(length = 512)
    private String adTitle;

    @Column(length = 1024)
    private String adText;

    @Column(length = 512)
    private String adContact;

    @Column(length = 512)
    private String adImage;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getCategoryKey() { return categoryKey; }
    public void setCategoryKey(String categoryKey) { this.categoryKey = categoryKey; }
    public int getAdId() { return adId; }
    public void setAdId(int adId) { this.adId = adId; }
    public String getAdTitle() { return adTitle; }
    public void setAdTitle(String adTitle) { this.adTitle = adTitle; }
    public String getAdText() { return adText; }
    public void setAdText(String adText) { this.adText = adText; }
    public String getAdContact() { return adContact; }
    public void setAdContact(String adContact) { this.adContact = adContact; }
    public String getAdImage() { return adImage; }
    public void setAdImage(String adImage) { this.adImage = adImage; }
} 