package com.dan.sd.user_management.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UserDetailsDTO {
    @NotNull
    private String role;
    @NotNull
    private String username;
    @NotNull
    private String password;

    public UserDetailsDTO(String role, String username, String password) {
        this.role = role;
        this.username = username;
        this.password = password;
    }
}
