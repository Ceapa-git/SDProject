package com.dan.sd.device_management.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeviceDTO {
    private UUID id;
    private String description;
    private String address;
    private Float maxHourlyConsumption;
    private UUID userId;

    public DeviceDTO(UUID id, String description, String address, Float maxHourlyConsumption, UUID userId) {
        this.id = id;
        this.description = description;
        this.address = address;
        this.maxHourlyConsumption = maxHourlyConsumption;
        this.userId = userId;
    }
}
