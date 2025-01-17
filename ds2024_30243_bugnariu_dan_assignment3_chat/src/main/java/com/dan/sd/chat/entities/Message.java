package com.dan.sd.chat.entities;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Message implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private UUID senderId;
    private UUID receiverId;
    private Date timestamp;
    private String text;
    private Boolean seen;

    public Message() {
    }

    public Message(UUID id, UUID senderId, UUID receiverId, Date timestamp, String text, Boolean seen) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.timestamp = timestamp;
        this.text = text;
        this.seen = seen;
    }
}
