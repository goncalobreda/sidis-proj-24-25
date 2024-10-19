package com.example.bookservice.service;

import com.example.bookservice.model.Author;
import com.example.bookservice.model.CoAuthorDTO;
import com.example.bookservice.model.Book;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AuthorService {

    List<Author> findByName(String name);

    Optional<Author> findByAuthorID(String authorID);

    Author create(CreateAuthorRequest request);

    Author partialUpdate(String authorID, EditAuthorRequest request, long parseLong);

    List<CoAuthorDTO> getCoAuthorsAndBooks(String authorId);


}
