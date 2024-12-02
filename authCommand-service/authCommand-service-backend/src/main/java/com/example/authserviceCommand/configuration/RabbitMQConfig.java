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

    @Value("${rabbitmq.bootstrap.queue.name:}")
    private String bootstrapQueueName;

    @Value("${instance.id}")
    private String instanceId;

    public static final String EXCHANGE_NAME = "auth-service-exchange";

    @Bean
    public Queue syncQueue() {
        return new Queue(queueName, true);
    }

    @Bean
    public Queue bootstrapQueue() {
        if ("command.auth1".equals(instanceId)) {
            return new Queue(bootstrapQueueName, true);
        }
        return null;
    }

    @Bean
    public TopicExchange authExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding syncQueueBinding(Queue syncQueue, TopicExchange authExchange) {
        String routingKey = "user.sync.#"; // Qualquer instância pode escutar mensagens de sincronização
        return BindingBuilder.bind(syncQueue).to(authExchange).with(routingKey);
    }


    @Bean
    public Binding registerQueueBinding(Queue syncQueue, TopicExchange authExchange) {
        String routingKey = "user.register.#";
        return BindingBuilder.bind(syncQueue).to(authExchange).with(routingKey);
    }


    @Bean
    public Binding bootstrapQueueBinding(Queue bootstrapQueue, TopicExchange authExchange) {
        if ("command.auth1".equals(instanceId) && bootstrapQueue != null) {
            String routingKey = "bootstrap.sync.query";
            return BindingBuilder.bind(bootstrapQueue).to(authExchange).with(routingKey);
        }
        return null;
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
