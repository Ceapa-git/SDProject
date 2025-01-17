package com.dan.sd.chat.entities;

import com.dan.sd.chat.entities.keys.WritingId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Getter
@Setter
@IdClass(WritingId.class)
public class Writing implements Serializable {
    @Id
    private UUID senderId;
    @Id
    private UUID receiverId;

    public Writing() {
    }

    public Writing(UUID senderId, UUID receiverId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
    }
}
