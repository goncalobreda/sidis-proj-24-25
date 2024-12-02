package com.example.readerserviceQuery.messaging;

import com.example.readerserviceQuery.config.RabbitMQConfig;
import com.example.readerserviceQuery.dto.UserSyncDTO;
import com.example.readerserviceQuery.model.Reader;
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
        // Converter Reader para UserSyncDTO
        UserSyncDTO userSyncDTO = new UserSyncDTO(
                reader.getEmail(),
                reader.getFullName(),
                reader.getPassword(),
                reader.isEnabled(),
                instanceId,
                reader.getPhoneNumber()
        );

        // Logando a mensagem antes de enviar
        logger.info("Enviando mensagem de sincronização para o RabbitMQ: {}", userSyncDTO);

        try {
            // Enviar a mensagem para a fila de sincronização com o routing key configurado
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "user.sync.#", userSyncDTO);  // Alterado para a exchange e routing key do Auth
            logger.info("Mensagem enviada com sucesso para sincronizar Reader: {}", userSyncDTO);
        } catch (Exception e) {
            logger.error("Erro ao enviar mensagem de sincronização: {}", e.getMessage());
        }
    }
}
