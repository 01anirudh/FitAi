package com.fitai.userservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResopnse {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
