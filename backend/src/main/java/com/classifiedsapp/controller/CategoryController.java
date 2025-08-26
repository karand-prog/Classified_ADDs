package com.classifiedsapp.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
public class CategoryController {
    private static final Map<String, Map<String, Object>> categoryData = new HashMap<>();
    private static final Map<String, List<Map<String, Object>>> postedAds = new HashMap<>(); // new ads per category
    
    static {
        // Initialize postedAds with empty lists for all categories
        postedAds.put("job-finder", new ArrayList<>());
        postedAds.put("required", new ArrayList<>());
        postedAds.put("work-from-home", new ArrayList<>());
        postedAds.put("education", new ArrayList<>());
        postedAds.put("marriage", new ArrayList<>());
        postedAds.put("personal", new ArrayList<>());
        postedAds.put("land-details", new ArrayList<>());
        postedAds.put("for-rent", new ArrayList<>());
        postedAds.put("health", new ArrayList<>());
        postedAds.put("addiction", new ArrayList<>());
        postedAds.put("tourism", new ArrayList<>());
        postedAds.put("buy-sell", new ArrayList<>());
        postedAds.put("services", new ArrayList<>());
        postedAds.put("housing", new ArrayList<>());
        postedAds.put("vehicles", new ArrayList<>());
        postedAds.put("healthcare", new ArrayList<>());
        postedAds.put("technology", new ArrayList<>());
        postedAds.put("fashion", new ArrayList<>());
        postedAds.put("sports", new ArrayList<>());
        postedAds.put("entertainment", new ArrayList<>());
        postedAds.put("food", new ArrayList<>());
        postedAds.put("travel", new ArrayList<>());
        postedAds.put("many-others", new ArrayList<>());
        
        // Add some demo posted ads for testing
        List<Map<String, Object>> demoJobAds = new ArrayList<>();
        demoJobAds.add(Map.of(
            "id", "demo-job-1",
            "title", "Software Developer Needed",
            "text", "Looking for experienced Java developer for our team",
            "contact", "hr@company.com",
            "userId", "1",
            "image", "",
            "images", new ArrayList<>()
        ));
        postedAds.put("job-finder", demoJobAds);
        
        List<Map<String, Object>> demoEducationAds = new ArrayList<>();
        demoEducationAds.add(Map.of(
            "id", "demo-edu-1",
            "title", "Math Tutor Available",
            "text", "Experienced math tutor for high school students",
            "contact", "tutor@education.com",
            "userId", "1",
            "image", "",
            "images", new ArrayList<>()
        ));
        postedAds.put("education", demoEducationAds);
        
        // Initialize categoryData
        Map<String, Object> jobFinder = new HashMap<>();
        jobFinder.put("name", "Job Finder");
        jobFinder.put("icon", "👔");
        jobFinder.put("ads", List.of(
            Map.of("id", 1, "text", "ऑफिससाठी टेली कॉलर, ऑफिस स्टाफ, मार्केटिंग स्टाफ, शिपाई, घरकामासाठी महिला पाहिजेत. संपर्क: 9421973737"),
            Map.of("id", 2, "text", "Require BE / Diploma / ITI Electrical Candidate for field work in Power sector. Send Resume Wh. 9325170078 Email: powerjaydeep@gmail.com"),
            Map.of("id", 3, "text", "Wanted Teachers for Eng. Maths, Sci, Drawing Sub. For Primary & High School, Fluent in Eng. Interview-8th, 9th, 10th May. Timing- 9am To 2pm. Venue- Ellora Eng. School Khadi Rd, Bead by Pass Mob. 9921151770/9822625772."),
            Map.of("id", 4, "text", "Requirement of candidates in:- Accounts, purchase, Store assistant, Marketing & Electrical Engineer (Transformer knowledge must). Computer Knowledge Must to all posts. Experience- Min. 2-3 Years. Megha Electrical services, C-15, MIDC, Waluj. M - 9421676830, 9762703532.")
        ));
        categoryData.put("job-finder", jobFinder);

        Map<String, Object> required = new HashMap<>();
        required.put("name", "Required");
        required.put("icon", "❗");
        required.put("ads", List.of(
            Map.of("id", 1, "text", "संपूर्ण महाराष्ट्रासाठी दूरसंचार टॉवर एक्टिविटीसाठी फर्म टॉलीसाठी प्रमाणित फिगर पाहिजे. पगार 18 हजार-22 हजार."),
            Map.of("id", 2, "text", "Required Experienced Staff in CA Office in Taxation Audit & Tally Working : 8855830685")
        ));
        categoryData.put("required", required);

        Map<String, Object> workFromHome = new HashMap<>();
        workFromHome.put("name", "Work From Home");
        workFromHome.put("icon", "💻");
        workFromHome.put("ads", List.of(
            Map.of("id", 1, "text", "Work from home opportunity for students and housewives. Contact: 9876543210")
        ));
        categoryData.put("work-from-home", workFromHome);

        Map<String, Object> education = new HashMap<>();
        education.put("name", "Education");
        education.put("icon", "🎓");
        education.put("ads", List.of(
            Map.of("id", 1, "text", "Wanted Teachers for Eng. Maths, Sci, Drawing Sub. For Primary & High School, Fluent in Eng. Interview-8th, 9th, 10th May.")
        ));
        categoryData.put("education", education);

        Map<String, Object> marriage = new HashMap<>();
        marriage.put("name", "Marriage");
        marriage.put("icon", "💍");
        marriage.put("ads", List.of(
            Map.of("id", 1, "text", "Looking for suitable bride for well-educated groom. Contact: 9000000000")
        ));
        categoryData.put("marriage", marriage);

        Map<String, Object> personal = new HashMap<>();
        personal.put("name", "Personal");
        personal.put("icon", "👤");
        personal.put("ads", List.of(
            Map.of("id", 1, "text", "Personal assistant required for business owner. Contact: 9123456789")
        ));
        categoryData.put("personal", personal);

        Map<String, Object> landDetails = new HashMap<>();
        landDetails.put("name", "Land Details");
        landDetails.put("icon", "📍");
        landDetails.put("ads", List.of(
            Map.of("id", 1, "text", "Land for sale in Pune. 2000 sq.ft. Contact: 9988776655")
        ));
        categoryData.put("land-details", landDetails);

        Map<String, Object> forRent = new HashMap<>();
        forRent.put("name", "For Rent");
        forRent.put("icon", "🏠");
        forRent.put("ads", List.of(
            Map.of("id", 1, "text", "2BHK flat for rent in Mumbai. Contact: 9876543211")
        ));
        categoryData.put("for-rent", forRent);

        Map<String, Object> health = new HashMap<>();
        health.put("name", "Health");
        health.put("icon", "➕");
        health.put("ads", List.of(
            Map.of("id", 1, "text", "Health supplements available at discount. Call: 9001122334")
        ));
        categoryData.put("health", health);

        Map<String, Object> addiction = new HashMap<>();
        addiction.put("name", "Addiction");
        addiction.put("icon", "🚫");
        addiction.put("ads", List.of(
            Map.of("id", 1, "text", "Addiction recovery program. Contact: 8001234567")
        ));
        categoryData.put("addiction", addiction);

        Map<String, Object> tourism = new HashMap<>();
        tourism.put("name", "Tourism");
        tourism.put("icon", "🏞️");
        tourism.put("ads", List.of(
            Map.of("id", 1, "text", "Tour packages available for Goa and Kerala. Call: 9090909090")
        ));
        categoryData.put("tourism", tourism);

        Map<String, Object> business = new HashMap<>();
        business.put("name", "Business");
        business.put("icon", "💼");
        business.put("ads", List.of(
            Map.of("id", 1, "text", "Business partnership opportunity. Contact: 9112233445")
        ));
        categoryData.put("business", business);

        Map<String, Object> mobileTower = new HashMap<>();
        mobileTower.put("name", "Mobile Tower");
        mobileTower.put("icon", "📡");
        mobileTower.put("ads", List.of(
            Map.of("id", 1, "text", "Mobile tower installation on your land. Contact: 9009988776")
        ));
        categoryData.put("mobile-tower", mobileTower);

        Map<String, Object> sexProblem = new HashMap<>();
        sexProblem.put("name", "Sex Problem");
        sexProblem.put("icon", "❤️‍🩹");
        sexProblem.put("ads", List.of(
            Map.of("id", 1, "text", "Consultation for sex-related problems. Call: 8008008008")
        ));
        categoryData.put("sex-problem", sexProblem);

        Map<String, Object> manyOthers = new HashMap<>();
        manyOthers.put("name", "Many others");
        manyOthers.put("icon", "⋯");
        manyOthers.put("ads", List.of(
            Map.of("id", 1, "text", "Contact for more classified categories and ads.")
        ));
        categoryData.put("many-others", manyOthers);

        // Add missing categories that frontend expects
        Map<String, Object> buySell = new HashMap<>();
        buySell.put("name", "Buy & Sell");
        buySell.put("icon", "💰");
        buySell.put("ads", List.of(
            Map.of("id", 1, "text", "Selling iPhone 13 Pro Max. Excellent condition. Contact: 9876543210"),
            Map.of("id", 2, "text", "Buying old laptops and computers. Any condition. Call: 9001122334")
        ));
        categoryData.put("buy-sell", buySell);

        Map<String, Object> services = new HashMap<>();
        services.put("name", "Services");
        services.put("icon", "🔧");
        services.put("ads", List.of(
            Map.of("id", 1, "text", "Professional cleaning services available. Contact: 9876543211"),
            Map.of("id", 2, "text", "Plumbing and electrical services. 24/7 available. Call: 9001122335")
        ));
        categoryData.put("services", services);

        Map<String, Object> housing = new HashMap<>();
        housing.put("name", "Housing");
        housing.put("icon", "🏘️");
        housing.put("ads", List.of(
            Map.of("id", 1, "text", "3BHK apartment for sale in prime location. Contact: 9876543212"),
            Map.of("id", 2, "text", "Studio apartment for rent. Near metro station. Call: 9001122336")
        ));
        categoryData.put("housing", housing);

        Map<String, Object> vehicles = new HashMap<>();
        vehicles.put("name", "Vehicles");
        vehicles.put("icon", "🚗");
        vehicles.put("ads", List.of(
            Map.of("id", 1, "text", "Honda City 2019 for sale. Single owner. Contact: 9876543213"),
            Map.of("id", 2, "text", "Buying used cars. Any make and model. Call: 9001122337")
        ));
        categoryData.put("vehicles", vehicles);

        Map<String, Object> healthcare = new HashMap<>();
        healthcare.put("name", "Healthcare");
        healthcare.put("icon", "🏥");
        healthcare.put("ads", List.of(
            Map.of("id", 1, "text", "Home healthcare services available. Contact: 9876543215"),
            Map.of("id", 2, "text", "Dental consultation at home. Call: 9001122339")
        ));
        categoryData.put("healthcare", healthcare);

        Map<String, Object> technology = new HashMap<>();
        technology.put("name", "Technology");
        technology.put("icon", "💻");
        technology.put("ads", List.of(
            Map.of("id", 1, "text", "Web development services. Contact: 9876543216"),
            Map.of("id", 2, "text", "Mobile app development. Call: 9001122340")
        ));
        categoryData.put("technology", technology);

        Map<String, Object> fashion = new HashMap<>();
        fashion.put("name", "Fashion & Beauty");
        fashion.put("icon", "👗");
        fashion.put("ads", List.of(
            Map.of("id", 1, "text", "Designer clothes for sale. Contact: 9876543217"),
            Map.of("id", 2, "text", "Beauty salon services at home. Call: 9001122341")
        ));
        categoryData.put("fashion", fashion);

        Map<String, Object> sports = new HashMap<>();
        sports.put("name", "Sports & Fitness");
        sports.put("icon", "⚽");
        sports.put("ads", List.of(
            Map.of("id", 1, "text", "Personal trainer available. Contact: 9876543218"),
            Map.of("id", 2, "text", "Gym equipment for sale. Call: 9001122342")
        ));
        categoryData.put("sports", sports);

        Map<String, Object> entertainment = new HashMap<>();
        entertainment.put("name", "Entertainment");
        entertainment.put("icon", "🎬");
        entertainment.put("ads", List.of(
            Map.of("id", 1, "text", "Event planning services. Contact: 9876543219"),
            Map.of("id", 2, "text", "DJ services for parties. Call: 9001122343")
        ));
        categoryData.put("entertainment", entertainment);

        Map<String, Object> food = new HashMap<>();
        food.put("name", "Food & Dining");
        food.put("icon", "🍕");
        food.put("ads", List.of(
            Map.of("id", 1, "text", "Home-cooked food delivery. Contact: 9876543220"),
            Map.of("id", 2, "text", "Catering services for events. Call: 9001122344")
        ));
        categoryData.put("food", food);

        Map<String, Object> travel = new HashMap<>();
        travel.put("name", "Travel & Tourism");
        travel.put("icon", "✈️");
        travel.put("ads", List.of(
            Map.of("id", 1, "text", "Travel packages available. Contact: 9876543221"),
            Map.of("id", 2, "text", "Hotel booking services. Call: 9001122345")
        ));
        categoryData.put("travel", travel);
    }

    @GetMapping("/{categoryKey}/ads")
    public Map<String, Object> getCategoryAds(@PathVariable String categoryKey) {
        Map<String, Object> category = categoryData.get(categoryKey);
        if (category == null) {
            return Map.of("error", "Category not found");
        }
        // Combine static ads and posted ads
        List<Map<String, Object>> staticAds = (List<Map<String, Object>>) category.get("ads");
        List<Map<String, Object>> dynamicAds = postedAds.getOrDefault(categoryKey, new ArrayList<>());
        List<Map<String, Object>> allAds = new ArrayList<>();
        allAds.addAll(staticAds);
        allAds.addAll(dynamicAds);
        Map<String, Object> result = new HashMap<>(category);
        result.put("ads", allAds);
        return result;
    }

    @PostMapping("/{categoryKey}/ads")
    public ResponseEntity<?> postAd(@PathVariable String categoryKey, @RequestBody Map<String, Object> body) {
        try {
            // Get the category data
        Map<String, Object> category = categoryData.get(categoryKey);
        if (category == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Category not found"));
        }

            // Create new ad
            Map<String, Object> newAd = new HashMap<>();
            newAd.put("id", System.currentTimeMillis()); // Simple ID generation
            newAd.put("title", body.get("title"));
            newAd.put("text", body.get("text"));
            newAd.put("contact", body.get("contact"));
            
            // Handle images - can be single string or array
            Object imageData = body.get("images");
            if (imageData instanceof List) {
                // If it's an array, join with comma or take first image
                List<?> images = (List<?>) imageData;
                if (!images.isEmpty()) {
                    newAd.put("image", images.get(0)); // Use first image for now
                    newAd.put("images", images); // Store all images
                } else {
                    newAd.put("image", "");
                    newAd.put("images", new ArrayList<>());
                }
            } else {
                // If it's a single string
                newAd.put("image", imageData != null ? imageData : "");
                newAd.put("images", imageData != null ? List.of(imageData) : new ArrayList<>());
            }
            
            newAd.put("userId", body.get("userId")); // Add user ID to track ownership
            newAd.put("categoryKey", categoryKey); // Add category key for easy filtering

            // Add to posted ads
            if (!postedAds.containsKey(categoryKey)) {
                postedAds.put(categoryKey, new ArrayList<>());
            }
            postedAds.get(categoryKey).add(newAd);

            System.out.println("[POST AD] Category: " + categoryKey + ", Data: " + body);
            System.out.println("[POST AD] Success: " + newAd);
            System.out.println("[POST AD] Posted ads in category now: " + postedAds.get(categoryKey).size());
            System.out.println("[POST AD] All posted ads: " + postedAds);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "ad", newAd
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // New endpoint to get user's ads
    @GetMapping("/user/{userId}/ads")
    public ResponseEntity<?> getUserAds(@PathVariable String userId) {
        try {
            if (userId == null || userId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "User ID is required"));
            }
            
            System.out.println("[GET USER ADS] Starting search for user ID: " + userId);
            List<Map<String, Object>> userAds = new ArrayList<>();
            
            // Check posted ads in all categories
            if (postedAds != null) {
                System.out.println("[GET USER ADS] Checking postedAds, total categories: " + postedAds.size());
                for (Map.Entry<String, List<Map<String, Object>>> entry : postedAds.entrySet()) {
                    String categoryKey = entry.getKey();
                    List<Map<String, Object>> ads = entry.getValue();
                    
                    System.out.println("[GET USER ADS] Category: " + categoryKey + ", Ads count: " + (ads != null ? ads.size() : 0));
                    
                    if (ads != null) {
                        for (Map<String, Object> ad : ads) {
                            if (ad != null) {
                                Object adUserId = ad.get("userId");
                                System.out.println("[GET USER ADS] Ad ID: " + ad.get("id") + ", Ad UserID: " + adUserId + ", Looking for: " + userId + ", Match: " + userId.equals(adUserId));
                                
                                // Handle both string and numeric userId types
                                boolean userIdMatch = false;
                                if (adUserId != null) {
                                    if (userId.equals(adUserId.toString()) || userId.equals(adUserId)) {
                                        userIdMatch = true;
                                    }
                                }
                                
                                if (userIdMatch) {
                                    // Add category info to the ad
                                    Map<String, Object> adWithCategory = new HashMap<>(ad);
                                    adWithCategory.put("categoryKey", categoryKey);
                                    adWithCategory.put("categoryName", getCategoryName(categoryKey));
                                    userAds.add(adWithCategory);
                                    System.out.println("[GET USER ADS] Added ad: " + ad.get("title"));
                                }
                            }
                        }
                    }
                }
            }
            
            // Also check static ads in categoryData for user ownership
            // For demo purposes, we'll assign some static ads to the current user
            if (categoryData != null) {
                System.out.println("[GET USER ADS] Checking static ads in categoryData");
                for (Map.Entry<String, Map<String, Object>> entry : categoryData.entrySet()) {
                    String categoryKey = entry.getKey();
                    Map<String, Object> category = entry.getValue();
                    
                    if (category != null) {
                        List<Map<String, Object>> staticAds = (List<Map<String, Object>>) category.get("ads");
                        
                        if (staticAds != null) {
                            for (int i = 0; i < staticAds.size(); i++) {
                                Map<String, Object> staticAd = staticAds.get(i);
                                
                                if (staticAd != null) {
                                    // For demo purposes, assign some static ads to user ID "1"
                                    // This simulates the user having some existing ads
                                    if (userId.equals("1") && i < 2) { // First 2 ads in each category for demo user
                                        Map<String, Object> adWithCategory = new HashMap<>(staticAd);
                                        adWithCategory.put("categoryKey", categoryKey);
                                        adWithCategory.put("categoryName", getCategoryName(categoryKey));
                                        adWithCategory.put("userId", userId); // Add userId for consistency
                                        userAds.add(adWithCategory);
                                        System.out.println("[GET USER ADS] Added static ad: " + staticAd.get("text"));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            System.out.println("[GET USER ADS] User ID: " + userId + ", Total found ads: " + userAds.size());
            System.out.println("[GET USER ADS] Final userAds: " + userAds);
            return ResponseEntity.ok(userAds);
        } catch (Exception e) {
            e.printStackTrace(); // Add logging for debugging
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch user ads: " + e.getMessage()));
        }
    }

    // Helper method to get category name
    private String getCategoryName(String categoryKey) {
        try {
            if (categoryKey == null) {
                return "Unknown Category";
            }
            
            Map<String, Object> category = categoryData.get(categoryKey);
            if (category != null && category.get("name") != null) {
                return (String) category.get("name");
            }
            return categoryKey;
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to get category name for: " + categoryKey + ", Error: " + e.getMessage());
            return categoryKey != null ? categoryKey : "Unknown Category";
        }
    }

    // New endpoint to delete user's ad
    @DeleteMapping("/{categoryKey}/ads/{adId}")
    public ResponseEntity<?> deleteUserAd(@PathVariable String categoryKey, @PathVariable String adId) {
        try {
            if (!postedAds.containsKey(categoryKey)) {
                return ResponseEntity.notFound().build();
            }
            
            List<Map<String, Object>> ads = postedAds.get(categoryKey);
            boolean removed = ads.removeIf(ad -> adId.equals(ad.get("id").toString()));
            
            if (removed) {
                return ResponseEntity.ok(Map.of("message", "Ad deleted successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{categoryKey}/ads/{adId}")
    public ResponseEntity<?> updateAd(@PathVariable String categoryKey, @PathVariable Long adId, @RequestBody Map<String, Object> body) {
        try {
            // Find the existing ad
            Map<String, Object> category = categoryData.get(categoryKey);
            if (category == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Category not found"));
            }

            List<Map<String, Object>> ads = (List<Map<String, Object>>) category.get("ads");
            Optional<Map<String, Object>> existingAdOpt = ads.stream()
                .filter(ad -> adId.equals(ad.get("id")))
                .findFirst();

            if (!existingAdOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> existingAd = existingAdOpt.get();
            
            // Update the ad fields
            existingAd.put("title", body.get("title"));
            existingAd.put("text", body.get("text"));
            existingAd.put("contact", body.get("contact"));
            
            // Handle images - can be single string or array
            Object imageData = body.get("images");
            if (imageData instanceof List) {
                // If it's an array, join with comma or take first image
                List<?> images = (List<?>) imageData;
                if (!images.isEmpty()) {
                    existingAd.put("image", images.get(0)); // Use first image for now
                    existingAd.put("images", images); // Store all images
                } else {
                    existingAd.put("image", "");
                    existingAd.put("images", new ArrayList<>());
                }
            } else {
                // If it's a single string
                existingAd.put("image", imageData != null ? imageData : "");
                existingAd.put("images", imageData != null ? List.of(imageData) : new ArrayList<>());
            }
            
            existingAd.put("categoryKey", categoryKey);
            
            // Save the updated ad
            // In a real application, you would persist this change to your data store
            // For this example, we'll just return the updated ad
            return ResponseEntity.ok(existingAd);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Failed to update ad: " + e.getMessage()));
        }
    }
} 