package com.example.authserviceCommand.messaging;

import com.example.authserviceCommand.configuration.RabbitMQConfig;
import com.example.authserviceCommand.dto.UserSyncDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RabbitMQProducer {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQProducer.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${instance.id}")
    private String instanceId;

    public <T> void sendMessage(String routingKeyPrefix, T message) {
        if (message instanceof UserSyncDTO userSyncDTO) {
            userSyncDTO.setOriginInstanceId(instanceId);
            userSyncDTO.setMessageId(UUID.randomUUID().toString()); // Gerar ID único para a mensagem
        }

        try {
            String routingKey = routingKeyPrefix + "." + instanceId + ".create";
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, routingKey, message);
            logger.info("Mensagem enviada com sucesso para o Exchange '{}' com routingKey '{}'", RabbitMQConfig.EXCHANGE_NAME, routingKey);
        } catch (Exception e) {
            logger.error("Erro ao enviar mensagem para RabbitMQ: {}", e.getMessage());
        }
    }

    public void sendBootstrapMessage(List<UserSyncDTO> bootstrapUsers) {
        if ("command.auth1".equals(instanceId)) {
            String routingKey = "bootstrap.sync.query";
            logger.info("Preparando mensagem de bootstrap com {} utilizadores.", bootstrapUsers.size());
            try {
                rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, routingKey, bootstrapUsers);
                logger.info("Mensagem de bootstrap enviada com sucesso para o Exchange '{}' com routingKey '{}'", RabbitMQConfig.EXCHANGE_NAME, routingKey);
            } catch (Exception e) {
                logger.error("Erro ao enviar mensagem de bootstrap: {}", e.getMessage());
            }
        } else {
            logger.info("Instância {} não participa do envio de mensagens de bootstrap.", instanceId);
        }
    }
}
