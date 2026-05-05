package com.fitness.aiservice.controller;

import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.service.RecommendationService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {
    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Recommendation>> getUserRecommendation(@PathVariable String userId) {
        List<Recommendation> recs = recommendationService.getUserRecommendation(userId);
        return ResponseEntity.ok(recs != null ? recs : List.of());
    }

    @GetMapping("/activity/{activityId}")
    public ResponseEntity<Recommendation> getActivityRecommendation(@PathVariable String activityId) {
        try {
            Recommendation rec = recommendationService.getActivityRecommendation(activityId);
            if (rec == null) {
                return ResponseEntity.notFound().build(); // 404 — AI still processing
            }
            return ResponseEntity.ok(rec);
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // 404 instead of 500
        }
    }
}
