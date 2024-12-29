package com.example.bookservice.messaging;

import com.example.bookservice.config.RabbitMQConfig;
import com.example.bookservice.dto.AuthorDTO;
import com.example.bookservice.dto.BookCreationResponseDTO;
import com.example.bookservice.dto.BookSyncDTO;
import com.example.bookservice.model.Author;
import com.example.bookservice.model.Book;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMQProducer {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQProducer.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.book-service:book-service-exchange}")
    private String bookServiceExchangeName;

    @Value("${instance.id}")
    private String instanceId;

    @Value("${rabbitmq.exchange.name}")
    private String bookCommandExchange;

    public void sendBookSyncEvent(Book book) {
        try {
            BookSyncDTO eventDTO = BookSyncDTO.fromBook("sync", book, instanceId);

            // Enviar para a instância do Query
            rabbitTemplate.convertAndSend(bookCommandExchange, "book.sync.query.book1", eventDTO);
            logger.info("Book sync event sent to Query: {}", eventDTO);

            // Enviar para a segunda instância do Command
            rabbitTemplate.convertAndSend(bookCommandExchange, "book.sync.command.book2", eventDTO);
            logger.info("Book sync event sent to Command Instance 2: {}", eventDTO);
        } catch (Exception e) {
            logger.error("Erro ao enviar evento de sincronização do livro: {}", e.getMessage(), e);
        }
    }


    public void sendPartialUpdateEvent(Book book) {
        try {
            BookSyncDTO eventDTO = BookSyncDTO.fromBook("update", book, instanceId);
            rabbitTemplate.convertAndSend(bookCommandExchange, "book.partial.update." + instanceId, eventDTO); // Utiliza a exchange da propriedade
            logger.info("Book partial update event sent: {}", eventDTO);
        } catch (Exception e) {
            logger.error("Erro ao enviar evento de atualização parcial do livro: {}", e.getMessage(), e);
        }
    }

    public void sendAuthorEvent(String action, Author author) {
        try {
            AuthorDTO authorDTO = AuthorDTO.fromAuthor(author);
            rabbitTemplate.convertAndSend(bookCommandExchange, "author.sync." + instanceId, authorDTO); // Utiliza a exchange da propriedade
            logger.info("Author event sent: {}", authorDTO);
        } catch (Exception e) {
            logger.error("Erro ao enviar evento de autor: {}", e.getMessage(), e);
        }
    }

    public void sendBookCreationResponse(String isbn, boolean success, String errorReason) {
        BookCreationResponseDTO responseDTO = new BookCreationResponseDTO(isbn, success, errorReason);

        final String routingKey = "acquisition.book.creation.result";

        try {
            rabbitTemplate.convertAndSend(
                    bookServiceExchangeName, // "book-service-exchange"
                    routingKey,              // "acquisition.book.creation.result"
                    responseDTO
            );
            logger.info("[Book] Enviando BookCreationResponseDTO -> exchange={}, rk={}: {}",
                    bookServiceExchangeName, routingKey, responseDTO);
        } catch (Exception e) {
            logger.error("[Book] Erro ao enviar BookCreationResponseDTO: {}", e.getMessage(), e);
        }
    }


}
