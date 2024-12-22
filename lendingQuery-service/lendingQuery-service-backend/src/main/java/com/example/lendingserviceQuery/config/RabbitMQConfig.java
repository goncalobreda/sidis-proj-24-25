package com.example.lendingserviceQuery.config;

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
    private String queueName; // Este era para readers se bem entendi

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    public static final String ROUTING_KEY_READERS = "user.sync.#";  // para user
    public static final String ROUTING_KEY_LENDINGS = "lending.sync.#"; // para lending

    @Bean
    public TopicExchange lendingExchange() {
        return new TopicExchange(exchangeName);
    }

    // Fila para Readers (já existia):
    @Bean
    public Queue readerSyncQueue() {
        return new Queue(queueName, true);
    }

    @Bean
    public Binding readerSyncBinding(Queue readerSyncQueue, TopicExchange lendingExchange) {
        // "user.sync.#"
        return BindingBuilder.bind(readerSyncQueue).to(lendingExchange).with(ROUTING_KEY_READERS);
    }

    // == Nova Fila e Binding para Lendings: ==
    @Bean
    public Queue lendingSyncQueue() {
        // Usa outro nome (p.ex. "lendingQuery.sync.queue")
        return new Queue("lendingQuery.sync.queue", true);
    }

    @Bean
    public Binding lendingSyncBinding(Queue lendingSyncQueue, TopicExchange lendingExchange) {
        // "lending.sync.#"
        return BindingBuilder.bind(lendingSyncQueue).to(lendingExchange).with(ROUTING_KEY_LENDINGS);
    }

    // Converter e template
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
