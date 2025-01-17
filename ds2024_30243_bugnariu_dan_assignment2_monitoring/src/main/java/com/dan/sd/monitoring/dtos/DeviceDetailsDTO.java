package com.dan.sd.monitoring.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
public class DeviceDetailsDTO {
    @NotNull
    private UUID id;
    @NotNull
    private Float maxHourlyConsumption;
    @NotNull
    private UUID userId;

    public DeviceDetailsDTO(UUID id, Float maxHourlyConsumption, UUID userId) {
        this.id = id;
        this.maxHourlyConsumption = maxHourlyConsumption;
        this.userId = userId;
    }
}
