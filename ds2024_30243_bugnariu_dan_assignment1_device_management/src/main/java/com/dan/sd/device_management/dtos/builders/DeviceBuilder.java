package com.dan.sd.device_management.dtos.builders;

import com.dan.sd.device_management.dtos.DeviceDTO;
import com.dan.sd.device_management.dtos.DeviceDetailsDTO;
import com.dan.sd.device_management.entities.Device;

public class DeviceBuilder {
    public static DeviceDTO toDeviceDTO(Device device) {
        return new DeviceDTO(device.getId(), device.getDescription(), device.getAddress(), device.getMaxHourlyConsumption(), device.getUserId().getId());
    }

    public static Device toEntity(DeviceDetailsDTO deviceDetailsDTO) {
        return new Device(deviceDetailsDTO.getDescription(), deviceDetailsDTO.getAddress(), deviceDetailsDTO.getMaxHourlyConsumption());
    }
}
