package com.example.acquisitionserviceCommand.configuration;

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

    @Value("${rabbitmq.command.queue.name}")
    private String commandQueueName;

    public static final String BOOK_COMMAND_EXCHANGE = "book-command-exchange";
    public static final String BOOK_ROUTING_KEY = "book.sync.event";

    public static final String EXCHANGE_NAME = "auth-service-exchange";
    public static final String ROUTING_KEY = "user.sync.#";

    public static final String ACQUISITION_EXCHANGE_NAME = "acquisition-service-exchange";
    public static final String ACQUISITION_ROUTING_KEY = "acquisition.sync.#";

    @Bean
    public Queue acquisitionQueue() {
        return new Queue(queueName, true); // Durable
    }

    @Bean
    public Queue commandQueue() {
        return new Queue(commandQueueName, true); // Durable queue exclusiva para este Command
    }

    @Bean
    public TopicExchange authExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public TopicExchange bookCommandExchange() {
        return new TopicExchange(BOOK_COMMAND_EXCHANGE);
    }

    @Bean
    public TopicExchange acquisitionExchange() {
        return new TopicExchange(ACQUISITION_EXCHANGE_NAME);
    }

    @Bean
    public Binding acquisitionQueueBinding(Queue acquisitionQueue, TopicExchange authExchange) {
        return BindingBuilder.bind(acquisitionQueue).to(authExchange).with(ROUTING_KEY);
    }

    @Bean
    public Binding commandQueueBinding(Queue commandQueue, TopicExchange acquisitionExchange) {
        return BindingBuilder.bind(commandQueue).to(acquisitionExchange).with(ACQUISITION_ROUTING_KEY);
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