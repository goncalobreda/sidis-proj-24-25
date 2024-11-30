package com.example.authserviceCommand.messaging;

import com.example.authserviceCommand.configuration.RabbitMQConfig;
import com.example.authserviceCommand.dto.UserSyncDTO;
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

    @Value("${instance.id}")
    private String instanceId;

    public <T> void sendMessage(String routingKey, T message) {
        if (message instanceof UserSyncDTO) {
            ((UserSyncDTO) message).setOriginInstanceId(instanceId);
        }
        try {
            logger.info("Enviando mensagem para RabbitMQ: routingKey={}, message={}", routingKey, message);
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, routingKey, message);
        } catch (Exception e) {
            logger.error("Erro ao enviar mensagem para RabbitMQ: {}", e.getMessage());
        }
    }

    public <T> void sendBootstrapMessage(T message) {
        String routingKey = "bootstrap.sync." + instanceId;
        try {
            logger.info("Enviando mensagem de bootstrap para RabbitMQ: routingKey={}, message={}", routingKey, message);
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, routingKey, message);
        } catch (Exception e) {
            logger.error("Erro ao enviar mensagem de bootstrap para RabbitMQ: {}", e.getMessage());
        }
    }
}
