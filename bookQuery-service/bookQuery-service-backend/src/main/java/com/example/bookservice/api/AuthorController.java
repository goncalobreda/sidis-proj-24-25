package com.example.bookservice.api;

import com.example.bookservice.model.*;
import com.example.bookservice.service.AuthorServiceImpl;
import com.example.bookservice.service.CreateAuthorRequest;
import com.example.bookservice.service.EditAuthorRequest;
import com.example.bookservice.service.BookServiceImpl;
import com.example.bookservice.exceptions.NotFoundException;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;


@Tag(name = "Authors", description = "Endpoints for managing Authors")
@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    private static final String IF_MATCH = "If-Match";

    private final AuthorServiceImpl authorService;
    private final BookServiceImpl bookService;
    private final BookViewMapper bookViewMapper;

    private final AuthorViewMapper authorViewMapper;

    private static final Logger logger = LoggerFactory.getLogger(AuthorController.class);


    @Autowired
    public AuthorController(AuthorServiceImpl authorService, BookServiceImpl bookService, BookViewMapper bookViewMapper, AuthorViewMapper authorViewMapper) {
        this.authorService = authorService;
        this.bookService = bookService;
        this.bookViewMapper = bookViewMapper;
        this.authorViewMapper = authorViewMapper;
    }

    @Operation(summary = "Gets a specific Author by Name")
    @GetMapping(value = "/name/{name}")
    public List<AuthorView> findByName(
            @PathVariable("name") @Parameter(description = "The Name of the Author to find") final String name) {
        List<Author> authors = authorService.findByName(name);
        return authors.stream().map(authorViewMapper::toAuthorView).toList();
    }

    @Operation(summary = "Gets a specific Author by id")
    @GetMapping(value = "/id/{id1}/{id2}")
    public ResponseEntity<AuthorView> findByAuthorID(
            @PathVariable("id1") @Parameter(description = "The id of the author to find") final String id1,
            @PathVariable("id2") final String id2) {
        String authorID = id1 + "/" + id2;
        final var author = authorService.findByAuthorID(authorID).orElseThrow(() -> new NotFoundException(Author.class, authorID));
        AuthorView authorView = authorViewMapper.toAuthorView(author);
        authorView.setImageUrl(authorService.getAuthorImageUrl(authorID));
        System.out.println("Author ID: " + authorID + " Image URL: " + authorView.getImageUrl());
        return ResponseEntity.ok(authorView);
    }



    //SprintB
    //SprintB
    //SprintB
    //SprintB

    @Operation(summary = "Get coauthors")
    @GetMapping("/{id1}/{id2}/coauthors")
    public ResponseEntity<List<CoAuthorDTO>> getCoAuthorsAndBooks(@PathVariable String id1, @PathVariable String id2) {
        String authorId = id1 + "/" + id2;

        List<CoAuthorDTO> coAuthors = authorService.getCoAuthorsAndBooks(authorId);
        return ResponseEntity.ok(coAuthors);
    }

    @Operation(summary = "Get author and their books by divided ID")
    @GetMapping("/{id1}/{id2}/books")
    public ResponseEntity<Optional<AuthorDTO>> getAuthorAndBooks(@PathVariable String id1, @PathVariable String id2) {
        // Concatena os IDs para formar o authorId completo
        String authorId = id1 + "/" + id2;

        // Chama o serviço para obter o autor e seus livros
        Optional<AuthorDTO> author = authorService.getAuthorAndBooks(authorId);

        // Retorna a resposta com o AuthorDTO no corpo
        return ResponseEntity.ok(author);
    }
/*
    @GetMapping("/top5Authors")
    public List<TopAuthorLendingDTO> getTop5Authors() {
        return authorService.findTop5AuthorsPerLending();
    }
*/
}

