package com.dan.sd.device_management.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Getter
@Setter
public class User implements Serializable {
    @Id
    private UUID id;

    public User() {
    }

    public User(UUID id) {
        this.id = id;
    }
}
