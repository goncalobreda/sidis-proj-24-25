package com.example.authserviceCommand.messaging;

import com.example.authserviceCommand.dto.UserSyncDTO;
import com.example.authserviceCommand.usermanagement.services.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMQConsumer {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConsumer.class);

    private final UserService userService;

    @Value("${instance.id}")
    private String instanceId;

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void receiveSyncMessage(UserSyncDTO userSyncDTO) {
        logger.info("Mensagem de sincronização recebida: {}", userSyncDTO.getUsername());

        // Ignorar mensagens da mesma instância
        if (instanceId.equals(userSyncDTO.getOriginInstanceId())) {
            logger.info("Mensagem ignorada por ser originada da mesma instância: {}", instanceId);
            return;
        }

        try {
            userService.upsert(userSyncDTO);
            logger.info("Utilizador sincronizado com sucesso: {}", userSyncDTO.getUsername());
        } catch (Exception e) {
            logger.error("Erro ao sincronizar utilizador: {}", e.getMessage(), e);
        }
    }
}
