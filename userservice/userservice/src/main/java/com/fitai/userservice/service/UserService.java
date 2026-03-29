package com.fitai.userservice.service;

import com.fitai.userservice.dto.RegisterRequest;
import com.fitai.userservice.dto.UserResopnse;
import com.fitai.userservice.model.User;
import com.fitai.userservice.reposiroty.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository repository;

    public UserResopnse registerUser(@Valid RegisterRequest request) {

        if (repository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        User savedUser = repository.save(user);
        return mapToResponse(savedUser);
    }

    public UserResopnse getUserProfile(String userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));
        return mapToResponse(user);
    }

    public Boolean existByUserId(String userId) {
        return repository.existsByKeycloakId(userId);
    }

    /**
     * Sync a Keycloak user into the local database.
     * Uses the Keycloak subject UUID as the user's local ID so that
     * activity-service and ai-service can reference users by Keycloak ID.
     */
    public UserResopnse syncKeycloakUser(String keycloakId, String email,
                                          String firstName, String lastName) {
        // Case 1: User already synced via Keycloak — return existing record
        Optional<User> byKeycloakId = repository.findByKeycloakId(keycloakId);
        if (byKeycloakId.isPresent()) {
            return mapToResponse(byKeycloakId.get());
        }

        // Case 2: User registered manually before (via /register) — link their keycloakId
        if (email != null && !email.isBlank()) {
            Optional<User> byEmail = repository.findByEmail(email);
            if (byEmail.isPresent()) {
                User existing = byEmail.get();
                existing.setKeycloakId(keycloakId);
                if (firstName != null && !firstName.isBlank()) existing.setFirstName(firstName);
                if (lastName != null && !lastName.isBlank()) existing.setLastName(lastName);
                log.info("Linked keycloakId={} to existing user email={}", keycloakId, email);
                return mapToResponse(repository.save(existing));
            }
        }

        // Case 3: Completely new user — create local record
        User user = new User();
        user.setKeycloakId(keycloakId);
        user.setEmail(email != null && !email.isBlank() ? email : keycloakId + "@keycloak.local");
        user.setFirstName(firstName != null ? firstName : "");
        user.setLastName(lastName != null ? lastName : "");
        user.setPassword(""); // Auth handled by Keycloak

        User savedUser = repository.save(user);
        log.info("Created new user from Keycloak sync: keycloakId={}, email={}", keycloakId, email);
        return mapToResponse(savedUser);
    }

    private UserResopnse mapToResponse(User user) {
        UserResopnse response = new UserResopnse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
}
