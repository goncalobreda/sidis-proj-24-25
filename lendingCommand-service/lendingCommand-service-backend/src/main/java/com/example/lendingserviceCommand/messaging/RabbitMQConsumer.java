package com.example.lendingserviceCommand.messaging;

import com.example.lendingserviceCommand.dto.UserSyncDTO;
import com.example.lendingserviceCommand.model.Lending;
import com.example.lendingserviceCommand.model.Reader;
import com.example.lendingserviceCommand.repositories.LendingRepository;
import com.example.lendingserviceCommand.repositories.ReaderRepository;
import com.example.lendingserviceCommand.service.LendingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RabbitMQConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQConsumer.class);

    private final LendingRepository lendingRepository;

    public RabbitMQConsumer(LendingRepository lendingRepository) {
        this.lendingRepository = lendingRepository;
    }

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void processLendingSync(Lending lending) {
        LOGGER.info("Recebendo Lending para sincronização: {}", lending);

        // Atualizar ou salvar o Lending
        lendingRepository.findByLendingID(lending.getLendingID()).ifPresentOrElse(
                existing -> {
                    existing.setBookID(lending.getBookID());
                    existing.setReaderID(lending.getReaderID());
                    existing.setStartDate(lending.getStartDate());
                    existing.setExpectedReturnDate(lending.getExpectedReturnDate());
                    existing.setReturnDate(lending.getReturnDate());
                    existing.setOverdue(lending.isOverdue());
                    existing.setFine(lending.getFine());
                    existing.setNotes(lending.getNotes());
                    existing.setVersion(lending.getVersion());
                    lendingRepository.save(existing);
                    LOGGER.info("Lending atualizado: {}", existing);
                },
                () -> {
                    lendingRepository.save(lending);
                    LOGGER.info("Novo Lending salvo: {}", lending);
                }
        );
    }
}
