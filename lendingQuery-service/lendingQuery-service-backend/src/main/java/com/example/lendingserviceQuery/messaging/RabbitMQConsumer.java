package com.example.lendingserviceQuery.messaging;

import com.example.lendingserviceQuery.dto.UserSyncDTO;
import com.example.lendingserviceQuery.model.Lending;
import com.example.lendingserviceQuery.model.Reader;
import com.example.lendingserviceQuery.repositories.LendingRepository;
import com.example.lendingserviceQuery.repositories.ReaderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class RabbitMQConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQConsumer.class);

    private final ReaderRepository readerRepository;
    private final LendingRepository lendingRepository;

    public RabbitMQConsumer(ReaderRepository readerRepository, LendingRepository lendingRepository) {
        this.readerRepository = readerRepository;
        this.lendingRepository = lendingRepository;
    }

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

    @RabbitListener(queues = "${rabbitmq.lending.queue.name}")
    public void processLendingSync(Lending lending) {
        LOGGER.info("Mensagem recebida para sincronizar Lending: {}", lending);

        Optional<Lending> existingOpt = lendingRepository.findByLendingID(lending.getLendingID());
        if (existingOpt.isPresent()) {
            Lending existing = existingOpt.get();
            existing.setBookID(lending.getBookID());
            existing.setReaderID(lending.getReaderID());
            existing.setStartDate(lending.getStartDate());
            existing.setExpectedReturnDate(lending.getExpectedReturnDate());
            existing.setReturnDate(lending.getReturnDate());
            existing.setOverdue(lending.isOverdue());
            existing.setFine(lending.getFine());
            lendingRepository.save(existing);
            LOGGER.info("Lending atualizado com sucesso: {}", existing);
        } else {
            lendingRepository.save(lending);
            LOGGER.info("Novo Lending criado com sucesso: {}", lending);
        }
    }
}
