package com.example.bookservice.service;

import com.example.bookservice.messaging.RabbitMQProducer;
import com.example.bookservice.model.Author;
import com.example.bookservice.repositories.AuthorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuthorServiceImpl implements AuthorService {

    private static final Logger logger = LoggerFactory.getLogger(AuthorServiceImpl.class);

    private final AuthorRepository authorRepository;
    private final RabbitMQProducer rabbitMQProducer;

    public AuthorServiceImpl(AuthorRepository authorRepository, RabbitMQProducer rabbitMQProducer) {
        this.authorRepository = authorRepository;
        this.rabbitMQProducer = rabbitMQProducer;
    }

    @Override
    public Author create(CreateAuthorRequest request) {
        logger.info("Criando novo autor: {}", request.getName());

        // Validação básica
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("O nome do autor não pode ser vazio.");
        }

        Author author = new Author(request.getName(), request.getBiography());
        author = authorRepository.save(author);

        // Envio de evento de criação para RabbitMQ
        rabbitMQProducer.sendAuthorEvent("create", author);
        logger.info("Autor criado com sucesso e sincronizado: {}", author.getAuthorID());

        return author;
    }

    @Override
    public Author partialUpdate(String authorID, EditAuthorRequest request, long version) {
        logger.info("Atualizando autor com ID: {}", authorID);

        Author author = authorRepository.findByAuthorID(authorID)
                .orElseThrow(() -> new IllegalArgumentException("Autor não encontrado com o ID fornecido: " + authorID));

        if (request.getName() != null) {
            author.setName(request.getName());
        }

        if (request.getBiography() != null) {
            author.setBiography(request.getBiography());
        }

        author.setVersion(version); // Atualização de versão
        author = authorRepository.save(author);

        // Envio de evento de atualização para RabbitMQ
        rabbitMQProducer.sendAuthorEvent("update", author);
        logger.info("Autor atualizado com sucesso e sincronizado: {}", author.getAuthorID());

        return author;
    }

}
