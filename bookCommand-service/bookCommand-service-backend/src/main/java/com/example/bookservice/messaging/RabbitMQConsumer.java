package com.example.bookservice.messaging;

import com.example.bookservice.dto.AuthorDTO;
import com.example.bookservice.dto.BookSyncDTO;
import com.example.bookservice.service.AuthorService;
import com.example.bookservice.service.BookService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMQConsumer {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConsumer.class);

    private final BookService bookService;
    private final AuthorService authorService;

    @Value("${instance.id}")
    private String instanceId;

    @RabbitListener(queues = "${rabbitmq.queue.book.sync}")
    public void receiveBookSyncMessage(BookSyncDTO bookSyncDTO) {
        logger.info("Mensagem de sincronização de livro recebida: {}", bookSyncDTO);

        try {
            bookService.syncBook(bookSyncDTO);
            logger.info("Livro sincronizado com sucesso: {}", bookSyncDTO.getTitle());
        } catch (Exception e) {
            logger.error("Erro ao sincronizar livro: {}", e.getMessage(), e);
        }
    }

    @RabbitListener(queues = "${rabbitmq.queue.author.sync}")
    public void receiveAuthorSyncMessage(AuthorDTO authorDTO) {
        logger.info("Mensagem de sincronização de autor recebida: {}", authorDTO);

        try {
            authorService.syncAuthor(authorDTO);
            logger.info("Autor sincronizado com sucesso: {}", authorDTO.getName());
        } catch (Exception e) {
            logger.error("Erro ao sincronizar autor: {}", e.getMessage(), e);
        }
    }
}
