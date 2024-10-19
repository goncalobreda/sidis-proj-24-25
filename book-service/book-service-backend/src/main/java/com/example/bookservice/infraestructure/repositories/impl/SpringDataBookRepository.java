package com.example.bookservice.infraestructure.repositories.impl;

import com.example.bookservice.model.Book;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import com.example.bookservice.repositories.BookRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SpringDataBookRepository extends BookRepository, CrudRepository<Book, Long> {

    @Override
    @Query("SELECT b FROM Book b ORDER BY b.bookID DESC LIMIT 1")
    Optional<Book> getLastId();

    @Override
    @Query("SELECT b FROM Book b")
    List<Book> findAll();

    @Override
    @Query("SELECT b FROM Book b WHERE b.isbn LIKE :isbn")
    Optional<Book> findByIsbn(@Param("isbn") String isbn);

    @Query("SELECT b FROM Book b WHERE b.bookID = :bookID")
    Optional<Book> findById(@Param("bookID") Long bookID);

    @Query("SELECT b FROM Book b JOIN b.genre g WHERE LOWER(g.interest) LIKE LOWER(CONCAT('%', :genre, '%'))")
    List<Book> findByGenre(@Param("genre") String genre);

    @Override
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT(:title, '%'))")
    List<Book> findByTitle(@Param("title") String title);

    @Query("SELECT g.interest, COUNT(b) FROM Genre g JOIN g.books b GROUP BY g.interest ORDER BY COUNT(b) DESC LIMIT 5")
    List<Map.Entry<String, Long>> findTop5Genres();


    @Query("SELECT b FROM Book b JOIN b.author a WHERE a.authorID = :authorID")
    List<Book> findByAuthorId(@Param("authorID") String authorID);


}
