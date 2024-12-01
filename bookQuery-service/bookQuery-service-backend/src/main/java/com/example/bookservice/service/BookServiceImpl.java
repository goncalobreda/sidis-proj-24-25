package com.example.bookservice.service;


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

    @PersistenceContext
    private EntityManager entityManager;


    private final BookRepository bookRepository;

    private final BookImageRepository bookImageRepository;

    private final AuthorRepository authorRepository;



    @Autowired
    public BookServiceImpl(BookRepository bookRepository, BookImageRepository bookImageRepository, GenreRepository genreRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.bookImageRepository = bookImageRepository;
        this.genreRepository = genreRepository;
        this.authorRepository = authorRepository;
    }

    @Override
    public boolean isBookIDUnique(Long bookID) {
        return bookRepository.findBookByBookID(bookID).isEmpty();
    }

    @Override
    public Optional<Book> getBookById(final Long bookID) {
        return bookRepository.findById(bookID);
    }

    public List<Book> getAll() {
        return bookRepository.findAll();
    }


    @Override
    public Optional<Book> getBookByIsbn(final String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    @Override
    public List<Book> getBookByGenre(final String genre) {
        return bookRepository.findByGenre(genre);
    }

    @Override
    public Genre getGenreByInterest(String interest) {
        return genreRepository.findByInterest(interest);
    }

    @Override
    public List<Book> getBookByTitle(final String title) {
        return bookRepository.findByTitle(title);
    }

    private GenreRepository genreRepository;

    @Override
    public List<Map.Entry<String, Long>> findTop5Genres() {
        List<Genre> genres = genreRepository.findAll();
        Map<String, Long> genreBookCount = new HashMap<>();

        for (Genre genre : genres) {
            long count = genre.getBooks().size();
            genreBookCount.put(genre.getInterest(), count);
        }

        return genreBookCount.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());
    }


    @Override
    public List<Book> getBooksByAuthorId(String authorID) {
        return bookRepository.findByAuthorId(authorID);
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
