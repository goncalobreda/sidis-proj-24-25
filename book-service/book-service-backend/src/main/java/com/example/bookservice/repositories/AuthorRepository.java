package com.example.bookservice.repositories;

import com.example.bookservice.model.Author;
import com.example.bookservice.model.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository {

    List<Author> findByName(String name);

    Optional<Author> findByAuthorID(String authorID);

    List<Author> findByNameAndBiography(String name, String authorBiography);

    <S extends Author> S save(S entity);

    Optional<Author> findTopByOrderByAuthorIDDesc();


    List<Book> findByAuthorsContaining(Author author);

}
