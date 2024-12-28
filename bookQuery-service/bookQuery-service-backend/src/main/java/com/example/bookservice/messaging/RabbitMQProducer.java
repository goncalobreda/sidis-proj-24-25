package com.example.bookservice.messaging;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQProducer {

    private final AmqpTemplate amqpTemplate;

    @Value("${rabbitmq.queue.book.sync}")
    private String routingKey;

    @Value("${rabbitmq.exchange.book.command}")
    private String exchange;

    public RabbitMQProducer(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    public void sendBookSyncMessage(Object message) {
        try {
            System.out.println("Sending message to RabbitMQ:");
            System.out.println("Exchange: " + exchange);
            System.out.println("Routing Key: " + routingKey);
            System.out.println("Message: " + message);

            amqpTemplate.convertAndSend(exchange, routingKey, message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send message to RabbitMQ", e);
        }
    }


}
