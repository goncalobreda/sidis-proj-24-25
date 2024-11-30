package com.example.bookservice.messaging;

import com.example.bookservice.config.RabbitMQConfig;
import com.example.bookservice.dto.BookEventDTO;
import com.example.bookservice.model.Author;
import com.example.bookservice.model.Book;
import com.example.bookservice.model.Genre;
import com.example.bookservice.repositories.AuthorRepository;
import com.example.bookservice.repositories.BookRepository;
import com.example.bookservice.repositories.GenreRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RabbitMQConsumer {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConsumer.class);

    private final BookRepository bookRepository;
    private final GenreRepository genreRepository;
    private final AuthorRepository authorRepository;

    public RabbitMQConsumer(BookRepository bookRepository, GenreRepository genreRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.genreRepository = genreRepository;
        this.authorRepository = authorRepository;
    }

    @RabbitListener(queues = RabbitMQConfig.BOOK_EVENT_QUEUE)
    public void handleBookEvent(BookEventDTO bookEventDTO) {
        logger.info("Received book event: {}", bookEventDTO);

        switch (bookEventDTO.getEventType()) {
            case "create":
                saveOrUpdateBook(bookEventDTO);
                break;
            case "update":
                saveOrUpdateBook(bookEventDTO);
                break;
            default:
                logger.warn("Unhandled event type: {}", bookEventDTO.getEventType());
        }
    }

    private void saveOrUpdateBook(BookEventDTO bookEventDTO) {
        Genre genre = genreRepository.findByInterest(bookEventDTO.getGenre())
                .orElseThrow(() -> new IllegalArgumentException("Genre not found"));

        List<Author> authors = bookEventDTO.getAuthorIds().stream()
                .map(authorId -> authorRepository.findByAuthorID(authorId)
                        .orElseThrow(() -> new IllegalArgumentException("Author not found")))
                .collect(Collectors.toList());

        Book book = new Book(
                bookEventDTO.getIsbn(),
                bookEventDTO.getTitle(),
                genre,
                bookEventDTO.getDescription(),
                authors,
                null
        );

        book.setBookID(bookEventDTO.getBookID());
        book.setVersion(bookEventDTO.getVersion());
        bookRepository.save(book);
    }
}
