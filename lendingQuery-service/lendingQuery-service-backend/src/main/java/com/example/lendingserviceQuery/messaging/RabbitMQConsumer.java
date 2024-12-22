package com.example.lendingserviceQuery.messaging;

import com.example.lendingserviceQuery.dto.UserSyncDTO;
import com.example.lendingserviceQuery.model.Lending;
import com.example.lendingserviceQuery.model.Reader;
import com.example.lendingserviceQuery.repositories.LendingRepository;
import com.example.lendingserviceQuery.repositories.ReaderRepository;
import com.example.lendingserviceQuery.service.LendingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class RabbitMQConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQConsumer.class);

    private final LendingService lendingService;  // se quiseres
    private final ReaderRepository readerRepository;
    private final LendingRepository lendingRepository;

    public RabbitMQConsumer(
            LendingService lendingService,
            ReaderRepository readerRepository,
            LendingRepository lendingRepository
    ) {
        this.lendingService = lendingService;
        this.readerRepository = readerRepository;
        this.lendingRepository = lendingRepository;
    }

    /**
     * 1) Recebe Readers (UserSyncDTO) via "lending1.reader.sync.command.queue"
     */
    @RabbitListener(queues = "lending1.reader.sync.command.queue")
    public void processSyncMessage(UserSyncDTO userSyncDTO) {
        LOGGER.info("Mensagem recebida para sincronizar Reader (LendingQuery): {}", userSyncDTO);

        String userEmail = userSyncDTO.getUsername();
        String userFullName = userSyncDTO.getFullName();

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
        LOGGER.info("Reader sincronizado com sucesso para o User: {}", userEmail);
    }

    /**
     * 2) Recebe Lendings no Query side via "lendingQuery.sync.queue"
     *    que está ligado a routingKey "lending.sync.#"
     */
    @RabbitListener(queues = "lendingQuery.sync.queue")
    public void processLendingSync(Lending incomingLending) {
        LOGGER.info("Recebido Lending via RabbitMQ (LendingQuery): {}", incomingLending);

        Optional<Lending> existingOpt = lendingRepository.findByLendingID(incomingLending.getLendingID());
        if (existingOpt.isPresent()) {
            // Atualiza
            Lending existing = existingOpt.get();
            existing.setBookID(incomingLending.getBookID());
            existing.setReaderID(incomingLending.getReaderID());
            existing.setStartDate(incomingLending.getStartDate());
            existing.setExpectedReturnDate(incomingLending.getExpectedReturnDate());
            existing.setReturnDate(incomingLending.getReturnDate());
            existing.setOverdue(incomingLending.isOverdue());
            existing.setFine(incomingLending.getFine());
            existing.setNotes(incomingLending.getNotes());
            existing.setVersion(incomingLending.getVersion());
            lendingRepository.save(existing);
            LOGGER.info("Lending atualizado na Query DB: {}", existing.getLendingID());
        } else {
            // Insere novo
            Lending saved = lendingRepository.save(incomingLending);
            LOGGER.info("Lending criado na Query DB: {}", saved.getLendingID());
        }
    }
}
