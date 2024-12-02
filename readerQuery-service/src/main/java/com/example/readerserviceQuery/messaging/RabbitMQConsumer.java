package com.example.readerserviceQuery.messaging;

import com.example.readerserviceQuery.dto.UserSyncDTO;
import com.example.readerserviceQuery.service.ReaderServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConsumer {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConsumer.class);

    private final ReaderServiceImpl readerServiceImpl;

    @Value("${instance.id}")
    private String instanceId;

    public RabbitMQConsumer(ReaderServiceImpl readerServiceImpl) {
        this.readerServiceImpl = readerServiceImpl;
    }

    // Consumir mensagens de sincronização
    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void processSyncMessage(UserSyncDTO userSyncDTO) {
        logger.info("Mensagem recebida para sincronizar Reader: {}", userSyncDTO);

        // Ignorar mensagens da mesma instância
        if (instanceId.equals(userSyncDTO.getOriginInstanceId())) {
            logger.info("Mensagem ignorada, enviada pela própria instância: {}", instanceId);
            return;
        }

        try {
            readerServiceImpl.createFromUserSyncDTO(userSyncDTO);
            logger.info("Reader sincronizado com sucesso: {}", userSyncDTO.getUsername());
        } catch (Exception e) {
            logger.error("Erro ao processar mensagem de sincronização: {}", e.getMessage(), e);
        }
    }
}
