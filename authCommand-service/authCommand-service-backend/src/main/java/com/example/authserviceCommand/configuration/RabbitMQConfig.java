package com.example.authserviceCommand.configuration;

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

    @Value("${rabbitmq.bootstrap.queue.name}")
    private String bootstrapQueueName;

    @Value("${instance.id}")
    private String instanceId;

    public static final String EXCHANGE_NAME = "auth-service-exchange";

    // Queue para sincronização de utilizadores
    @Bean
    public Queue syncQueue() {
        return new Queue(queueName, true);
    }

    // Queue para bootstrap
    @Bean
    public Queue bootstrapQueue() {
        return new Queue(bootstrapQueueName, true);
    }

    @Bean
    public TopicExchange authExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    // Binding para a queue de sincronização
    @Bean
    public Binding syncQueueBinding(Queue syncQueue, TopicExchange authExchange) {
        String routingKey = "user.sync." + instanceId;
        return BindingBuilder.bind(syncQueue).to(authExchange).with(routingKey);
    }

    // Binding para a queue de bootstrap
    @Bean
    public Binding bootstrapQueueBinding(Queue bootstrapQueue, TopicExchange authExchange) {
        String routingKey = "bootstrap.sync." + instanceId;
        return BindingBuilder.bind(bootstrapQueue).to(authExchange).with(routingKey);
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
