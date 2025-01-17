package com.dan.sd.device_management.services;

import com.dan.sd.device_management.configs.RabbitMQConfig;
import com.dan.sd.device_management.constants.RabbitMQAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RabbitMQSender {
    private final RabbitTemplate rabbitTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQSender.class);

    @Autowired
    public RabbitMQSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(UUID deviceId, UUID userId, Float maxHourlyConsumption, RabbitMQAction action) {
        String message = "{\"deviceId\":\"" +
                deviceId.toString() +
                "\", \"userId\":\"" +
                (userId != null ? userId.toString() : "null") +
                "\", \"maxHourlyConsumption\":" +
                (maxHourlyConsumption != null ? String.format("%.1f", maxHourlyConsumption) : "null") +
                ", \"action\":\"" +
                action.toString() +
                "\"}";
        LOGGER.error("Sending message to RabbitMQ: {}", message);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.TOPIC_ROUTING_KEY, message);
    }
}

