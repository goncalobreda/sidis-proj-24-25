package com.example.lendingserviceQuery.messaging;

import com.example.lendingserviceQuery.dto.UserSyncDTO;
import com.example.lendingserviceQuery.model.Lending;
import com.example.lendingserviceQuery.model.Reader;
import com.example.lendingserviceQuery.repositories.LendingRepository;
import com.example.lendingserviceQuery.repositories.ReaderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class RabbitMQConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQConsumer.class);

    private final ReaderRepository readerRepository;
    private final LendingRepository lendingRepository;

    @Value("${rabbitmq.create.queue.name}")
    private String createQueueName;

    @Value("${rabbitmq.partial.update.queue.name}")
    private String partialUpdateQueueName;

    @Value("${rabbitmq.reader.queue.name}")
    private String readerSyncQueueName;

    public RabbitMQConsumer(ReaderRepository readerRepository, LendingRepository lendingRepository) {
        this.readerRepository = readerRepository;
        this.lendingRepository = lendingRepository;
    }

    // ------------------
    // READER SYNC
    // ------------------
    @RabbitListener(queues = "${rabbitmq.reader.queue.name}")
    public void processReaderSync(UserSyncDTO userSyncDTO) {
        LOGGER.info("Mensagem recebida para sincronizar Reader: {}", userSyncDTO);

        String userEmail = userSyncDTO.getUsername();
        Optional<Reader> existingReaderOpt = readerRepository.findByEmail(userEmail);

        if (existingReaderOpt.isPresent()) {
            Reader existingReader = existingReaderOpt.get();
            existingReader.setFullName(userSyncDTO.getFullName());
            existingReader.setEnabled(userSyncDTO.isEnabled());
            readerRepository.save(existingReader);
            LOGGER.info("Reader atualizado com sucesso: {}", existingReader);
        } else {
            Reader newReader = new Reader();
            newReader.setEmail(userEmail);
            newReader.setFullName(userSyncDTO.getFullName());
            newReader.setEnabled(userSyncDTO.isEnabled());
            newReader.setCreatedAt(LocalDateTime.now());
            newReader.setUniqueReaderID();
            readerRepository.save(newReader);
            LOGGER.info("Novo Reader criado com sucesso: {}", newReader);
        }
    }

    // ------------------
    // LENDING CREATE
    // ------------------
    @RabbitListener(queues = "${rabbitmq.create.queue.name}")
    public void processLendingCreate(Lending lending) {
        LOGGER.info("Mensagem recebida para CRIAR Lending (Query): {}", lending);

        Optional<Lending> existingOpt = lendingRepository.findByLendingID(lending.getLendingID());
        if (existingOpt.isPresent()) {
            // Se já existir, podes decidir sobrescrever ou ignorar.
            Lending existing = existingOpt.get();
            updateLendingFields(existing, lending);
            lendingRepository.save(existing);
            LOGGER.info("Lending (create) sobrescrito/atualizado: {}", existing);
        } else {
            lendingRepository.save(lending);
            LOGGER.info("Novo Lending (create) criado com sucesso: {}", lending);
        }
    }

    // ------------------
    // LENDING PARTIAL UPDATE
    // ------------------
    @RabbitListener(queues = "${rabbitmq.partial.update.queue.name}")
    public void processLendingPartialUpdate(Lending lending) {
        LOGGER.info("Mensagem recebida para PARTIAL UPDATE Lending (Query): {}", lending);

        Optional<Lending> existingOpt = lendingRepository.findByLendingID(lending.getLendingID());
        if (existingOpt.isPresent()) {
            Lending existing = existingOpt.get();
            updateLendingFields(existing, lending);
            lendingRepository.save(existing);
            LOGGER.info("Lending (partial update) atualizado com sucesso: {}", existing);
        } else {
            // Se não existir, podes criar ou ignorar. Normalmente cria-se para garantir consistência.
            lendingRepository.save(lending);
            LOGGER.info("Lending não existia, mas partial update chegou. Novo Lending criado: {}", lending);
        }
    }

    private void updateLendingFields(Lending target, Lending source) {
        target.setBookID(source.getBookID());
        target.setReaderID(source.getReaderID());
        target.setStartDate(source.getStartDate());
        target.setExpectedReturnDate(source.getExpectedReturnDate());
        target.setReturnDate(source.getReturnDate());
        target.setOverdue(source.isOverdue());
        target.setFine(source.getFine());
        target.setNotes(source.getNotes());
        // Se retirares @Version completamente, não tens target.setVersion(...).
    }
}
