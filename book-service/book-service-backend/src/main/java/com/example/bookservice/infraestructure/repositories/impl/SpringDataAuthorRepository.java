package com.example.bookservice.infraestructure.repositories.impl;

import com.example.bookservice.model.Author;
import com.example.bookservice.repositories.AuthorRepository;
import com.example.bookservice.model.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpringDataAuthorRepository extends AuthorRepository, CrudRepository<Author, Long> {

    @Override
    @Query("SELECT a FROM Author a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Author> findByName(@Param("name") String name);

    @Override
    @Query("SELECT a FROM Author a WHERE a.authorID LIKE :authorID")
    Optional<Author> findByAuthorID(@Param("authorID") String authorID);

    @Query("SELECT a FROM Author a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%')) AND a.biography = :authorBiography")
    List<Author> findByNameAndBiography(@Param("name") String name, @Param("authorBiography") String authorBiography);

    @Override
    @Query("SELECT a FROM Author a ORDER BY substring(a.authorID, 1, 4), cast(substring(a.authorID, 6, 10) AS int) DESC LIMIT 1")
    Optional<Author> findTopByOrderByAuthorIDDesc();

    @Query("SELECT b FROM Book b JOIN b.author a WHERE a = :author")
    List<Book> findByAuthorsContaining(@Param("author") Author author);


}



