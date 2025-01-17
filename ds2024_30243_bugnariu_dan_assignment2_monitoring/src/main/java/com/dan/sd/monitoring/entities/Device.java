package com.dan.sd.monitoring.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Device implements Serializable {
    @Id
    private UUID id;

    @Column(nullable = false)
    private Float maxHourlyConsumption;

    @Column(nullable = false)
    private UUID userId;

    public Device() {
    }

    public Device(UUID id, Float maxHourlyConsumption, UUID userId) {
        this.id = id;
        this.maxHourlyConsumption = maxHourlyConsumption;
        this.userId = userId;
    }
}
