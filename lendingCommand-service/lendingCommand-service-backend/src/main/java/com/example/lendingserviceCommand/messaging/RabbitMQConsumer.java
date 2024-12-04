package com.example.lendingserviceCommand.messaging;

import com.example.lendingserviceCommand.dto.UserSyncDTO;
import com.example.lendingserviceCommand.model.Reader;
import com.example.lendingserviceCommand.repositories.ReaderRepository;
import com.example.lendingserviceCommand.service.LendingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RabbitMQConsumer {

    private final LendingService lendingService;
    private final ReaderRepository readerRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQConsumer.class);

    public RabbitMQConsumer(LendingService lendingService, ReaderRepository readerRepository) {
        this.lendingService = lendingService;
        this.readerRepository = readerRepository;
    }

    @RabbitListener(queues = "lending1.reader.sync.command.queue")
    public void processSyncMessage(UserSyncDTO userSyncDTO) {
        LOGGER.info("Mensagem recebida para sincronizar Lending: {}", userSyncDTO);

        String userEmail = userSyncDTO.getUsername();
        String userFullName = userSyncDTO.getFullName();

        // Verificar se o usuário já existe
        if (!readerRepository.existsByEmail(userEmail)) {
            LOGGER.info("Usuário não encontrado. Criando novo leitor com email: {}", userEmail);

            Reader newReader = new Reader();
            newReader.setEmail(userEmail);
            newReader.setFullName(userFullName);
            newReader.setCreatedAt(LocalDateTime.now());
            newReader.setEnabled(userSyncDTO.isEnabled());
            newReader.setUniqueReaderID();

            readerRepository.save(newReader);
            LOGGER.info("Novo leitor criado com sucesso: {}", newReader);
        } else {
            LOGGER.info("Usuário já existe: {}", userEmail);
        }

        LOGGER.info("Lending sincronizado com sucesso para o User: {}", userEmail);
    }
}
