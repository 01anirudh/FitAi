package com.fitAI.acitvityservice.dto;

import com.fitAI.acitvityservice.model.ActivityType;

import java.time.LocalDateTime;
import java.util.Map;

public class ActivityResponse {
    private String id;
    private String userId;
    private ActivityType type;
    private Integer duration;
    private Integer caloriesBurned;
    private LocalDateTime startTime;
    private Map<String, Object> additionalMetrics;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public ActivityType getType() { return type; }
    public Integer getDuration() { return duration; }
    public Integer getCaloriesBurned() { return caloriesBurned; }
    public LocalDateTime getStartTime() { return startTime; }
    public Map<String, Object> getAdditionalMetrics() { return additionalMetrics; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setId(String id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setType(ActivityType type) { this.type = type; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public void setCaloriesBurned(Integer caloriesBurned) { this.caloriesBurned = caloriesBurned; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public void setAdditionalMetrics(Map<String, Object> additionalMetrics) { this.additionalMetrics = additionalMetrics; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
