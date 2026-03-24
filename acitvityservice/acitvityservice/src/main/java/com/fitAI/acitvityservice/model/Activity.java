package com.fitAI.acitvityservice.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "activities")
public class Activity {
    private String id;
    private String userId;
    private ActivityType type;
    private Integer duration;
    private Integer caloriesBurned;
    private LocalDateTime startTime;

    @Field("metrics")
    private Map<String, Object> additionalMetrics;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public Activity() {}

    public Activity(String id, String userId, ActivityType type, Integer duration,
                    Integer caloriesBurned, LocalDateTime startTime,
                    Map<String, Object> additionalMetrics,
                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.duration = duration;
        this.caloriesBurned = caloriesBurned;
        this.startTime = startTime;
        this.additionalMetrics = additionalMetrics;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public ActivityType getType() { return type; }
    public Integer getDuration() { return duration; }
    public Integer getCaloriesBurned() { return caloriesBurned; }
    public LocalDateTime getStartTime() { return startTime; }
    public Map<String, Object> getAdditionalMetrics() { return additionalMetrics; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setType(ActivityType type) { this.type = type; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public void setCaloriesBurned(Integer caloriesBurned) { this.caloriesBurned = caloriesBurned; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public void setAdditionalMetrics(Map<String, Object> additionalMetrics) { this.additionalMetrics = additionalMetrics; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String id;
        private String userId;
        private ActivityType type;
        private Integer duration;
        private Integer caloriesBurned;
        private LocalDateTime startTime;
        private Map<String, Object> additionalMetrics;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder userId(String userId) { this.userId = userId; return this; }
        public Builder type(ActivityType type) { this.type = type; return this; }
        public Builder duration(Integer duration) { this.duration = duration; return this; }
        public Builder caloriesBurned(Integer caloriesBurned) { this.caloriesBurned = caloriesBurned; return this; }
        public Builder startTime(LocalDateTime startTime) { this.startTime = startTime; return this; }
        public Builder additionalMetrics(Map<String, Object> additionalMetrics) { this.additionalMetrics = additionalMetrics; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Activity build() {
            return new Activity(id, userId, type, duration, caloriesBurned,
                    startTime, additionalMetrics, createdAt, updatedAt);
        }
    }
}
