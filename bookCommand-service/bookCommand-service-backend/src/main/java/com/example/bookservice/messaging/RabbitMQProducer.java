package com.example.bookservice.messaging;

import com.example.bookservice.dto.BookSyncDTO;
import com.example.bookservice.model.Author;
import com.example.bookservice.model.Book;
import com.example.bookservice.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQProducer {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }


    public void sendBookSyncEvent(Book book) {
        try {
            BookSyncDTO bookSyncDTO = new BookSyncDTO(
                    book.getIsbn(),
                    book.getTitle(),
                    book.getGenre().getInterest(),
                    book.getDescription()
            );

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.BOOK_COMMAND_EXCHANGE,
                    RabbitMQConfig.BOOK_ROUTING_KEY,
                    bookSyncDTO
            );

            logger.info("Evento de sincronização enviado: {}", bookSyncDTO);
        } catch (Exception e) {
            logger.error("Erro ao enviar evento de sincronização: {}", e.getMessage(), e);
        }
    }


    public void sendAuthorEvent(String action, Author author) {
        try {
            // Crie uma mensagem contendo a ação e o autor
            AuthorEventMessage message = new AuthorEventMessage(action, author.getAuthorID(), author.getName(), author.getBiography());

            // Envie a mensagem para o RabbitMQ
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.AUTHOR_COMMAND_EXCHANGE,
                    RabbitMQConfig.AUTHOR_ROUTING_KEY,
                    message
            );
            logger.info("Evento de autor enviado: ação={}, autorID={}", action, author.getAuthorID());
        } catch (Exception e) {
            logger.error("Erro ao enviar evento de autor: {}", e.getMessage(), e);
        }
    }
}
