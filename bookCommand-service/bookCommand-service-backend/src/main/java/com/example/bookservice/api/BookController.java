package com.example.bookservice.api;

import com.example.bookservice.messaging.RabbitMQProducer;
import com.example.bookservice.model.*;
import com.example.bookservice.repositories.BookRepository;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
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
    private final BookRepository bookRepository;
    private final RabbitMQProducer rabbitMQProducer;


    @Autowired
    public BookController(BookServiceImpl bookService, BookViewMapper bookMapper, BookRepository bookRepository, RabbitMQProducer rabbitMQProducer) {
        this.bookService = bookService;
        this.bookMapper = bookMapper;
        this.bookRepository = bookRepository;
        this.rabbitMQProducer = rabbitMQProducer;
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

    @PutMapping(value = "/{bookID}/image", consumes = "multipart/form-data")
    public ResponseEntity<Void> addImageToBook(
            @PathVariable("bookID") @Parameter(description = "The id of the book to update") final Long bookID,
            @RequestParam("image") MultipartFile imageFile) {

        byte[] imageBytes;
        try {
            imageBytes = imageFile.getBytes();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to read image file");
        }

        bookService.addImageToBook(bookID, imageBytes, imageFile.getContentType());
        return ResponseEntity.ok().build();
    }


    @PostMapping("/sync")
    public ResponseEntity<Void> syncBook(@RequestBody Book book) {
        rabbitMQProducer.sendBookSyncEvent(book);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
