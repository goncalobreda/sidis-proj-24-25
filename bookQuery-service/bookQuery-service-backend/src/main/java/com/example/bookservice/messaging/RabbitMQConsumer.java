package com.example.bookservice.messaging;

import com.example.bookservice.model.Book;
import com.example.bookservice.repositories.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConsumer {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConsumer.class);

    private final BookRepository bookRepository;

    public RabbitMQConsumer(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void receiveBookSyncEvent(Book book) {
        try {
            logger.info("Recebendo evento de sincronização de livro: {}", book.getBookID());

            // Sincroniza o livro no banco de dados local
            bookRepository.findById(book.getBookID()).ifPresentOrElse(
                    existingBook -> {
                        existingBook.setTitle(book.getTitle());
                        existingBook.setGenre(book.getGenre());
                        existingBook.setDescription(book.getDescription());
                        bookRepository.save(existingBook);
                        logger.info("Livro sincronizado com sucesso: {}", existingBook.getBookID());
                    },
                    () -> {
                        bookRepository.save(book);
                        logger.info("Livro criado na sincronização: {}", book.getBookID());
                    }
            );
        } catch (Exception e) {
            logger.error("Erro ao sincronizar livro: {}", e.getMessage());
        }
    }
}
