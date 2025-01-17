package com.dan.sd.monitoring.dtos.builders;

import com.dan.sd.monitoring.dtos.DeviceDetailsDTO;
import com.dan.sd.monitoring.entities.Device;

public class DeviceBuilder {

    public static Device toEntity(DeviceDetailsDTO deviceDetailsDTO) {
        return new Device(deviceDetailsDTO.getId(), deviceDetailsDTO.getMaxHourlyConsumption(), deviceDetailsDTO.getUserId());
    }
}
