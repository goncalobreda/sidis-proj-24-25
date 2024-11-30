package com.example.bookservice.service;

import com.example.bookservice.messaging.RabbitMQProducer;
import com.example.bookservice.model.Author;
import com.example.bookservice.repositories.AuthorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final RabbitMQProducer rabbitMQProducer;

    public AuthorServiceImpl(AuthorRepository authorRepository, RabbitMQProducer rabbitMQProducer) {
        this.authorRepository = authorRepository;
        this.rabbitMQProducer = rabbitMQProducer;
    }

    @Override
    public Author create(CreateAuthorRequest request) {
        Author author = new Author(request.getName(), request.getBiography());
        author = authorRepository.save(author);
        rabbitMQProducer.sendAuthorEvent("create", author);
        return author;
    }

    @Override
    public List<Author> findByName(String name) {
        return authorRepository.findByName(name);
    }

    @Override
    public Optional<Author> findByAuthorID(String authorID) {
        return authorRepository.findByAuthorID(authorID);
    }

    @Override
    public Author partialUpdate(String authorID, EditAuthorRequest request, long version) {
        Author author = authorRepository.findByAuthorID(authorID)
                .orElseThrow(() -> new IllegalArgumentException("Author not found"));

        author.applyPatch(version, request.getName(), request.getBiography());
        author = authorRepository.save(author);
        rabbitMQProducer.sendAuthorEvent("update", author);
        return author;
    }
}
