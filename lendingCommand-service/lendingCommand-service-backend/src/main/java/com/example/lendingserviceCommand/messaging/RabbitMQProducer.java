package com.example.lendingserviceCommand.messaging;

import com.example.lendingserviceCommand.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMQProducer {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQProducer.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${instance.id}")
    private String instanceId; // "command.lending1" ou "command.lending2"

    /**
     * Enviar mensagem para a fila de CREATE
     */
    public <T> void sendCreateMessage(T message) {
        // Ex.: routing key = "lending.create.command.lending1"
        String routingKey = RabbitMQConfig.CREATE_ROUTING_KEY_PREFIX + instanceId;
        send(exchangeName, routingKey, message);
    }

    /**
     * Enviar mensagem para a fila de PARTIAL UPDATE
     */
    public <T> void sendPartialUpdateMessage(T message) {
        // Ex.: routing key = "lending.partial.update.command.lending1"
        String routingKey = RabbitMQConfig.PARTIAL_UPDATE_ROUTING_KEY_PREFIX + instanceId;
        send(exchangeName, routingKey, message);
    }

    private <T> void send(String exchange, String routingKey, T payload) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, payload);
            logger.info("Mensagem enviada: Exchange='{}', routingKey='{}', Payload='{}'",
                    exchange, routingKey, payload);
        } catch (Exception e) {
            logger.error("Erro ao enviar mensagem para RabbitMQ: {}", e.getMessage(), e);
        }
    }
}
