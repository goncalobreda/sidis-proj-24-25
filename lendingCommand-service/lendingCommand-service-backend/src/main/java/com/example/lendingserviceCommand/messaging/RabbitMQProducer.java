package com.example.lendingserviceCommand.messaging;

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
    private String instanceId;

    public <T> void sendMessage(String routingKeyPrefix, T message) {
        try {
            String routingKey = routingKeyPrefix + "." + instanceId + ".sync";
            rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
            logger.info("Mensagem enviada para o Exchange '{}' com routingKey '{}'", exchangeName, routingKey);
        } catch (Exception e) {
            logger.error("Erro ao enviar mensagem para RabbitMQ: {}", e.getMessage(), e);
        }
    }
}
