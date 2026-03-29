package com.fitai.userservice.dto;

import lombok.Data;

@Data
public class KeycloakRegisterRequest {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
}
