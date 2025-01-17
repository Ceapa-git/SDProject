package com.dan.sd.chat.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserDTO {
    private UUID id;

    public UserDTO(UUID id) {
        this.id = id;
    }
}
