package com.example.bookservice.messaging;

import com.example.bookservice.dto.BookSyncDTO;
import com.example.bookservice.dto.PartialUpdateDTO;
import com.example.bookservice.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConsumer {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConsumer.class);

    private final BookService bookService;

    @Value("${instance.id}")
    private String instanceId;

    public RabbitMQConsumer(BookService bookService) {
        this.bookService = bookService;
    }

    @RabbitListener(queues = "${rabbitmq.queue.book.sync.query1}")
    public void processSyncMessageQuery1(BookSyncDTO bookSyncDTO) {
        logger.info("Mensagem recebida para sincronizar livro (Query1): {}", bookSyncDTO);
        try {
            bookService.createOrUpdateFromBookSyncDTO(bookSyncDTO);
            logger.info("Livro sincronizado com sucesso no Query1: {}", bookSyncDTO.getBookID());
        } catch (Exception e) {
            logger.error("Erro ao sincronizar livro no Query1: {}", e.getMessage(), e);
        }
    }

    @RabbitListener(queues = "${rabbitmq.queue.book.sync.query2}")
    public void processSyncMessageQuery2(BookSyncDTO bookSyncDTO) {
        logger.info("Mensagem recebida para sincronizar livro (Query2): {}", bookSyncDTO);
        try {
            bookService.createOrUpdateFromBookSyncDTO(bookSyncDTO);
            logger.info("Livro sincronizado com sucesso no Query2: {}", bookSyncDTO.getBookID());
        } catch (Exception e) {
            logger.error("Erro ao sincronizar livro no Query2: {}", e.getMessage(), e);
        }
    }

    @RabbitListener(queues = "${rabbitmq.partial.update.queue.name}")
    public void processPartialUpdate(PartialUpdateDTO partialUpdateDTO) {
        logger.info("Mensagem recebida para atualização parcial: {}", partialUpdateDTO);

        if (instanceId.equals(partialUpdateDTO.getOriginInstanceId())) {
            logger.info("Mensagem ignorada, enviada pela própria instância: {}", instanceId);
            return;
        }

        try {
            bookService.applyPartialUpdate(partialUpdateDTO);
            logger.info("Atualização parcial aplicada com sucesso para o livro: {}", partialUpdateDTO.getBookID());
        } catch (Exception e) {
            logger.error("Erro ao aplicar atualização parcial: {}", e.getMessage(), e);
        }
    }
}
