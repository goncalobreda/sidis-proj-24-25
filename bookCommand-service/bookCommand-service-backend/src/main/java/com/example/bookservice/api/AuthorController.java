package com.example.bookservice.api;

import com.example.bookservice.model.*;
import com.example.bookservice.service.AuthorServiceImpl;
import com.example.bookservice.service.CreateAuthorRequest;
import com.example.bookservice.service.EditAuthorRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Authors", description = "Endpoints for managing Authors")
@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    private static final Logger logger = LoggerFactory.getLogger(AuthorController.class);

    private final AuthorServiceImpl authorService;

    public AuthorController(AuthorServiceImpl authorService) {
        this.authorService = authorService;
    }

    @Operation(summary = "Creates a new Author")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Author> create(@Valid @RequestBody CreateAuthorRequest request) {
        logger.info("Recebendo solicitação para criar autor.");
        Author createdAuthor = authorService.create(request);
        logger.info("Autor criado com sucesso: {}", createdAuthor.getAuthorID());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAuthor);
    }

    @Operation(summary = "Updates an existing Author")
    @PatchMapping("/{id}")
    public ResponseEntity<Author> partialUpdate(
            @PathVariable("id") @Parameter(description = "The ID of the author to update") String authorID,
            @Valid @RequestBody EditAuthorRequest request) {
        logger.info("Recebendo solicitação para atualizar autor com ID: {}", authorID);
        Author updatedAuthor = authorService.partialUpdate(authorID, request, 1L); // Modifique para controle de versão real
        logger.info("Autor atualizado com sucesso: {}", updatedAuthor.getAuthorID());
        return ResponseEntity.ok(updatedAuthor);
    }
}
