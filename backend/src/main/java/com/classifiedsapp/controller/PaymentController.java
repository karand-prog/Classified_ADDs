package com.classifiedsapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class PaymentController {

    private final Map<String, Map<String, Object>> paymentIntentsById = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Object>> subscriptionsByUser = new ConcurrentHashMap<>();

    private boolean isStripeEnabled() {
        String key = System.getenv("STRIPE_SECRET");
        if (key == null || key.isBlank()) {
            key = System.getProperty("STRIPE_SECRET");
        }
        return key != null && !key.isBlank();
    }

    private String getStripeKey() {
        String key = System.getenv("STRIPE_SECRET");
        if (key == null || key.isBlank()) {
            key = System.getProperty("STRIPE_SECRET");
        }
        return key;
    }

    @PostMapping("/payments/create-intent")
    public ResponseEntity<?> createPaymentIntent(@RequestBody Map<String, Object> body) throws Exception {
        String amountStr = String.valueOf(body.getOrDefault("amount", "0"));
        String currency = String.valueOf(body.getOrDefault("currency", "USD")).toLowerCase();
        String userId = String.valueOf(body.getOrDefault("userId", ""));

        if (isStripeEnabled()) {
            com.stripe.Stripe.apiKey = getStripeKey();
            long amountMinor = (long) Math.round(Double.parseDouble(amountStr) * 100L);
            Map<String, Object> params = new HashMap<>();
            params.put("amount", amountMinor);
            params.put("currency", currency);
            params.put("automatic_payment_methods", Map.of("enabled", true));
            com.stripe.model.PaymentIntent pi = com.stripe.model.PaymentIntent.create(params);
            Map<String, Object> resp = new HashMap<>();
            resp.put("id", pi.getId());
            resp.put("amount", amountStr);
            resp.put("currency", currency);
            resp.put("userId", userId);
            resp.put("status", pi.getStatus());
            resp.put("clientSecret", pi.getClientSecret());
            return ResponseEntity.ok(resp);
        }

        String intentId = UUID.randomUUID().toString();
        Map<String, Object> intent = new HashMap<>();
        intent.put("id", intentId);
        intent.put("amount", amountStr);
        intent.put("currency", currency);
        intent.put("userId", userId);
        intent.put("status", "requires_confirmation");
        intent.put("createdAt", LocalDateTime.now().toString());
        paymentIntentsById.put(intentId, intent);
        return ResponseEntity.ok(intent);
    }

    @PostMapping("/payments/confirm")
    public ResponseEntity<?> confirmPayment(@RequestBody Map<String, Object> body) throws Exception {
        String intentId = String.valueOf(body.getOrDefault("intentId", ""));
        if (isStripeEnabled()) {
            com.stripe.Stripe.apiKey = getStripeKey();
            com.stripe.model.PaymentIntent pi = com.stripe.model.PaymentIntent.retrieve(intentId);
            Map<String, Object> params = new HashMap<>();
            com.stripe.model.PaymentIntent confirmed = pi.confirm(params);
            Map<String, Object> resp = new HashMap<>();
            resp.put("id", confirmed.getId());
            resp.put("status", confirmed.getStatus());
            resp.put("clientSecret", confirmed.getClientSecret());
            return ResponseEntity.ok(resp);
        }
        Map<String, Object> intent = paymentIntentsById.get(intentId);
        if (intent == null) return ResponseEntity.notFound().build();
        intent.put("status", "succeeded");
        intent.put("confirmedAt", LocalDateTime.now().toString());
        return ResponseEntity.ok(intent);
    }

    @GetMapping("/payments/{intentId}")
    public ResponseEntity<?> getPayment(@PathVariable String intentId) throws Exception {
        if (isStripeEnabled()) {
            com.stripe.Stripe.apiKey = getStripeKey();
            com.stripe.model.PaymentIntent pi = com.stripe.model.PaymentIntent.retrieve(intentId);
            Map<String, Object> resp = new HashMap<>();
            resp.put("id", pi.getId());
            resp.put("status", pi.getStatus());
            resp.put("amount", pi.getAmount());
            resp.put("currency", pi.getCurrency());
            return ResponseEntity.ok(resp);
        }
        Map<String, Object> intent = paymentIntentsById.get(intentId);
        if (intent == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(intent);
    }

    // Subscriptions (simple mock switch; real-world would use Stripe Billing)
    @PostMapping("/subscriptions/create")
    public ResponseEntity<?> createSubscription(@RequestBody Map<String, Object> body) throws Exception {
        String userId = String.valueOf(body.getOrDefault("userId", ""));
        String plan = String.valueOf(body.getOrDefault("plan", "pro"));
        String currency = String.valueOf(body.getOrDefault("currency", "USD"));
        if (isStripeEnabled()) {
            // For now, return a pseudo-active subscription; can be expanded to Billing
            Map<String, Object> sub = new HashMap<>();
            sub.put("id", "sub_" + UUID.randomUUID());
            sub.put("userId", userId);
            sub.put("plan", plan);
            sub.put("currency", currency);
            sub.put("status", "active");
            sub.put("createdAt", LocalDateTime.now().toString());
            return ResponseEntity.ok(sub);
        }
        String subId = UUID.randomUUID().toString();
        Map<String, Object> sub = new HashMap<>();
        sub.put("id", subId);
        sub.put("userId", userId);
        sub.put("plan", plan);
        sub.put("currency", currency);
        sub.put("status", "active");
        sub.put("createdAt", LocalDateTime.now().toString());
        subscriptionsByUser.put(userId, sub);
        return ResponseEntity.ok(sub);
    }

    @PostMapping("/subscriptions/cancel")
    public ResponseEntity<?> cancelSubscription(@RequestBody Map<String, Object> body) throws Exception {
        String userId = String.valueOf(body.getOrDefault("userId", ""));
        if (isStripeEnabled()) {
            Map<String, Object> sub = new HashMap<>();
            sub.put("id", "sub_fake");
            sub.put("userId", userId);
            sub.put("status", "canceled");
            sub.put("canceledAt", LocalDateTime.now().toString());
            return ResponseEntity.ok(sub);
        }
        Map<String, Object> sub = subscriptionsByUser.get(userId);
        if (sub == null) return ResponseEntity.notFound().build();
        sub.put("status", "canceled");
        sub.put("canceledAt", LocalDateTime.now().toString());
        return ResponseEntity.ok(sub);
    }

    @GetMapping("/subscriptions/{userId}")
    public ResponseEntity<?> getSubscription(@PathVariable String userId) {
        Map<String, Object> sub = subscriptionsByUser.get(userId);
        if (sub == null) return ResponseEntity.ok(Map.of("status", "none"));
        return ResponseEntity.ok(sub);
    }
}
