package com.example.authserviceQuery.messaging;

import com.example.authserviceQuery.dto.UserSyncDTO;
import com.example.authserviceQuery.usermanagement.services.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RabbitMQConsumer {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConsumer.class);

    private final UserService userService;


    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void receiveSyncMessage(UserSyncDTO user) {
        logger.info("Mensagem de sincronização recebida: {}", user.getUsername());

        try {
            userService.upsert(user);
            logger.info("Utilizador sincronizado com sucesso: {}", user.getUsername());
        } catch (Exception e) {
            logger.error("Erro ao sincronizar utilizador: {}", e.getMessage());
        }
    }
}
