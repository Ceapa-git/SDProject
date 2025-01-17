package com.dan.sd.chat.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class MessageDetailsDTO {
    @NotNull
    private String text;
    @NotNull
    private UUID senderId;
    @NotNull
    private UUID receiverId;

    public MessageDetailsDTO(String text, UUID senderId, UUID receiverId) {
        this.text = text;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }
}
