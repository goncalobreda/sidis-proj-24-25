package com.example.authserviceCommand.messaging;

import com.example.authserviceCommand.configuration.RabbitMQConfig;
import com.example.authserviceCommand.dto.UserSyncDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMQProducer {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQProducer.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${instance.id}")
    private String instanceId;

    /**
     * Envia uma mensagem para RabbitMQ usando uma `routingKeyPrefix`.
     *
     * @param routingKeyPrefix Prefixo da routing key.
     * @param message          Objeto a ser enviado.
     * @param <T>              Tipo do objeto.
     */
    public <T> void sendMessage(String routingKeyPrefix, T message) {
        if (message instanceof UserSyncDTO) {
            ((UserSyncDTO) message).setOriginInstanceId(instanceId);
        }
        try {
            // Construir a routingKey com base no prefixo e instância atual
            String routingKey = routingKeyPrefix + "." + instanceId + ".create";

            logger.info("Enviando mensagem para RabbitMQ: routingKey={}, message={}", routingKey, message);

            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, routingKey, message);

            logger.info("Mensagem enviada com sucesso para o Exchange '{}' com routingKey '{}'", RabbitMQConfig.EXCHANGE_NAME, routingKey);
        } catch (Exception e) {
            logger.error("Erro ao enviar mensagem para RabbitMQ: {}", e.getMessage());
        }
    }

    /**
     * Envia uma mensagem de bootstrap para RabbitMQ.
     */
    public void sendBootstrapMessage(String bootstrapMessage) {
        String routingKey = "bootstrap.sync." + instanceId;
        String message = "Bootstrap completo pela instância " + instanceId;

        try {
            logger.info("Enviando mensagem de bootstrap: routingKey={}, message={}", routingKey, message);

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    routingKey,
                    message,
                    msg -> {
                        // Configurar manualmente o tipo da mensagem para ser interpretado como String
                        msg.getMessageProperties().getHeaders().put("__TypeId__", "java.lang.String");
                        return msg;
                    }
            );

            logger.info("Mensagem de bootstrap enviada com sucesso para o Exchange '{}' com routingKey '{}'", RabbitMQConfig.EXCHANGE_NAME, routingKey);
        } catch (Exception e) {
            logger.error("Erro ao enviar mensagem de bootstrap: {}", e.getMessage());
        }
    }
}
