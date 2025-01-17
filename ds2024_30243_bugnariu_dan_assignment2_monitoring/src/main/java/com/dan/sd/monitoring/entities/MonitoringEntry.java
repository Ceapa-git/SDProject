package com.dan.sd.monitoring.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Getter
@Setter
public class MonitoringEntry implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "device_id")
    private Device deviceId;

    @Column(nullable = false)
    private Float measurementValue;

    @Column(nullable = false)
    private Long timestamp;

    public MonitoringEntry() {
    }

    public MonitoringEntry(Device deviceId, Float measurementValue, Long timestamp) {
        this.deviceId = deviceId;
        this.measurementValue = measurementValue;
        this.timestamp = timestamp;
    }
}
