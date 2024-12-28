package com.example.acquisitionserviceCommand.messaging;

import com.example.acquisitionserviceCommand.dto.BookSyncDTO;
import com.example.acquisitionserviceCommand.configuration.RabbitMQConfig;
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

    public <T> void sendMessage(String routingKeyPrefix, T message) {
        try {
            String routingKey = routingKeyPrefix + "." + instanceId + ".create";
            rabbitTemplate.convertAndSend(RabbitMQConfig.ACQUISITION_EXCHANGE_NAME, routingKey, message);
            logger.info("Mensagem enviada com sucesso para o Exchange '{}' com routingKey '{}'", RabbitMQConfig.ACQUISITION_EXCHANGE_NAME, routingKey);
        } catch (Exception e) {
            logger.error("Erro ao enviar mensagem para RabbitMQ: {}", e.getMessage(), e);
        }
    }


    public void sendBookSyncEvent(BookSyncDTO bookSyncDTO) {
        try {
            String routingKey = "acquisition.approve." + instanceId;
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ACQUISITION_EXCHANGE_NAME,
                    routingKey,
                    bookSyncDTO
            );
            logger.info("Evento de sincronização de livro enviado para aprovação: {}", bookSyncDTO);
        } catch (Exception e) {
            logger.error("Erro ao enviar evento de aprovação: {}", e.getMessage());
        }
    }


    public <T> void sendStatusSyncMessage(T message) {
        try {
            String routingKey = "acquisition.status.sync"; // Routing key genérica para todas as queues de status
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ACQUISITION_EXCHANGE_NAME,
                    routingKey,
                    message
            );
            logger.info("Mensagem de status enviada para o Exchange '{}' com routingKey '{}': {}",
                    RabbitMQConfig.ACQUISITION_EXCHANGE_NAME, routingKey, message);
        } catch (Exception e) {
            logger.error("Erro ao enviar mensagem de status para RabbitMQ: {}", e.getMessage(), e);
        }
    }
}
