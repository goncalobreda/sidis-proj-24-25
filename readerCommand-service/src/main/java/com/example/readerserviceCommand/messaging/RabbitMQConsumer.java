package com.example.readerserviceCommand.messaging;

import com.example.readerserviceCommand.dto.UserSyncDTO;
import com.example.readerserviceCommand.service.ReaderServiceImpl;
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

        // Log para verificar a instância de origem
        if (instanceId.equals(userSyncDTO.getOriginInstanceId())) {
            logger.info("Mensagem ignorada, enviada pela própria instância: {}", instanceId);
            return;
        }

        try {
            logger.info("Processando sincronização para o Reader: {}", userSyncDTO.getUsername());
            readerServiceImpl.createFromUserSyncDTO(userSyncDTO);
            logger.info("Reader sincronizado com sucesso: {}", userSyncDTO.getUsername());
        } catch (Exception e) {
            logger.error("Erro ao processar mensagem de sincronização: {}", e.getMessage(), e);
        }
    }
}
