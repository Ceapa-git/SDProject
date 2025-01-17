package com.dan.sd.chat.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class WritingDTO {
    private UUID senderId;
    private UUID receiverId;

    public WritingDTO(UUID senderId, UUID receiverId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
    }
}
