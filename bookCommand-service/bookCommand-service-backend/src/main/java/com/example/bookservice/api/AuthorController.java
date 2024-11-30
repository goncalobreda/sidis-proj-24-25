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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Tag(name = "Authors", description = "Endpoints for managing Authors")
@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    private static final Logger logger = LoggerFactory.getLogger(AuthorController.class);

    private final AuthorServiceImpl authorService;

    @Autowired
    public AuthorController(AuthorServiceImpl authorService) {
        this.authorService = authorService;
    }

    @Operation(summary = "Gets a specific Author by Name")
    @GetMapping(value = "/name/{name}")
    public List<Author> findByName(
            @PathVariable("name") @Parameter(description = "The Name of the Author to find") final String name) {
        return authorService.findByName(name);
    }

    @Operation(summary = "Gets a specific Author by id")
    @GetMapping(value = "/id/{id}")
    public ResponseEntity<Author> findByAuthorID(
            @PathVariable("id") @Parameter(description = "The id of the author to find") final String authorID) {
        return authorService.findByAuthorID(authorID)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found"));
    }

    @Operation(summary = "Creates a new Author")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Author> create(@Valid @RequestBody CreateAuthorRequest request) {
        Author createdAuthor = authorService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAuthor);
    }

    @Operation(summary = "Updates an existing Author")
    @PatchMapping(value = "/{id}")
    public ResponseEntity<Author> partialUpdate(
            @PathVariable("id") @Parameter(description = "The id of the author to update") final String authorID,
            @Valid @RequestBody final EditAuthorRequest request) {
        Author updatedAuthor = authorService.partialUpdate(authorID, request, 1L); // Replace with actual version handling logic
        return ResponseEntity.ok(updatedAuthor);
    }
}
