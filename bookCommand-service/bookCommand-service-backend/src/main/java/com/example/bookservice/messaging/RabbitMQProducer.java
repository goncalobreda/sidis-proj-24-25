package com.example.bookservice.messaging;

import com.example.bookservice.config.RabbitMQConfig;
import com.example.bookservice.dto.BookEventDTO;
import com.example.bookservice.dto.AuthorEventDTO;
import com.example.bookservice.model.Book;
import com.example.bookservice.model.Author;
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

    public void sendBookEvent(String eventType, Book book) {
        BookEventDTO bookEventDTO = BookEventDTO.fromBook(eventType, book);

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.BOOK_COMMAND_EXCHANGE,
                    RabbitMQConfig.BOOK_ROUTING_KEY,
                    bookEventDTO
            );
            logger.info("Book event [{}] sent successfully: {}", eventType, bookEventDTO);
        } catch (Exception e) {
            logger.error("Failed to send book event: {}", e.getMessage(), e);
        }
    }

    public void sendAuthorEvent(String eventType, Author author) {
        AuthorEventDTO authorEventDTO = AuthorEventDTO.fromAuthor(eventType, author);

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.AUTHOR_COMMAND_EXCHANGE,
                    RabbitMQConfig.AUTHOR_ROUTING_KEY,
                    authorEventDTO
            );
            logger.info("Author event [{}] sent successfully: {}", eventType, authorEventDTO);
        } catch (Exception e) {
            logger.error("Failed to send author event: {}", e.getMessage(), e);
        }
    }
}
