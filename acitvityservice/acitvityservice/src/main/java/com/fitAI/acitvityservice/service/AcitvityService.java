package com.fitAI.acitvityservice.service;

import com.fitAI.acitvityservice.ActivityRepository;
import com.fitAI.acitvityservice.dto.ActivityResponse;
import com.fitAI.acitvityservice.dto.ActivityRequest;
import com.fitAI.acitvityservice.model.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AcitvityService {

    private static final Logger log = LoggerFactory.getLogger(AcitvityService.class);

    private final ActivityRepository activityRepository;
    private final UserValidationService userValidationService;
    private final RabbitTemplate rabbitTemplate;

    public AcitvityService(ActivityRepository activityRepository,
                           UserValidationService userValidationService,
                           RabbitTemplate rabbitTemplate) {
        this.activityRepository = activityRepository;
        this.userValidationService = userValidationService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    public ActivityResponse trackActivity(ActivityRequest request) {

        boolean isValidUser = userValidationService.validateUser(request.getUserId());

        if (!isValidUser) {
            throw new RuntimeException("Invalid User: " + request.getUserId());
        }

        int calories = (request.getCaloriesBurned() != null && request.getCaloriesBurned() > 0) 
                ? request.getCaloriesBurned() 
                : calculateCaloriesBurned(String.valueOf(request.getType()), request.getDuration());

        Activity activity = Activity.builder()
                .userId(request.getUserId())
                .type(request.getType())
                .duration(request.getDuration())
                .caloriesBurned(calories)
                .startTime(request.getStartTime())
                .additionalMetrics(request.getAdditionalMetrics())
                .build();

        Activity savedActivity = activityRepository.save(activity);

        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, savedActivity);
        } catch (Exception e) {
            log.error("Failed to publish activity to rabbitMQ: ", e);
        }

        return mapToResponse(savedActivity);
    }

    private int calculateCaloriesBurned(String type, Integer duration) {
        if (type == null || duration == null || duration <= 0) return 0;
        
        double metValue = 5.0; // Default MET for general activity
        String t = type.toLowerCase();
        
        if (t.contains("run") || t.contains("jog")) {
            metValue = 9.8;
        } else if (t.contains("cycl") || t.contains("bik")) {
            metValue = 7.5;
        } else if (t.contains("swim")) {
            metValue = 8.0;
        } else if (t.contains("walk")) {
            metValue = 3.5;
        } else if (t.contains("yoga") || t.contains("pilat")) {
            metValue = 2.5;
        } else if (t.contains("weight") || t.contains("lift") || t.contains("strength")) {
            metValue = 6.0;
        } else if (t.contains("hiit") || t.contains("cross")) {
            metValue = 8.0;
        }
        
        // Formula: Calories = MET * Weight(kg) * (Duration / 60)
        // Using average weight of 70kg as baseline
        double weightKg = 70.0;
        
        return (int) (metValue * weightKg * ((double) duration / 60.0));
    }

    private ActivityResponse mapToResponse(Activity activity) {
        ActivityResponse response = new ActivityResponse();
        response.setId(activity.getId());
        response.setUserId(activity.getUserId());
        response.setType(activity.getType());
        response.setDuration(activity.getDuration());
        response.setCaloriesBurned(activity.getCaloriesBurned());
        response.setAdditionalMetrics(activity.getAdditionalMetrics());
        response.setStartTime(activity.getStartTime());
        response.setCreatedAt(activity.getCreatedAt());
        response.setUpdatedAt(activity.getUpdatedAt());
        return response;
    }

    public List<ActivityResponse> getUserActivities(String userId) {
        List<Activity> activities = activityRepository.findByUserId(userId);
        return activities.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ActivityResponse getUserActivityById(String activityId) {
        return activityRepository.findById(activityId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Activity Not found with id :" + activityId));
    }
}
