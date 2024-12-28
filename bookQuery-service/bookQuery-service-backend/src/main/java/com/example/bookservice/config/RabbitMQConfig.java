package com.example.bookservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.book.sync}")
    private String bookSyncQueueName;

    @Value("${rabbitmq.partial.update.queue.name}")
    private String partialUpdateQueueName;

    @Value("${rabbitmq.exchange.book.command}")
    private String bookCommandExchange;

    @Bean
    public Queue bookSyncQueue() {
        return new Queue(bookSyncQueueName, true);
    }

    @Bean
    public Queue partialUpdateQueue() {
        return new Queue(partialUpdateQueueName, true);
    }

    @Bean
    public TopicExchange bookCommandExchange() {
        return new TopicExchange(bookCommandExchange);
    }

    @Bean
    public Binding querySyncBinding(@Qualifier("bookSyncQueue") Queue bookSyncQueue, TopicExchange bookCommandExchange) {
        return BindingBuilder.bind(bookSyncQueue).to(bookCommandExchange).with("book.sync.query.book1");
    }

    @Bean
    public Binding partialUpdateBinding(@Qualifier("partialUpdateQueue") Queue partialUpdateQueue, TopicExchange bookCommandExchange) {
        return BindingBuilder.bind(partialUpdateQueue).to(bookCommandExchange).with("book.partial.update.#");
    }


    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        return factory;
    }
}
