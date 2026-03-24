package com.fitai.userservice.controller;

import com.fitai.userservice.dto.RegisterRequest;
import com.fitai.userservice.dto.UserResopnse;
import com.fitai.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserResopnse> getUserProfile(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResopnse> registerUser(@Valid @RequestBody RegisterRequest request) {
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
    public ResponseEntity<UserResopnse> syncKeycloakUser(
            @AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        String firstName = jwt.getClaimAsString("given_name");
        String lastName = jwt.getClaimAsString("family_name");

        return ResponseEntity.ok(
                userService.syncKeycloakUser(keycloakId, email, firstName, lastName)
        );
    }
}
