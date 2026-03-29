package com.fitai.userservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class KeycloakAdminService {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Value("${keycloak.admin.password:admin}")
    private String adminPassword;

    private final RestTemplate restTemplate = new RestTemplate();

    /** Derives https://keycloak-host from the issuer URI */
    private String baseUrl() {
        int idx = issuerUri.indexOf("/realms/");
        return idx > 0 ? issuerUri.substring(0, idx) : issuerUri;
    }

    /** Derives realm name from the issuer URI */
    private String realm() {
        int idx = issuerUri.indexOf("/realms/");
        return idx > 0 ? issuerUri.substring(idx + 8) : "master";
    }

    /** Obtains a short-lived admin access token from the master realm */
    private String adminToken() {
        String url = baseUrl() + "/realms/master/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", "admin-cli");
        body.add("username", "admin");
        body.add("password", adminPassword);

        ResponseEntity<Map<String, Object>> res = restTemplate.postForEntity(
                url, new HttpEntity<>(body, headers), (Class<Map<String, Object>>)(Class<?>)Map.class);
        
        Map<String, Object> responseBody = res.getBody();
        if (responseBody == null || !responseBody.containsKey("access_token")) {
            throw new RuntimeException("Failed to get admin token from Keycloak");
        }
        return (String) responseBody.get("access_token");
    }

    /**
     * Creates a user in Keycloak via the Admin REST API.
     * Throws RuntimeException with a user-friendly message on failure.
     */
    public void createUser(String username, String email,
                           String firstName, String lastName, String password) {
        String token = adminToken();

        Map<String, Object> credential = new HashMap<>();
        credential.put("type", "password");
        credential.put("value", password);
        credential.put("temporary", false);

        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("email", email);
        user.put("firstName", firstName);
        user.put("lastName", lastName);
        user.put("enabled", true);
        user.put("emailVerified", true);
        user.put("credentials", List.of(credential));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            restTemplate.postForEntity(
                    baseUrl() + "/admin/realms/" + realm() + "/users",
                    new HttpEntity<>(user, headers),
                    Void.class
            );
            log.info("Created Keycloak user: username={}, email={}", username, email);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                throw new RuntimeException("Username or email is already taken");
            }
            log.error("Keycloak user creation failed: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Registration failed. Please try again.");
        }
    }
}
