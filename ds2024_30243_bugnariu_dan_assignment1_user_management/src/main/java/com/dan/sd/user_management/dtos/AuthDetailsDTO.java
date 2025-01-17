package com.dan.sd.user_management.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AuthDetailsDTO {
    private UUID id;
    private String username;
    private String role;

    public AuthDetailsDTO(UUID id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }
}
