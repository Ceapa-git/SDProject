package com.dan.sd.device_management.services;

import com.dan.sd.device_management.constants.RabbitMQAction;
import com.dan.sd.device_management.controllers.handlers.exceptions.model.ResourceNotFoundException;
import com.dan.sd.device_management.dtos.DeviceDTO;
import com.dan.sd.device_management.dtos.DeviceDetailsDTO;
import com.dan.sd.device_management.dtos.DeviceUpdateDTO;
import com.dan.sd.device_management.dtos.builders.DeviceBuilder;
import com.dan.sd.device_management.entities.Device;
import com.dan.sd.device_management.entities.User;
import com.dan.sd.device_management.repositories.DeviceRepository;
import com.dan.sd.device_management.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DeviceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceService.class);
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final RabbitMQSender rabbitMQSender;

    @Autowired
    public DeviceService(DeviceRepository deviceRepository, UserRepository userRepository, RabbitMQSender rabbitMQSender) {
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
        this.rabbitMQSender = rabbitMQSender;
    }

    private Device getDeviceIfExists(UUID id, Optional<Device> deviceOptional) {
        if (deviceOptional.isEmpty()) {
            LOGGER.error("Device with id {} not found", id);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + id);
        }
        return deviceOptional.get();
    }

    private User getUser(UUID userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            LOGGER.error("User with id {} not found", userId);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + userId);
        }
        return userOptional.get();
    }

    private void sendDeleteRabbitMQMessage(UUID id) {
        sendDeleteRabbitMQMessage(RabbitMQAction.DELETE, id, null, null);
    }

    private void sendDeleteRabbitMQMessage(RabbitMQAction action, UUID id, UUID userId, Float maxHourlyConsumption) {
        rabbitMQSender.sendMessage(id, userId, maxHourlyConsumption, action);
        LOGGER.error("Message sent to RabbitMQ for device with id {}", id);
    }

    public List<DeviceDTO> findDevices() {
        List<Device> deviceList = deviceRepository.findAll();
        return deviceList.stream()
                .map(DeviceBuilder::toDeviceDTO)
                .collect(Collectors.toList());
    }

    public DeviceDTO insert(DeviceDetailsDTO deviceDetailsDTO) {
        User user = getUser(deviceDetailsDTO.getUserId());
        Device device = DeviceBuilder.toEntity(deviceDetailsDTO);
        device.setUserId(user);
        device = deviceRepository.save(device);
        LOGGER.error("Device with id {} was inserted in db", device.getId());

        sendDeleteRabbitMQMessage(RabbitMQAction.CREATE, device.getId(), device.getUserId().getId(), device.getMaxHourlyConsumption());

        return DeviceBuilder.toDeviceDTO(device);
    }

    public DeviceDTO findById(UUID id) {
        Optional<Device> deviceOptional = deviceRepository.findById(id);
        return DeviceBuilder.toDeviceDTO(getDeviceIfExists(id, deviceOptional));
    }

    public DeviceDTO update(UUID id, DeviceUpdateDTO deviceUpdateDTO) {
        Optional<Device> deviceOptional = deviceRepository.findById(id);
        Device device = getDeviceIfExists(id, deviceOptional);
        if (deviceUpdateDTO.getAddress() != null) {
            device.setAddress(deviceUpdateDTO.getAddress());
        }
        if (deviceUpdateDTO.getDescription() != null) {
            device.setDescription(deviceUpdateDTO.getDescription());
        }
        if (deviceUpdateDTO.getMaxHourlyConsumption() != null) {
            device.setMaxHourlyConsumption(deviceUpdateDTO.getMaxHourlyConsumption());
        }
        if (deviceUpdateDTO.getUserId() != null) {
            User user = getUser(deviceUpdateDTO.getUserId());
            device.setUserId(user);
        }
        device = deviceRepository.save(device);
        LOGGER.error("Device with id {} was updated in db", device.getId());

        sendDeleteRabbitMQMessage(RabbitMQAction.UPDATE, device.getId(), device.getUserId().getId(), device.getMaxHourlyConsumption());

        return DeviceBuilder.toDeviceDTO(device);
    }

    public void deleteById(UUID id) {
        deviceRepository.deleteById(id);
        LOGGER.error("Device with id {} was deleted from db", id);

        sendDeleteRabbitMQMessage(id);
    }

    public List<DeviceDTO> findByUserId(UUID id) {
        List<Device> devices = deviceRepository.findByUserId(getUser(id));
        return devices.stream()
                .map(DeviceBuilder::toDeviceDTO)
                .collect(Collectors.toList());
    }
}
