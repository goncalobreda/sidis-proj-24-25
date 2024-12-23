package com.example.lendingserviceCommand.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.name}")
    private String queueName;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    public static final String ROUTING_KEY_PREFIX = "lending.sync.";

    @Bean
    public Queue lendingQueue() {
        // Nome único para cada instância (baseado no nome em application.properties)
        return new Queue(queueName, true);
    }

    @Bean
    public TopicExchange lendingExchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Binding lendingQueueBinding(Queue lendingQueue, TopicExchange lendingExchange) {
        // Cada instância terá uma chave de roteamento distinta
        return BindingBuilder.bind(lendingQueue).to(lendingExchange).with(ROUTING_KEY_PREFIX + "#");
    }

    @Bean
    public Jackson2JsonMessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jacksonMessageConverter());
        return rabbitTemplate;
    }
}
