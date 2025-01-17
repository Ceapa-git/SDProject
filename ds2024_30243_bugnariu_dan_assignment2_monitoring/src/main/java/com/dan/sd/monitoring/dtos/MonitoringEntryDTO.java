package com.dan.sd.monitoring.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class MonitoringEntryDTO {
    @NotNull
    private Float measurementValue;
    @NotNull
    private Long timestamp;

    public MonitoringEntryDTO(Float measurementValue, Long timestamp) {
        this.measurementValue = measurementValue;
        this.timestamp = timestamp;
    }
}
