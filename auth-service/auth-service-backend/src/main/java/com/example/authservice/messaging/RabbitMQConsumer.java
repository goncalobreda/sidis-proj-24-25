package com.example.authservice.messaging;

import com.example.authservice.dto.UserSyncDTO;
import com.example.authservice.usermanagement.services.CreateUserRequest;
import com.example.authservice.usermanagement.services.UserService;
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

    @Value("${instance.id}") // ID único para identificar a instância (auth1 ou auth2)
    private String instanceId;

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void processMessage(UserSyncDTO userSyncDTO) {
        logger.info("Mensagem recebida do auth-service: {}", userSyncDTO);
        logger.info("PhoneNumber recebido no UserSyncDTO: {}", userSyncDTO.getPhoneNumber());

        // Verifica se a mensagem foi enviada pela mesma instância
        if (instanceId.equals(userSyncDTO.getOriginInstanceId())) {
            logger.info("Mensagem ignorada pois foi enviada pela mesma instância: {}", instanceId);
            return;
        }

        logger.info("Instância {} recebeu mensagem para sincronização: {}", instanceId, userSyncDTO);

        try {
            userService.upsert(new CreateUserRequest(
                    userSyncDTO.getUsername(),
                    userSyncDTO.getFullName(),
                    userSyncDTO.getPassword(),
                    userSyncDTO.getAuthorities(),
                    userSyncDTO.getPhoneNumber()
            ));
            logger.info("Sincronização concluída para utilizador: {}", userSyncDTO.getUsername());
        } catch (Exception e) {
            logger.error("Erro ao sincronizar utilizador: {}", e.getMessage());
        }
    }
}
