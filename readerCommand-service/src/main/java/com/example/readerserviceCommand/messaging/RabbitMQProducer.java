package com.example.readerserviceCommand.messaging;

import com.example.readerserviceCommand.config.RabbitMQConfig;
import com.example.readerserviceCommand.dto.UserSyncDTO;
import com.example.readerserviceCommand.model.Reader;
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
                instanceId,
                reader.getPhoneNumber()
        );

        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.READER_EXCHANGE, RabbitMQConfig.READER_ROUTING_KEY, userSyncDTO);
            logger.info("Mensagem enviada com sucesso: {}", userSyncDTO);
        } catch (Exception e) {
            logger.error("Erro ao enviar mensagem: {}", e.getMessage());
        }
    }
}