package com.example.authserviceCommand.messaging;

import com.example.authserviceCommand.dto.UserSyncDTO;
import com.example.authserviceCommand.usermanagement.services.CreateUserRequest;
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

    @Value("${instance.id}") // ID único para identificar a instância (auth1 ou auth2)
    private String instanceId;

    /**
     * Processa mensagens recebidas para sincronização de utilizadores.
     *
     * @param userSyncDTO Mensagem recebida.
     */
    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void processMessage(UserSyncDTO userSyncDTO) {
        logger.info("Mensagem recebida do RabbitMQ: {}", userSyncDTO);

        // Ignora mensagens enviadas pela mesma instância
        if (instanceId.equals(userSyncDTO.getOriginInstanceId())) {
            logger.info("Mensagem ignorada: originInstanceId={} é igual à instanceId={}", userSyncDTO.getOriginInstanceId(), instanceId);
            return;
        }

        try {
            logger.info("Sincronizando utilizador: {}", userSyncDTO.getUsername());
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

    @RabbitListener(queues = "${rabbitmq.bootstrap.queue.name}")
    public void processBootstrapMessage(String message) {
        logger.info("Mensagem de bootstrap recebida: {}", message);
        // Lógica para processar mensagens de bootstrap
    }
}
