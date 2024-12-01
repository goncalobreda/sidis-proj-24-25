package com.example.lendingservice.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.query.queue.name}")
    private String queryQueueName;

    @Value("${rabbitmq.bootstrap.queue.name}")
    private String bootstrapQueueName;

    @Value("${instance.id}")
    private String instanceId;

    public static final String EXCHANGE_NAME = "lending-service-exchange";

    // Queue para mensagens de consulta
    @Bean
    public Queue queryQueue() {
        return new Queue(queryQueueName, true);
    }

    // Queue para mensagens de bootstrap (sincronização inicial)
    @Bean
    public Queue bootstrapQueue() {
        return new Queue(bootstrapQueueName, true);
    }

    @Bean
    public TopicExchange lendingExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    // Binding para a fila de consulta (Query Queue)
    @Bean
    public Binding queryQueueBinding(Queue queryQueue, TopicExchange lendingExchange) {
        String routingKey = "lending.query." + instanceId;
        return BindingBuilder.bind(queryQueue).to(lendingExchange).with(routingKey);
    }

    // Binding para a fila de bootstrap
    @Bean
    public Binding bootstrapQueueBinding(Queue bootstrapQueue, TopicExchange lendingExchange) {
        String routingKey = "bootstrap.sync." + instanceId;
        return BindingBuilder.bind(bootstrapQueue).to(lendingExchange).with(routingKey);
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
