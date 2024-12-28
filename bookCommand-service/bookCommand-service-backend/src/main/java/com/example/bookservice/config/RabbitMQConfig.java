package com.example.bookservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.book.sync}")
    private String bookSyncQueueName;

    @Value("${rabbitmq.queue.book.event}")
    private String bookEventQueueName;

    @Value("${rabbitmq.partial.update.queue.name}")
    private String partialUpdateQueueName;

    @Value("${rabbitmq.queue.author.sync}")
    private String authorSyncQueueName;

    @Value("${rabbitmq.exchange.name}")
    private String bookCommandExchange;

    @Bean
    public TopicExchange bookCommandExchange() {
        return new TopicExchange(bookCommandExchange);
    }

    @Bean
    @Qualifier("authorSyncQueue")
    public Queue authorSyncQueue() {
        return new Queue(authorSyncQueueName, true);
    }

    @Bean
    @Qualifier("bookSyncQueue")
    public Queue bookSyncQueue() {
        return new Queue(bookSyncQueueName, true);
    }

    @Bean
    @Qualifier("partialUpdateQueue")
    public Queue partialUpdateQueue() {
        return new Queue(partialUpdateQueueName, true);
    }

    @Bean
    @Qualifier("bookEventQueue")
    public Queue bookEventQueue() {
        return new Queue(bookEventQueueName, true);
    }



    // Bindings
    @Bean
    public Binding bindingInstance1(@Qualifier("bookSyncQueue") Queue bookSyncQueue, TopicExchange bookCommandExchange) {
        return BindingBuilder.bind(bookSyncQueue).to(bookCommandExchange).with("book.sync.command.book1");
    }

    @Bean
    public Binding bindingInstance2(@Qualifier("bookSyncQueue") Queue bookSyncQueue, TopicExchange bookCommandExchange) {
        return BindingBuilder.bind(bookSyncQueue).to(bookCommandExchange).with("book.sync.command.book2");
    }

    @Bean
    public Binding partialUpdateBinding(@Qualifier("partialUpdateQueue") Queue partialUpdateQueue, TopicExchange bookCommandExchange) {
        return BindingBuilder.bind(partialUpdateQueue).to(bookCommandExchange).with("book.partial.update.#");
    }


    @Bean
    public Binding bookEventBinding(@Qualifier("bookEventQueue") Queue bookEventQueue, TopicExchange bookCommandExchange) {
        return BindingBuilder.bind(bookEventQueue).to(bookCommandExchange).with("book.event.#");
    }


    @Bean
    public Binding authorSyncBinding(Queue authorSyncQueue, TopicExchange bookCommandExchange) {
        return BindingBuilder.bind(authorSyncQueue).to(bookCommandExchange).with("author.sync.#");
    }

    // Message Converter
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // RabbitTemplate with Message Converter
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}
