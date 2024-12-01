package com.example.bookservice.service;



import com.example.bookservice.messaging.RabbitMQProducer;
import com.example.bookservice.model.*;
import com.example.bookservice.repositories.AuthorRepository;
import com.example.bookservice.repositories.BookImageRepository;
import com.example.bookservice.repositories.GenreRepository;
import com.example.bookservice.exceptions.NotFoundException;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.example.bookservice.repositories.BookRepository;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final BookImageRepository bookImageRepository;
    private final RabbitMQProducer rabbitMQProducer;

    public BookServiceImpl(
            BookRepository bookRepository,
            AuthorRepository authorRepository,
            GenreRepository genreRepository,
            BookImageRepository bookImageRepository,
            RabbitMQProducer rabbitMQProducer) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.genreRepository = genreRepository;
        this.bookImageRepository = bookImageRepository;
        this.rabbitMQProducer = rabbitMQProducer;
    }

    @Override
    public Book create(CreateBookRequest request) {
        Genre genre = genreRepository.findByInterest(request.getGenre());
        if (genre == null) {
            throw new IllegalArgumentException("Genre not found: " + request.getGenre());
        }

        List<Author> authors = new ArrayList<>();
        for (String authorId : request.getAuthorIds()) {
            Author author = authorRepository.findByAuthorID(authorId)
                    .orElseThrow(() -> new IllegalArgumentException("Author not found with ID: " + authorId));
            authors.add(author);
        }

        Book book = new Book(
                request.getIsbn(),
                request.getTitle(),
                genre,
                request.getDescription(),
                authors,
                bookImageRepository.findById(request.getBookImageId())
                        .orElseThrow(() -> new IllegalArgumentException("Book image not found"))
        );

        book = bookRepository.save(book);
        rabbitMQProducer.sendBookEvent("create", book);

        return book;
    }

    @Override
    public Book partialUpdate(Long bookID, EditBookRequest request, long desiredVersion) {
        Book existingBook = bookRepository.findById(bookID)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        Genre genre = genreRepository.findByInterest(request.getGenre());
        if (genre == null) {
            throw new IllegalArgumentException("Genre not found: " + request.getGenre());
        }

        existingBook.applyPatch(desiredVersion, request.getTitle(), genre, request.getDescription());
        bookRepository.save(existingBook);
        rabbitMQProducer.sendBookEvent("update", existingBook);

        return existingBook;
    }


    @Override
    public boolean isBookIDUnique(Long bookID) {
        return bookRepository.findBookByBookID(bookID).isEmpty();
    }


    public void addImageToBook(Long bookID, byte[] image, String contentType) {
        Book book = bookRepository.findBookByBookID(bookID)
                .orElseThrow(() -> new NotFoundException("Book not found"));

        saveBookWithImage(book, image, contentType);
    }



    public void saveBookWithImage(Book book, byte[] image, String contentType) {
        Book savedBook = bookRepository.save(book); // Salva o livro no banco de dados

        BookImage bookImage = new BookImage();
        bookImage.setBook(savedBook); // Associa a imagem ao livro salvo
        bookImage.setImage(image);
        bookImage.setContentType(contentType);

        bookImageRepository.save(bookImage); // Salva a imagem no banco de dados
    }

/*
    @Override
    public List<BookCountDTO> findTop5Books() {
        List<LendingDTO> lendings = lendingServiceClient.getAllLendings();

        Map<Long, Long> bookIdCounts = lendings.stream()
                .collect(Collectors.groupingBy(LendingDTO::getBookID, Collectors.counting()));

        List<Map.Entry<Long, Long>> top5Books = bookIdCounts.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());

        return top5Books.stream()
                .map(entry -> new BookCountDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
*/

}
