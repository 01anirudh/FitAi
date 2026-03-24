package com.fitai.userservice.service;

import com.fitai.userservice.dto.RegisterRequest;
import com.fitai.userservice.dto.UserResopnse;
import com.fitai.userservice.model.User;
import com.fitai.userservice.reposiroty.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
        // If user already synced, return existing record
        return repository.findByKeycloakId(keycloakId)
                .map(this::mapToResponse)
                .orElseGet(() -> {
                    // Create new local user entry linked to Keycloak UUID
                    User user = new User();
                    user.setKeycloakId(keycloakId);
                    user.setEmail(email != null ? email : "");
                    user.setFirstName(firstName != null ? firstName : "");
                    user.setLastName(lastName != null ? lastName : "");
                    user.setPassword(""); // No local password — Keycloak handles auth

                    User savedUser = repository.save(user);
                    log.info("Synced new Keycloak user: keycloakId={}, email={}", keycloakId, email);
                    return mapToResponse(savedUser);
                });
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
