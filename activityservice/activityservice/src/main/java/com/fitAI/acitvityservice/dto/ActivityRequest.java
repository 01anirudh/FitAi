package com.fitAI.acitvityservice.dto;

import com.fitAI.acitvityservice.model.ActivityType;

import java.time.LocalDateTime;
import java.util.Map;

public class ActivityRequest {
    private String userId;
    private ActivityType type;
    private Integer duration;
    private Integer caloriesBurned;
    private LocalDateTime startTime;
    private Map<String, Object> additionalMetrics;

    public String getUserId() { return userId; }
    public ActivityType getType() { return type; }
    public Integer getDuration() { return duration; }
    public Integer getCaloriesBurned() { return caloriesBurned; }
    public LocalDateTime getStartTime() { return startTime; }
    public Map<String, Object> getAdditionalMetrics() { return additionalMetrics; }

    public void setUserId(String userId) { this.userId = userId; }
    public void setType(ActivityType type) { this.type = type; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public void setCaloriesBurned(Integer caloriesBurned) { this.caloriesBurned = caloriesBurned; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public void setAdditionalMetrics(Map<String, Object> additionalMetrics) { this.additionalMetrics = additionalMetrics; }
}
