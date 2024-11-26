package com.example.readerservice.messaging;

import com.example.readerservice.config.RabbitMQConfig;
import com.example.readerservice.dto.UserSyncDTO;
import com.example.readerservice.model.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQProducer {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQProducer.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${instance.id}")
    private String instanceId;

    public RabbitMQProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendSyncMessage(Reader reader) {
        UserSyncDTO userSyncDTO = new UserSyncDTO(
                reader.getEmail(),
                reader.getFullName(),
                reader.getPassword(),
                reader.isEnabled(),
                instanceId
        );

        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.READER_EXCHANGE, RabbitMQConfig.READER_ROUTING_KEY, userSyncDTO);
            logger.info("Mensagem enviada com sucesso: {}", userSyncDTO);
        } catch (Exception e) {
            logger.error("Erro ao enviar mensagem: {}", e.getMessage());
        }
    }
}