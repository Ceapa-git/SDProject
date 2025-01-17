package com.dan.sd.device_management.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeviceUpdateDTO {
    private String description;
    private String address;
    private Float maxHourlyConsumption;
    private UUID userId;

    public DeviceUpdateDTO(String description, String address, Float maxHourlyConsumption, UUID userId) {
        this.description = description;
        this.address = address;
        this.maxHourlyConsumption = maxHourlyConsumption;
        this.userId = userId;
    }
}
