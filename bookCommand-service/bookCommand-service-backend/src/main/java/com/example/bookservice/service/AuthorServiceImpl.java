package com.example.bookservice.service;

import com.example.bookservice.dto.AuthorDTO;
import com.example.bookservice.exceptions.NotFoundException;
import com.example.bookservice.messaging.RabbitMQProducer;
import com.example.bookservice.model.Author;
import com.example.bookservice.repositories.AuthorRepository;
import org.springframework.stereotype.Service;

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
    public Author partialUpdate(String authorID, EditAuthorRequest request, long version) {
        Author author = authorRepository.findByAuthorID(authorID)
                .orElseThrow(() -> new NotFoundException("Author not found: " + authorID));

        if (request.getName() != null) {
            author.setName(request.getName());
        }

        if (request.getBiography() != null) {
            author.setBiography(request.getBiography());
        }

        author.setVersion(version);
        author = authorRepository.save(author);
        rabbitMQProducer.sendAuthorEvent("update", author);
        return author;
    }

    @Override
    public void syncAuthor(AuthorDTO authorDTO) {
        Author author = authorRepository.findByAuthorID(authorDTO.getAuthorID())
                .orElse(new Author());

        author.setAuthorID(authorDTO.getAuthorID());
        author.setName(authorDTO.getName());
        author.setBiography(authorDTO.getBiography());

        authorRepository.save(author);
    }
}

