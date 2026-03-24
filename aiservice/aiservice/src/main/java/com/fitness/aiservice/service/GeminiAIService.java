package com.fitness.aiservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GeminiAIService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private static final String GEMINI_API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateRecommendation(String prompt) {
        try {
            RestClient restClient = RestClient.create();

            // Build the request body for Gemini API
            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", prompt)
                            ))
                    ),
                    "generationConfig", Map.of(
                            "temperature", 0.7,
                            "maxOutputTokens", 1024
                    )
            );

            String response = restClient.post()
                    .uri(GEMINI_API_URL + "?key=" + geminiApiKey)
                    .header("Content-Type", "application/json")
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            return extractTextFromResponse(response);

        } catch (Exception e) {
            log.error("Error calling Gemini API: {}", e.getMessage(), e);
            return null;
        }
    }

    private String extractTextFromResponse(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            return root
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();
        } catch (Exception e) {
            log.error("Error parsing Gemini response: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Build a structured fitness recommendation prompt from activity data.
     */
    public String buildFitnessPrompt(String activityType, int duration, int caloriesBurned,
                                      Map<String, Object> additionalMetrics) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an expert fitness coach and trainer. ");
        prompt.append("Analyze the following workout activity and provide detailed, personalized recommendations.\n\n");
        prompt.append("Activity Details:\n");
        prompt.append("- Type: ").append(activityType).append("\n");
        prompt.append("- Duration: ").append(duration).append(" minutes\n");
        prompt.append("- Calories Burned: ").append(caloriesBurned).append(" kcal\n");

        if (additionalMetrics != null && !additionalMetrics.isEmpty()) {
            prompt.append("- Additional Metrics: ").append(additionalMetrics).append("\n");
        }

        prompt.append("\nPlease provide your response in the following JSON format only, no extra text:\n");
        prompt.append("{\n");
        prompt.append("  \"recommendation\": \"<overall summary recommendation>\",\n");
        prompt.append("  \"improvements\": [\"<improvement 1>\", \"<improvement 2>\", \"<improvement 3>\"],\n");
        prompt.append("  \"suggestions\": [\"<next workout suggestion 1>\", \"<suggestion 2>\", \"<suggestion 3>\"],\n");
        prompt.append("  \"safety\": [\"<safety tip 1>\", \"<safety tip 2>\"]\n");
        prompt.append("}");

        return prompt.toString();
    }

    /**
     * Parse structured JSON response from Gemini into recommendation fields.
     */
    public ParsedRecommendation parseRecommendationResponse(String rawResponse) {
        if (rawResponse == null) {
            return createDefaultRecommendation();
        }

        try {
            // Extract JSON from response (remove markdown code blocks if present)
            String jsonStr = rawResponse.trim();
            if (jsonStr.startsWith("```")) {
                jsonStr = jsonStr.replaceAll("```json|```", "").trim();
            }

            JsonNode node = objectMapper.readTree(jsonStr);

            String recommendation = node.path("recommendation").asText("Keep up the great work!");

            List<String> improvements = new ArrayList<>();
            node.path("improvements").forEach(item -> improvements.add(item.asText()));

            List<String> suggestions = new ArrayList<>();
            node.path("suggestions").forEach(item -> suggestions.add(item.asText()));

            List<String> safety = new ArrayList<>();
            node.path("safety").forEach(item -> safety.add(item.asText()));

            return new ParsedRecommendation(recommendation, improvements, suggestions, safety);

        } catch (Exception e) {
            log.error("Error parsing recommendation JSON: {}", e.getMessage());
            return createDefaultRecommendation();
        }
    }

    private ParsedRecommendation createDefaultRecommendation() {
        return new ParsedRecommendation(
                "Great workout! Keep maintaining your fitness routine.",
                List.of("Focus on proper form", "Gradually increase intensity", "Track your progress regularly"),
                List.of("Try interval training", "Add strength training to complement cardio", "Consider yoga for flexibility"),
                List.of("Stay hydrated", "Warm up before and cool down after each session")
        );
    }

    public record ParsedRecommendation(
            String recommendation,
            List<String> improvements,
            List<String> suggestions,
            List<String> safety
    ) {}
}
