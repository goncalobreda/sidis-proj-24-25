package com.example.bookservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchanges
    public static final String BOOK_COMMAND_EXCHANGE = "book-command-exchange";
    public static final String AUTHOR_COMMAND_EXCHANGE = "author-command-exchange";

    // Routing Keys
    public static final String BOOK_ROUTING_KEY = "book.sync.event";
    public static final String AUTHOR_ROUTING_KEY = "author.sync.event";

    // Queues
    public static final String BOOK_EVENT_QUEUE = "book-event-queue";
    public static final String BOOK_SYNC_QUEUE = "book-sync-queue"; // Adicionada a fila ausente

    // Configuração da Fila de Eventos de Livro
    @Bean
    public Queue bookEventQueue() {
        return new Queue(BOOK_EVENT_QUEUE, true); // true -> Fila persistente
    }

    // Configuração da Fila de Sincronização de Livro
    @Bean
    public Queue bookSyncQueue() {
        return new Queue(BOOK_SYNC_QUEUE, true);
    }

    // Configuração da Exchange de Comandos de Livro
    @Bean
    public TopicExchange bookCommandExchange() {
        return new TopicExchange(BOOK_COMMAND_EXCHANGE);
    }

    // Binding entre a fila de eventos de livro e a exchange de comandos
    @Bean
    public Binding bookEventQueueBinding(Queue bookEventQueue, TopicExchange bookCommandExchange) {
        return BindingBuilder.bind(bookEventQueue).to(bookCommandExchange).with(BOOK_ROUTING_KEY);
    }

    // Binding entre a fila de sincronização de livro e a exchange de comandos
    @Bean
    public Binding bookSyncBinding(Queue bookSyncQueue, TopicExchange bookCommandExchange) {
        return BindingBuilder.bind(bookSyncQueue).to(bookCommandExchange).with(BOOK_ROUTING_KEY);
    }

    // Conversor de Mensagens JSON
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Configuração do RabbitTemplate com Conversor de Mensagens
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}
