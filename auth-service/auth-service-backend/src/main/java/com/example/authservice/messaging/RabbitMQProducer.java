package com.example.authservice.messaging;

import com.example.authservice.configuration.RabbitMQConfig;
import com.example.authservice.dto.UserSyncDTO;
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

    public void sendMessage(String routingKey, UserSyncDTO message) {
        try {
            // Define a instância de origem antes de enviar a mensagem
            message.setOriginInstanceId(instanceId);

            logger.info("Enviando mensagem com routing key {}: {}", routingKey, message);
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, routingKey, message);
            logger.info("Mensagem enviada com sucesso: {}", message);
        } catch (Exception e) {
            logger.error("Erro ao enviar mensagem para RabbitMQ: {}", e.getMessage());
        }
    }
}
