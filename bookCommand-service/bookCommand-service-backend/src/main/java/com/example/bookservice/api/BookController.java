package com.example.bookservice.api;

import com.example.bookservice.model.*;
import com.example.bookservice.service.BookServiceImpl;
import com.example.bookservice.service.EditBookRequest;
import com.example.bookservice.api.BookViewMapper;
import com.example.bookservice.service.CreateBookRequest;
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

import static org.springframework.http.HttpHeaders.IF_MATCH;

@Tag(name = "Books", description = "Endpoints for managing Books")
@RestController
@RequestMapping("/api/books")
public class BookController {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);
    private final BookServiceImpl bookService;
    private final BookViewMapper bookMapper;

    @Autowired
    public BookController(BookServiceImpl bookService, BookViewMapper bookMapper) {
        this.bookService = bookService;
        this.bookMapper = bookMapper;
    }

    @Operation(summary = "Get a specific book by genre")
    @GetMapping(value = "/genre/{genre}")
    public List<BookView> findByGenre(
            @PathVariable("genre") @Parameter(description = "The genre of the book to find") final String genre) {
        return bookMapper.toBookView(bookService.getBookByGenre(genre));
    }

    @Operation(summary = "Get a specific book by ID")
    @GetMapping(value = "/id/{id}")
    public ResponseEntity<BookView> findBookByBookID(
            @PathVariable("id") @Parameter(description = "The ID of the book to find") final Long id) {
        return bookService.getBookById(id)
                .map(book -> ResponseEntity.ok(bookMapper.toBookView(book)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
    }

    @Operation(summary = "Get a specific book by title")
    @GetMapping(value = "/title/{title}")
    public List<BookView> findByTitle(
            @PathVariable("title") @Parameter(description = "The title of the book to find") final String title) {
        return bookMapper.toBookView(bookService.getBookByTitle(title));
    }

    @Operation(summary = "Get all books")
    @GetMapping
    public Iterable<BookView> getAll() {
        return bookMapper.toBookView(bookService.getAll());
    }

    @Operation(summary = "Creates a new Book")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BookView> createBook(@Valid @RequestBody CreateBookRequest request) {
        Book createdBook = bookService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookMapper.toBookView(createdBook));
    }

    @Operation(summary = "Updates a specific book")
    @PatchMapping(value = "/{bookID}")
    public ResponseEntity<BookView> partialUpdate(
            final WebRequest request,
            @PathVariable("bookID") @Parameter(description = "The id of the book to update") final Long bookID,
            @Valid @RequestBody final EditBookRequest resource) {
        final String ifMatchValue = request.getHeader(IF_MATCH);
        if (ifMatchValue == null || ifMatchValue.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You must issue a conditional PATCH using 'if-match'");
        }
        Book updatedBook = bookService.partialUpdate(bookID, resource, Long.parseLong(ifMatchValue));
        return ResponseEntity.ok().eTag(Long.toString(updatedBook.getVersion())).body(bookMapper.toBookView(updatedBook));
    }

}
