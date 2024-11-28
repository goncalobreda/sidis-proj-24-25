package com.example.authservice.messaging;

import com.example.authservice.configuration.RabbitMQConfig;
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

    @Value("${instance.id}") // ID único para identificar a instância (auth1 ou auth2)
    private String instanceId;

    public <T> void sendMessage(String routingKey, T message) {
        try {
            logger.info("Enviando mensagem para RabbitMQ: routingKey={}, message={}", routingKey, message);
            logger.info("Enviando mensagem com routing key {}: {}", instanceId, routingKey, message);
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, routingKey, message);
            logger.info("Mensagem enviada com sucesso: {}",instanceId, message);
        } catch (Exception e) {
            logger.error("Erro ao enviar mensagem para RabbitMQ: {}", e.getMessage());
        }
    }
}
