package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.repository.RecommendationRepository;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ActivityMessageListener {

    private static final Logger log = LoggerFactory.getLogger(ActivityMessageListener.class);

    private final RecommendationRepository recommendationRepository;
    private final GeminiAIService geminiAIService;

    public ActivityMessageListener(RecommendationRepository recommendationRepository, GeminiAIService geminiAIService) {
        this.recommendationRepository = recommendationRepository;
        this.geminiAIService = geminiAIService;
    }

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void receiveActivityMessage(Activity activity) {
        log.info("Received activity from RabbitMQ: activityId={}, userId={}, type={}",
                activity.getId(), activity.getUserId(), activity.getType());

        try {
            // Build prompt for Gemini
            String prompt = geminiAIService.buildFitnessPrompt(
                    activity.getType(),
                    activity.getDuration(),
                    activity.getCaloriesBurned(),
                    activity.getAdditionalMetrics()
            );

            // Call Gemini API
            String rawAiResponse = geminiAIService.generateRecommendation(prompt);
            log.info("Received AI response for activityId={}", activity.getId());

            // Parse the structured response
            GeminiAIService.ParsedRecommendation parsed =
                    geminiAIService.parseRecommendationResponse(rawAiResponse);

            // Save recommendation to MongoDB
            Recommendation recommendation = new Recommendation();
            recommendation.setActivityId(activity.getId());
            recommendation.setUserId(activity.getUserId());
            recommendation.setActivityType(activity.getType());
            recommendation.setRecommendation(parsed.recommendation());
            recommendation.setImprovements(parsed.improvements());
            recommendation.setSuggestions(parsed.suggestions());
            recommendation.setSafety(parsed.safety());
            recommendation.setCreatedAt(LocalDateTime.now());

            recommendationRepository.save(recommendation);
            log.info("Saved recommendation for activityId={}", activity.getId());

        } catch (Exception e) {
            log.error("Error processing activity message for activityId={}: {}",
                    activity.getId(), e.getMessage(), e);
        }
    }
}
