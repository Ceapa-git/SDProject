package com.dan.sd.chat.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class MessageDTO {
    private String text;
    private UUID senderId;
    private UUID receiverId;
    private Date timestamp;
    private Boolean seen;

    public MessageDTO(String text, UUID senderId, UUID receiverId, Date timestamp, Boolean seen) {
        this.text = text;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.timestamp = timestamp;
        this.seen = seen;
    }
}
