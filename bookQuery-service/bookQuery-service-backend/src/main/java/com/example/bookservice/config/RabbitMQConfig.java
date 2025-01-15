package com.example.bookservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.partial.update.queue.name}")
    private String partialUpdateQueueName;

    @Value("${rabbitmq.queue.book.sync.query1}")
    private String bookSyncQuery1QueueName;

    @Value("${rabbitmq.queue.book.sync.query2}")
    private String bookSyncQuery2QueueName;

    @Value("${rabbitmq.exchange.book.command}")
    private String bookCommandExchange;

    @Bean
    public Queue bookSyncQuery1Queue() {
        return new Queue(bookSyncQuery1QueueName, true);
    }

    @Bean
    public Queue bookSyncQuery2Queue() {
        return new Queue(bookSyncQuery2QueueName, true);
    }

    @Bean
    public TopicExchange bookCommandExchange() {
        return new TopicExchange(bookCommandExchange);
    }

    @Bean
    public Binding bookSyncQuery1Binding(@Qualifier("bookSyncQuery1Queue") Queue bookSyncQuery1Queue, TopicExchange bookCommandExchange) {
        return BindingBuilder.bind(bookSyncQuery1Queue).to(bookCommandExchange).with("book.sync.command.book1");
    }

    @Bean
    public Binding bookSyncQuery2Binding(@Qualifier("bookSyncQuery2Queue") Queue bookSyncQuery2Queue, TopicExchange bookCommandExchange) {
        return BindingBuilder.bind(bookSyncQuery2Queue).to(bookCommandExchange).with("book-instance2.sync.query.queue");
    }


    @Bean
    public Queue partialUpdateQueue() {
        return new Queue(partialUpdateQueueName, true);
    }

    @Bean
    public Binding partialUpdateBinding(@Qualifier("partialUpdateQueue") Queue partialUpdateQueue, TopicExchange bookCommandExchange) {
        return BindingBuilder.bind(partialUpdateQueue).to(bookCommandExchange).with("book.partial.update.#");
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


    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        return factory;
    }

}
