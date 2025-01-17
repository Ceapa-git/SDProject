package com.dan.sd.user_management.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthDTO {
    private String username;
    private String password;

    public AuthDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
