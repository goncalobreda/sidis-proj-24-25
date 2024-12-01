package com.example.bookservice.service;

import com.example.bookservice.model.*;

import java.util.List;
import java.util.Optional;

public interface AuthorService {

    List<Author> findByName(String name);

    Optional<Author> findByAuthorID(String authorID);

    List<CoAuthorDTO> getCoAuthorsAndBooks(String authorId);

    Optional<Author> getLastId();

    Optional<AuthorDTO> getAuthorAndBooks(String authorId);
}
