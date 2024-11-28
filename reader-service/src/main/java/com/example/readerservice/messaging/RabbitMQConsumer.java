package com.example.readerservice.messaging;

import com.example.readerservice.config.RabbitMQConfig;
import com.example.readerservice.dto.UserSyncDTO;
import com.example.readerservice.service.ReaderServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
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

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void processMessage(UserSyncDTO userSyncDTO) {
        logger.info("Mensagem recebida do auth-service: {}", userSyncDTO);
        logger.info("Recebendo UserSyncDTO: username={}, phoneNumber={}", userSyncDTO.getUsername(), userSyncDTO.getPhoneNumber());


        try {
            readerServiceImpl.createFromUserSyncDTO(userSyncDTO);
            logger.info("Leitor sincronizado com sucesso: {}", userSyncDTO.getUsername());
        } catch (Exception e) {
            logger.error("Erro ao processar mensagem do auth-service: {}", e.getMessage(), e);
        }
    }


    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void processReaderSyncMessage(UserSyncDTO userSyncDTO) {
        if (instanceId.equals(userSyncDTO.getOriginInstanceId())) {
            logger.info("Mensagem ignorada, enviada pela própria instância.");
            return;
        }

        try {
            readerServiceImpl.createFromUserSyncDTO(userSyncDTO);
            logger.info("Leitor sincronizado com sucesso: {}", userSyncDTO.getFullName());
        } catch (Exception e) {
            logger.error("Erro ao processar mensagem: {}", e.getMessage());
        }
    }

    @RabbitListener(queues = RabbitMQConfig.AUTH_QUEUE)
    public void processAuthMessage(UserSyncDTO userSyncDTO) {
        logger.info("Mensagem recebida do auth-service: {}", userSyncDTO);
        try {
            readerServiceImpl.createFromUserSyncDTO(userSyncDTO);
            logger.info("Leitor criado a partir de auth-service: {}", userSyncDTO.getFullName());
        } catch (Exception e) {
            logger.error("Erro ao processar mensagem do auth-service: {}", e.getMessage());
        }
    }
}