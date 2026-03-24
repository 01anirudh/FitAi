package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityMessageListener {

    private final RecommendationRepository recommendationRepository;
    private final GeminiAIService geminiAIService;

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
            Recommendation recommendation = Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .activityType(activity.getType())
                    .recommendation(parsed.recommendation())
                    .improvements(parsed.improvements())
                    .suggestions(parsed.suggestions())
                    .safety(parsed.safety())
                    .createdAt(LocalDateTime.now())
                    .build();

            recommendationRepository.save(recommendation);
            log.info("Saved recommendation for activityId={}", activity.getId());

        } catch (Exception e) {
            log.error("Error processing activity message for activityId={}: {}",
                    activity.getId(), e.getMessage(), e);
        }
    }
}
