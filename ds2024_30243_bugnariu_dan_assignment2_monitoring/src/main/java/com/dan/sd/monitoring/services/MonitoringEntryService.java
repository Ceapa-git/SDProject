package com.dan.sd.monitoring.services;

import com.dan.sd.monitoring.constants.WebSocketEndpoints;
import com.dan.sd.monitoring.controllers.handlers.exceptions.model.ResourceNotFoundException;
import com.dan.sd.monitoring.dtos.MonitoringEntryDTO;
import com.dan.sd.monitoring.dtos.MonitoringEntryDetailsDTO;
import com.dan.sd.monitoring.dtos.builders.MonitoringEntryBuilder;
import com.dan.sd.monitoring.entities.Device;
import com.dan.sd.monitoring.entities.MonitoringEntry;
import com.dan.sd.monitoring.repositories.DeviceRepository;
import com.dan.sd.monitoring.repositories.MonitoringEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MonitoringEntryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MonitoringEntryService.class);
    private final MonitoringEntryRepository monitoringEntryRepository;
    private final DeviceRepository deviceRepository;
    private final SimpMessagingTemplate template;

    @Autowired
    public MonitoringEntryService(MonitoringEntryRepository monitoringEntryRepository, DeviceRepository deviceRepository, SimpMessagingTemplate template) {
        this.monitoringEntryRepository = monitoringEntryRepository;
        this.deviceRepository = deviceRepository;
        this.template = template;
    }

    private Device getDevice(UUID id) {
        Optional<Device> deviceOptional = deviceRepository.findById(id);
        if (deviceOptional.isEmpty()) {
            LOGGER.error("Device with id {} not found", id);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + id);
        }
        return deviceOptional.get();
    }

    public void insert(MonitoringEntryDetailsDTO monitoringEntryDTO) {
        Device device = getDevice(monitoringEntryDTO.getDeviceId());

        MonitoringEntry monitoringEntry = new MonitoringEntry(device, monitoringEntryDTO.getMeasurementValue(), monitoringEntryDTO.getTimestamp());
        monitoringEntryRepository.save(monitoringEntry);

        Long lastHour = monitoringEntryDTO.getTimestamp() - 3600000;
        List<MonitoringEntry> entries = monitoringEntryRepository.findAllByDeviceIdAndTimestampAfter(device, lastHour);

        LOGGER.error("Device with id {} has {} entries in the last hour", device.getId(), entries.size());
        Float consumption = 0f;
        for (MonitoringEntry entry : entries) {
            consumption += entry.getMeasurementValue();
        }

        if (consumption > device.getMaxHourlyConsumption()) {
            LOGGER.error("Device with id {} exceeded the hourly consumption limit", device.getId());
            template.convertAndSend(WebSocketEndpoints.LISTEN_TO_NOTIFICATIONS + "user/" + device.getUserId(), device.getId());
        }
    }

    public List<MonitoringEntryDTO> findByDeviceId(UUID deviceId, Long startTimestamp) {
        Device device = getDevice(deviceId);
        Long endTimestamp = startTimestamp + 86400000;
        List<MonitoringEntry> monitoringEntries = monitoringEntryRepository.findAllByDeviceIdAndTimestampBetween(device, startTimestamp, endTimestamp);
        return monitoringEntries.stream()
                .map(MonitoringEntryBuilder::toMonitoringEntryDTO)
                .collect(Collectors.toList());
    }
}
