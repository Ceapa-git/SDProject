package com.dan.sd.device_management.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserDTO {
    private UUID id;

    public UserDTO() {
    }

    public UserDTO(UUID id) {
        this.id = id;
    }
}
