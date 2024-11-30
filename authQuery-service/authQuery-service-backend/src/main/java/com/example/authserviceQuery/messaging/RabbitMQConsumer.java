package com.example.authserviceQuery.messaging;

import com.example.authserviceQuery.dto.UserSyncDTO;
import com.example.authserviceQuery.usermanagement.model.Role;
import com.example.authserviceQuery.usermanagement.model.User;
import com.example.authserviceQuery.usermanagement.repositories.UserRepository;
import com.example.authserviceQuery.usermanagement.repositories.UserSyncRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RabbitMQConsumer {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConsumer.class);

    private final UserRepository userRepo;
    private final UserSyncRepository userSyncRepo;

    @Value("${instance.id}") // ID único para identificar a instância (auth1 ou auth2)
    private String instanceId;

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void processMessage(UserSyncDTO userSyncDTO) {
        logger.info("Mensagem recebida no Query Service: {}", userSyncDTO);

        // Ignorar mensagens da mesma instância
        if (userSyncDTO.getOriginInstanceId() == null || userSyncDTO.getOriginInstanceId().equals(instanceId)) {
            logger.info("Mensagem ignorada, originInstanceId={} é igual à instanceId={}", userSyncDTO.getOriginInstanceId(), instanceId);
            return;
        }

        try {
            logger.info("Sincronizando utilizador no Query Service: {}", userSyncDTO.getUsername());

            // Verificar se o utilizador já existe
            User user = userRepo.findByUsername(userSyncDTO.getUsername())
                    .orElse(new User(userSyncDTO.getUsername(), userSyncDTO.getPassword()));

            // Atualizar os dados do utilizador
            user.setFullName(userSyncDTO.getFullName());
            user.setAuthorities(convertAuthorities(userSyncDTO.getAuthorities()));
            user.setEnabled(userSyncDTO.isEnabled());
            user.setPhoneNumber(userSyncDTO.getPhoneNumber());

            // Salvar no repositório de sincronização
            userSyncRepo.save(user);

            logger.info("Sincronização concluída com sucesso para utilizador: {}", userSyncDTO.getUsername());
        } catch (Exception e) {
            logger.error("Erro ao sincronizar utilizador no Query Service: {}", e.getMessage());
        }
    }

    private Set<Role> convertAuthorities(Set<String> authorities) {
        return authorities.stream().map(Role::new).collect(Collectors.toSet());
    }
}
