package com.dan.sd.monitoring.services;

import com.dan.sd.monitoring.configs.RabbitMQConfig;
import com.dan.sd.monitoring.constants.RabbitMQAction;
import com.dan.sd.monitoring.dtos.DeviceDetailsDTO;
import com.dan.sd.monitoring.dtos.MonitoringEntryDetailsDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RabbitMQReceiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQReceiver.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DeviceService deviceService;
    private final MonitoringEntryService monitoringEntryService;

    @Autowired
    public RabbitMQReceiver(DeviceService deviceService, MonitoringEntryService monitoringEntryService) {
        this.deviceService = deviceService;
        this.monitoringEntryService = monitoringEntryService;
    }

    @RabbitListener(queues = RabbitMQConfig.SYNC_QUEUE_NAME)
    public void receiveSyncMessage(String message) {
        JsonNode node = getJsonNode(message);
        if (node == null) return;
        String action = node.get("action").asText();
        RabbitMQAction rabbitMQAction = RabbitMQAction.valueOf(action);

        if(rabbitMQAction != RabbitMQAction.DELETE) {
            DeviceDetailsDTO deviceDetailsDTO = new DeviceDetailsDTO(
                    UUID.fromString(node.get("deviceId").asText()),
                    (float) node.get("maxHourlyConsumption").asDouble(),
                    UUID.fromString(node.get("userId").asText())
            );
            if (rabbitMQAction == RabbitMQAction.CREATE) {
                if(!deviceService.insert(deviceDetailsDTO)) {
                    LOGGER.error("[CREATE] Device with id {} already exists in db", deviceDetailsDTO.getId());
                }
            } else if (rabbitMQAction == RabbitMQAction.UPDATE) {
                if(!deviceService.update(deviceDetailsDTO)) {
                    LOGGER.error("[UPDATE] Device with id {} not found in db", deviceDetailsDTO.getId());
                }
            }
        } else {
            UUID deviceId = UUID.fromString(node.get("deviceId").asText());
            if(!deviceService.delete(deviceId)) {
                LOGGER.error("[DELETE] Device with id {} not found in db", deviceId);
            }
        }
    }
    @RabbitListener(queues = RabbitMQConfig.MONITORING_QUEUE_NAME)
    public void receiveMonitoringMessage(String message) {
        JsonNode node = getJsonNode(message);
        if (node == null) return;
        UUID deviceId = UUID.fromString(node.get("deviceId").asText());
        Float measurementValue = (float) node.get("measurementValue").asDouble();
        Long timestamp = node.get("timestamp").asLong();
        MonitoringEntryDetailsDTO monitoringEntryDTO = new MonitoringEntryDetailsDTO(deviceId, measurementValue, timestamp);
        monitoringEntryService.insert(monitoringEntryDTO);
    }

    private JsonNode getJsonNode(String message) {
        LOGGER.error("Received message from RabbitMQ: {}", message);
        try {
            return objectMapper.readTree(message);
        } catch (JsonProcessingException e) {
            LOGGER.error("Error parsing message from RabbitMQ");
            return null;
        }
    }
}
