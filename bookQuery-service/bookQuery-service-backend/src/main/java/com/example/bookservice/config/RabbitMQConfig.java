package com.example.bookservice.config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String BOOK_COMMAND_EXCHANGE = "book-command-exchange";
    public static final String BOOK_EVENT_QUEUE = "book-event-queue";
    public static final String BOOK_ROUTING_KEY = "book.sync.event";

    @Bean
    public Queue bookEventQueue() {
        return new Queue(BOOK_EVENT_QUEUE, true);
    }

    @Bean
    public TopicExchange bookCommandExchange() {
        return new TopicExchange(BOOK_COMMAND_EXCHANGE);
    }

    @Bean
    public Binding bookEventQueueBinding(Queue bookEventQueue, TopicExchange bookCommandExchange) {
        return BindingBuilder.bind(bookEventQueue).to(bookCommandExchange).with(BOOK_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}


