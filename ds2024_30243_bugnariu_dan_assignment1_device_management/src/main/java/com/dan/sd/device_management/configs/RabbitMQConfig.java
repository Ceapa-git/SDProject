package com.dan.sd.device_management.configs;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE_NAME = "syncDeviceToMonitoring";
    public static final String TOPIC_QUEUE_NAME = "sync";
    public static final String TOPIC_ROUTING_KEY = "device.monitoring";

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding topicBinding() {
        return BindingBuilder.bind(new Queue(TOPIC_QUEUE_NAME, true)).to(topicExchange()).with(TOPIC_ROUTING_KEY);
    }
}
