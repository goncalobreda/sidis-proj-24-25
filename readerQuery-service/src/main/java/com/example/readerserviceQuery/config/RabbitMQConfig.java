package com.example.readerserviceQuery.config;

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

    @Value("${rabbitmq.partial.update.queue.name}")
    private String partialUpdateQueueName;

    public static final String EXCHANGE_NAME = "auth-service-exchange";
    public static final String ROUTING_KEY = "user.sync.#";

    public static final String READER_SERVICE_EXCHANGE = "reader-service-exchange";
    public static final String PARTIAL_UPDATE_ROUTING_KEY = "reader.partial.update.#";

    @Bean
    public Queue readerQueue() {
        return new Queue(queueName, true);  // Durable
    }

    @Bean
    public Queue partialUpdateQueue() {
        return new Queue(partialUpdateQueueName, true);
    }

    @Bean
    public TopicExchange readerExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public TopicExchange readerServiceExchange() {
        return new TopicExchange(READER_SERVICE_EXCHANGE);
    }

    @Bean
    public Binding readerQueueBinding(Queue readerQueue, TopicExchange readerExchange) {
        return BindingBuilder.bind(readerQueue).to(readerExchange).with(ROUTING_KEY);
    }


    @Bean
    public Binding partialUpdateBinding(Queue partialUpdateQueue, TopicExchange readerServiceExchange) {
        return BindingBuilder.bind(partialUpdateQueue).to(readerServiceExchange).with(PARTIAL_UPDATE_ROUTING_KEY);

    }

    // Conversor de mensagens para JSON
    @Bean
    public Jackson2JsonMessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // RabbitTemplate configurado para usar o conversor JSON
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jacksonMessageConverter());
        return rabbitTemplate;
    }
}
