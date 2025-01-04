package com.example.lendingserviceCommand.messaging;

import com.example.lendingserviceCommand.model.Lending;
import com.example.lendingserviceCommand.repositories.LendingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQConsumer.class);

    private final LendingRepository lendingRepository;

    @Value("${rabbitmq.create.queue.name}")
    private String createQueueName;

    @Value("${rabbitmq.partial.update.queue.name}")
    private String partialUpdateQueueName;

    public RabbitMQConsumer(LendingRepository lendingRepository) {
        this.lendingRepository = lendingRepository;
    }

    /**
     * 1. Consumir mensagens de CREATE
     */
    @RabbitListener(queues = "${rabbitmq.create.queue.name}")
    public void processCreateMessage(Lending lending) {
        LOGGER.info("Recebido CREATE Lending: {}", lending);
        upsertLending(lending);
    }

    /**
     * 2. Consumir mensagens de PARTIAL UPDATE
     */
    @RabbitListener(queues = "${rabbitmq.partial.update.queue.name}")
    public void processPartialUpdateMessage(Lending lending) {
        LOGGER.info("Recebido PARTIAL UPDATE Lending: {}", lending);
        upsertLending(lending);
    }

    private void upsertLending(Lending incoming) {
        lendingRepository.findByLendingID(incoming.getLendingID()).ifPresentOrElse(
                existing -> {
                    existing.setBookID(incoming.getBookID());
                    existing.setReaderID(incoming.getReaderID());
                    existing.setStartDate(incoming.getStartDate());
                    existing.setExpectedReturnDate(incoming.getExpectedReturnDate());
                    existing.setReturnDate(incoming.getReturnDate());
                    existing.setOverdue(incoming.isOverdue());
                    existing.setFine(incoming.getFine());
                    existing.setNotes(incoming.getNotes());
                    //existing.setVersion(incoming.getVersion());
                    lendingRepository.save(existing);
                    LOGGER.info("Lending atualizado: {}", existing);
                },
                () -> {
                    lendingRepository.save(incoming);
                    LOGGER.info("Novo Lending criado: {}", incoming);
                }
        );
    }
}
