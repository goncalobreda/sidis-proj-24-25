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

    public static final String ROUTING_KEY_READERS = "user.sync.#"; // Para sincronizar Readers
    public static final String ROUTING_KEY_LENDINGS = "lending.sync.#"; // Para sincronizar Lendings

    @Value("${rabbitmq.lending.queue.name}")
    private String lendingQueueName;

    @Value("${rabbitmq.reader.queue.name}")
    private String readerQueueName;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    // Exchange para o serviço Lending
    @Bean
    public TopicExchange lendingExchange() {
        return new TopicExchange(exchangeName);
    }

    // Queue para sincronizar Readers
    @Bean
    public Queue readerSyncQueue() {
        return new Queue(readerQueueName, true);
    }

    // Binding para conectar a queue de Readers à exchange
    @Bean
    public Binding readerSyncBinding(Queue readerSyncQueue, TopicExchange lendingExchange) {
        return BindingBuilder.bind(readerSyncQueue).to(lendingExchange).with(ROUTING_KEY_READERS);
    }

    // Queue para sincronizar Lendings
    @Bean
    public Queue lendingSyncQueue() {
        return new Queue(lendingQueueName, true);
    }

    // Binding para conectar a queue de Lendings à exchange
    @Bean
    public Binding lendingSyncBinding(Queue lendingSyncQueue, TopicExchange lendingExchange) {
        return BindingBuilder.bind(lendingSyncQueue).to(lendingExchange).with(ROUTING_KEY_LENDINGS);
    }

    // Configuração para serialização/deserialização de mensagens
    @Bean
    public Jackson2JsonMessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Configuração do RabbitTemplate
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jacksonMessageConverter());
        return rabbitTemplate;
    }
}
