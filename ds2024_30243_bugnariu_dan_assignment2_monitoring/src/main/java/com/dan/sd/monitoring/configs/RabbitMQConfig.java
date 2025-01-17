package com.dan.sd.monitoring.configs;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String SYNC_QUEUE_NAME = "sync";
    public static final String MONITORING_QUEUE_NAME = "monitoring";

    @Bean
    public Queue sync() {
        return new Queue(SYNC_QUEUE_NAME, true);
    }

    @Bean Queue monitoring() {
        return new Queue(MONITORING_QUEUE_NAME, true);
    }
}
