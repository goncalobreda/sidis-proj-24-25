package com.example.readerserviceCommand.config;

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
    private String readerQueueName;

    public static final String READER_EXCHANGE = "reader-service-exchange";
    public static final String READER_ROUTING_KEY = "reader.sync.create";

    public static final String AUTH_EXCHANGE = "auth-service-exchange";
    public static final String AUTH_ROUTING_KEY = "user.sync.create";
    public static final String AUTH_QUEUE = "auth-reader-sync-queue";

    @Bean
    public Queue readerQueue() {
        return new Queue(readerQueueName, true);
    }

    @Bean
    public TopicExchange readerExchange() {
        return new TopicExchange(READER_EXCHANGE);
    }

    @Bean
    public Binding readerQueueBinding(Queue readerQueue, TopicExchange readerExchange) {
        return BindingBuilder.bind(readerQueue).to(readerExchange).with(READER_ROUTING_KEY);
    }

    @Bean
    public Queue authQueue() {
        return new Queue(AUTH_QUEUE, true);
    }

    @Bean
    public TopicExchange authExchange() {
        return new TopicExchange(AUTH_EXCHANGE);
    }

    @Bean
    public Binding authQueueBinding(Queue authQueue, TopicExchange authExchange) {
        return BindingBuilder.bind(authQueue).to(authExchange).with(AUTH_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}