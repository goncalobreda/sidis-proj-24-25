package com.example.recommendationserviceCommand.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchange configurado no .properties
    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    // Queue que vais usar para receber o "BookReturnedEvent"
    // Podes definir "recommendation1.bookreturned.queue" no application.properties
    @Value("${rabbitmq.bookreturned.queue.name}")
    private String bookReturnedQueueName;

    // Se quiseres parametrizar a routing key, podes (ou usar valor fixo "lending.returned.#")
    private static final String BOOK_RETURNED_ROUTING_KEY = "lending.returned.#";

    @Bean
    public TopicExchange recommendationExchange() {
        // O MESMO exchange que o Lending usa: "lending-service-exchange"
        return new TopicExchange(exchangeName);
    }

    /**
     * Declara a queue para receber BookReturnedEvent
     */
    @Bean
    public Queue bookReturnedQueue(@Value("${rabbitmq.bookreturned.queue.name}") String queueName) {
        return new Queue(queueName, true);
    }

    /**
     * Faz o binding entre a queue e a routing key "lending.returned.#".
     * Assim, qualquer mensagem enviada com routingKey que comece por "lending.returned."
     * cairá nesta queue.
     */
    @Bean
    public Binding bookReturnedBinding(Queue bookReturnedQueue, TopicExchange recommendationExchange) {
        return BindingBuilder.bind(bookReturnedQueue)
                .to(recommendationExchange)
                .with("lending.returned.#");
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
