package com.dan.sd.monitoring.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
public class MonitoringEntryDetailsDTO {
    @NotNull
    private UUID deviceId;
    @NotNull
    private Float measurementValue;
    @NotNull
    private Long timestamp;

    public MonitoringEntryDetailsDTO(UUID deviceId, Float measurementValue, Long timestamp) {
        this.deviceId = deviceId;
        this.measurementValue = measurementValue;
        this.timestamp = timestamp;
    }
}
