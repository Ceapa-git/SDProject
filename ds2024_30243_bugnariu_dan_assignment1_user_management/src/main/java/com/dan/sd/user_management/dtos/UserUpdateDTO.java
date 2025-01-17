package com.dan.sd.user_management.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDTO {
    private String role;
    private String username;
    private String password;

    public UserUpdateDTO(String role, String username, String password) {
        this.role = role;
        this.username = username;
        this.password = password;
    }
}
