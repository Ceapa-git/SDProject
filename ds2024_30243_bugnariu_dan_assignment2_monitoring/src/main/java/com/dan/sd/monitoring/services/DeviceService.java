package com.dan.sd.monitoring.services;

import com.dan.sd.monitoring.controllers.handlers.exceptions.model.ResourceNotFoundException;
import com.dan.sd.monitoring.dtos.DeviceDetailsDTO;
import com.dan.sd.monitoring.dtos.builders.DeviceBuilder;
import com.dan.sd.monitoring.entities.Device;
import com.dan.sd.monitoring.repositories.DeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class DeviceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceService.class);
    private final DeviceRepository deviceRepository;

    @Autowired
    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    private Device getDevice(UUID id) {
        Optional<Device> deviceOptional = deviceRepository.findById(id);
        if (deviceOptional.isEmpty()) {
            LOGGER.error("Device with id {} not found", id);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + id);
        }
        return deviceOptional.get();
    }

    public boolean insert(DeviceDetailsDTO deviceDetailsDTO) {
        try {
            getDevice(deviceDetailsDTO.getId());
            return false;
        } catch (ResourceNotFoundException e) {
            Device device = DeviceBuilder.toEntity(deviceDetailsDTO);
            deviceRepository.save(device);
            return true;
        }
    }

    public boolean update(DeviceDetailsDTO deviceDetailsDTO) {
        try {
            Device device = getDevice(deviceDetailsDTO.getId());
            device.setMaxHourlyConsumption(deviceDetailsDTO.getMaxHourlyConsumption());
            deviceRepository.save(device);
            return true;
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }

    public boolean delete(UUID id) {
        try {
            Device device = getDevice(id);
            deviceRepository.delete(device);
            return true;
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }
}
