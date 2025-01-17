package com.dan.sd.device_management.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
public class DeviceDetailsDTO {
    @NotNull
    private String description;
    @NotNull
    private String address;
    @NotNull
    private Float maxHourlyConsumption;
    @NotNull
    private UUID userId;

    public DeviceDetailsDTO(String description, String address, Float maxHourlyConsumption, UUID userId) {
        this.description = description;
        this.address = address;
        this.maxHourlyConsumption = maxHourlyConsumption;
        this.userId = userId;
    }
}
