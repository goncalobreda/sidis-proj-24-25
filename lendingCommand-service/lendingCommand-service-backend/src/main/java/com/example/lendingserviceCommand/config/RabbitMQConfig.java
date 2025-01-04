package com.example.lendingserviceCommand.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.create.queue.name}")
    private String createQueueName;

    @Value("${rabbitmq.partial.update.queue.name}")
    private String partialUpdateQueueName;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    // Routing Keys
    public static final String CREATE_ROUTING_KEY_PREFIX = "lending.create.";         // ex.: lending.create.lending1
    public static final String PARTIAL_UPDATE_ROUTING_KEY_PREFIX = "lending.partial.update.";

    // Wildcards para o binding
    public static final String CREATE_ROUTING_KEY_BINDING = "lending.create.#";
    public static final String PARTIAL_ROUTING_KEY_BINDING = "lending.partial.update.#";

    @Bean
    public Queue lendingCreateQueue() {
        return new Queue(createQueueName, true);
    }

    @Bean
    public Queue lendingPartialUpdateQueue() {
        return new Queue(partialUpdateQueueName, true);
    }

    @Bean
    public TopicExchange lendingExchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Binding createQueueBinding(Queue lendingCreateQueue, TopicExchange lendingExchange) {
        // Qualquer routing key que comece com "lending.create."
        return BindingBuilder.bind(lendingCreateQueue)
                .to(lendingExchange)
                .with(CREATE_ROUTING_KEY_BINDING);
    }

    @Bean
    public Binding partialUpdateQueueBinding(Queue lendingPartialUpdateQueue, TopicExchange lendingExchange) {
        // Qualquer routing key que comece com "lending.partial.update."
        return BindingBuilder.bind(lendingPartialUpdateQueue)
                .to(lendingExchange)
                .with(PARTIAL_ROUTING_KEY_BINDING);
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
