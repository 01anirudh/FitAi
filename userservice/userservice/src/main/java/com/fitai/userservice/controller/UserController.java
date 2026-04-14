package com.fitai.userservice.controller;

import com.fitai.userservice.dto.KeycloakRegisterRequest;
import com.fitai.userservice.dto.RegisterRequest;
import com.fitai.userservice.dto.UserResponse;
import com.fitai.userservice.service.KeycloakAdminService;
import com.fitai.userservice.service.UserService;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserService userService;
    private KeycloakAdminService keycloakAdminService;

    public UserController(UserService userService, KeycloakAdminService keycloakAdminService) {
        this.userService = userService;
        this.keycloakAdminService = keycloakAdminService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    /**
     * @deprecated Internal/legacy endpoint — use /keycloak-register for new registrations.
     * Requires JWT to prevent unauthenticated access.
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.registerUser(request));
    }

    @GetMapping("/{userId}/validate")
    public ResponseEntity<Boolean> validateUser(@PathVariable String userId) {
        return ResponseEntity.ok(userService.existByUserId(userId));
    }

    /**
     * Sync authenticated Keycloak user into local DB.
     * Call this after login to ensure the user exists in user-service.
     * The Keycloak subject (UUID) is used as the stored userId.
     */
    @PostMapping("/sync")
    public ResponseEntity<UserResponse> syncKeycloakUser(
            @AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        String firstName = jwt.getClaimAsString("given_name");
        String lastName = jwt.getClaimAsString("family_name");

        return ResponseEntity.ok(
                userService.syncKeycloakUser(keycloakId, email, firstName, lastName)
        );
    }
    /**
     * Public endpoint: create a new user in Keycloak (no JWT required).
     * The local DB record is created automatically on first /sync after login.
     */
    @PostMapping("/keycloak-register")
    public ResponseEntity<java.util.Map<String, String>> keycloakRegister(
            @RequestBody KeycloakRegisterRequest request) {
        keycloakAdminService.createUser(
                request.getUsername(),
                request.getEmail(),
                request.getFirstName(),
                request.getLastName(),
                request.getPassword()
        );
        return ResponseEntity.ok(java.util.Map.of("message", "Account created successfully"));
    }
}
