package com.example.bookservice.repositories;

import com.example.bookservice.model.Book;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface BookRepository {

    List<Book> findByGenre(String genre);

    List<Book> findByTitle(String title);

    List<Book> findAll();

    Optional<Book> findByIsbn(String isbn);

    Optional<Book> findBookByBookID(Long bookID);

    Optional<Book> findById(Long bookID);

    Optional<Book> getLastId();

    List<Book> findByAuthorId(String authorID);

    Book save(Book book);

    List<Object[]> findTop5BooksNative();

}
