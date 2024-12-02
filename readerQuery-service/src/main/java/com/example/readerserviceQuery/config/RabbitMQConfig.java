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
    private String queueName;  // Mesma fila usada pelo Auth

    public static final String EXCHANGE_NAME = "auth-service-exchange";  // Mesma exchange que o Auth
    public static final String ROUTING_KEY = "user.sync.#";  // Mesma routing key do Auth

    @Bean
    public Queue readerQueue() {
        return new Queue(queueName, true);  // Usando a mesma fila que o Auth
    }

    @Bean
    public TopicExchange readerExchange() {
        return new TopicExchange(EXCHANGE_NAME);  // Usando a mesma exchange que o Auth
    }

    @Bean
    public Binding readerQueueBinding(Queue readerQueue, TopicExchange readerExchange) {
        // O Reader vai consumir da mesma fila do Auth
        return BindingBuilder.bind(readerQueue).to(readerExchange).with(ROUTING_KEY);
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
