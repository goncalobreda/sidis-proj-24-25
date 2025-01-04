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

    // Filas que vêm do application.properties
    @Value("${rabbitmq.create.queue.name}")
    private String createQueueName;

    @Value("${rabbitmq.partial.update.queue.name}")
    private String partialUpdateQueueName;

    @Value("${rabbitmq.reader.queue.name}")
    private String readerSyncQueueName;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    // Routing keys para Lending
    public static final String CREATE_ROUTING_KEY = "lending.create.#";
    public static final String PARTIAL_UPDATE_ROUTING_KEY = "lending.partial.update.#";

    // Se mantiveres o sync de readers
    public static final String READER_ROUTING_KEY = "user.sync.#";

    // 1) Exchange
    @Bean
    public TopicExchange lendingExchange() {
        return new TopicExchange(exchangeName);
    }

    // 2) Queue para CREATE
    @Bean
    public Queue lendingCreateQueue() {
        return new Queue(createQueueName, true);
    }

    // 3) Queue para PARTIAL UPDATE
    @Bean
    public Queue lendingPartialUpdateQueue() {
        return new Queue(partialUpdateQueueName, true);
    }

    // 4) Se mantiveres a queue de Reader Sync
    @Bean
    public Queue readerSyncQueue() {
        return new Queue(readerSyncQueueName, true);
    }

    // 5) Binding: CREATE
    @Bean
    public Binding createBinding(Queue lendingCreateQueue, TopicExchange lendingExchange) {
        return BindingBuilder.bind(lendingCreateQueue)
                .to(lendingExchange)
                .with(CREATE_ROUTING_KEY);
    }

    // 6) Binding: PARTIAL UPDATE
    @Bean
    public Binding partialUpdateBinding(Queue lendingPartialUpdateQueue, TopicExchange lendingExchange) {
        return BindingBuilder.bind(lendingPartialUpdateQueue)
                .to(lendingExchange)
                .with(PARTIAL_UPDATE_ROUTING_KEY);
    }

    // 7) Binding: READER (se quiseres manter)
    @Bean
    public Binding readerSyncBinding(Queue readerSyncQueue, TopicExchange lendingExchange) {
        return BindingBuilder.bind(readerSyncQueue)
                .to(lendingExchange)
                .with(READER_ROUTING_KEY);
    }

    // Conversor JSON
    @Bean
    public Jackson2JsonMessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Template
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jacksonMessageConverter());
        return rabbitTemplate;
    }
}
