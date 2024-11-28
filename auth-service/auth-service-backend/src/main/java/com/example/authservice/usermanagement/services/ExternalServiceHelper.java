package com.example.authservice.usermanagement.services;

import com.example.authservice.dto.CreateReaderRequestDTO;
import com.example.authservice.messaging.RabbitMQProducer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExternalServiceHelper {

    private static final Logger logger = LoggerFactory.getLogger(ExternalServiceHelper.class);

    private final RabbitMQProducer rabbitMQProducer;

    public void registerReaderInService(CreateReaderRequestDTO request) {
        try {
            logger.info("Enviando mensagem para registrar o Reader: {}", request);
            rabbitMQProducer.sendMessage("reader-service.register", request);
            logger.info("Mensagem enviada com sucesso para o RabbitMQ.");
        } catch (Exception e) {
            logger.error("Erro ao enviar mensagem para o RabbitMQ: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao registrar o leitor no Reader Service.", e);
        }
    }
}
