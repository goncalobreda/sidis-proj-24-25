package com.example.readerserviceCommand.messaging;

import com.example.readerserviceCommand.config.RabbitMQConfig;
import com.example.readerserviceCommand.dto.PartialUpdateDTO;
import com.example.readerserviceCommand.dto.UserSyncDTO;
import com.example.readerserviceCommand.model.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.example.readerserviceCommand.config.RabbitMQConfig.READER_SERVICE_EXCHANGE;

@Component
public class RabbitMQProducer {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQProducer.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${instance.id}")
    private String instanceId;

    public RabbitMQProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        logger.info("RabbitMQProducer iniciado com instanceId: {}", instanceId);
    }

    public void sendSyncMessage(Reader reader) {
        // Converter Reader para UserSyncDTO
        UserSyncDTO userSyncDTO = new UserSyncDTO(
                reader.getEmail(),
                reader.getFullName(),
                reader.getPassword(),
                reader.isEnabled(),
                instanceId,
                reader.getPhoneNumber()
        );

        // Logando a mensagem antes de enviar
        logger.info("Enviando mensagem de sincronização para o RabbitMQ: {}", userSyncDTO);

        try {
            // Enviar a mensagem para a fila de sincronização com o routing key configurado
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "user.sync.#", userSyncDTO);  // Alterado para a exchange e routing key do Auth
            logger.info("Mensagem enviada com sucesso para sincronizar Reader: {}", userSyncDTO);
        } catch (Exception e) {
            logger.error("Erro ao enviar mensagem de sincronização: {}", e.getMessage());
        }
    }

    public void sendPartialUpdateMessage(PartialUpdateDTO message) {
        message.setOriginInstanceId(instanceId);
        message.setMessageId(UUID.randomUUID().toString()); // Gera um ID único para a mensagem

        logger.info("Antes de enviar: OriginInstanceId = {}", message.getOriginInstanceId());
        logger.info("Mensagem completa: {}", message);


        try {
            // Construir a routing key com a lógica necessária
            String routingKey = "reader.partial.update." + instanceId;

            // Logando detalhes antes de enviar
            logger.info("Enviando mensagem para RabbitMQ.");
            logger.info("Exchange: reader-service-exchange");
            logger.info("Routing Key: {}", routingKey);
            logger.info("Mensagem: {}", message);

            // Enviar a mensagem para o RabbitMQ
            rabbitTemplate.convertAndSend("reader-service-exchange", routingKey, message);

            // Log após sucesso
            logger.info("Mensagem enviada com sucesso para o Exchange 'reader-service-exchange' com routingKey '{}'", routingKey);

        } catch (Exception e) {
            // Logando erros, se ocorrerem
            logger.error("Erro ao enviar mensagem para RabbitMQ: {}", e.getMessage(), e);
        }
    }


}
