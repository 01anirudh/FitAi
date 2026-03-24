package com.fitAI.acitvityservice.controller;

import com.fitAI.acitvityservice.dto.ActivityResponse;
import com.fitAI.acitvityservice.dto.ActivityRequest;
import com.fitAI.acitvityservice.service.AcitvityService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private final AcitvityService activityService;

    public ActivityController(AcitvityService activityService) {
        this.activityService = activityService;
    }

    @PostMapping
    public ResponseEntity<ActivityResponse> trackAcitvity(
            @RequestBody ActivityRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        request.setUserId(jwt.getSubject());
        return ResponseEntity.ok(activityService.trackActivity(request));
    }

    @GetMapping
    public ResponseEntity<List<ActivityResponse>> getUserActivities(
            @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(activityService.getUserActivities(userId));
    }

    @GetMapping("/{activityId}")
    public ResponseEntity<ActivityResponse> getUserActivity(@PathVariable String activityId) {
        return ResponseEntity.ok(activityService.getUserActivityById(activityId));
    }
}
