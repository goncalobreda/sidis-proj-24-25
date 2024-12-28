package com.example.bookservice.api;

import com.example.bookservice.messaging.RabbitMQProducer;
import com.example.bookservice.model.Book;
import com.example.bookservice.service.BookServiceImpl;
import com.example.bookservice.service.CreateBookRequest;
import com.example.bookservice.service.EditBookRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.IF_MATCH;

@Tag(name = "Books", description = "Endpoints for managing Books")
@RestController
@RequestMapping("/api/books")
public class BookController {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);
    private final BookServiceImpl bookService;
    private final RabbitMQProducer rabbitMQProducer;

    public BookController(BookServiceImpl bookService, RabbitMQProducer rabbitMQProducer) {
        this.bookService = bookService;
        this.rabbitMQProducer = rabbitMQProducer;
    }

    @Operation(summary = "Creates a new Book")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Book> createBook(@Valid @RequestBody CreateBookRequest request) {
        logger.info("Creating a new book.");
        Book createdBook = bookService.create(request);
        logger.info("Book created successfully: {}", createdBook.getIsbn());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
    }

    @Operation(summary = "Updates a specific book")
    @PatchMapping(value = "/{bookID}")
    public ResponseEntity<Book> partialUpdate(
            final WebRequest request,
            @PathVariable("bookID") @Parameter(description = "The id of the book to update") final Long bookID,
            @Valid @RequestBody final EditBookRequest resource) {
        final String ifMatchValue = request.getHeader(IF_MATCH);
        if (ifMatchValue == null || ifMatchValue.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You must issue a conditional PATCH using 'if-match'");
        }
        Book updatedBook = bookService.partialUpdate(bookID, resource, Long.parseLong(ifMatchValue));
        logger.info("Book updated successfully: {}", updatedBook.getIsbn());
        return ResponseEntity.ok(updatedBook);
    }

    @PutMapping(value = "/{bookID}/image", consumes = "multipart/form-data")
    public ResponseEntity<Void> addImageToBook(
            @PathVariable("bookID") @Parameter(description = "The id of the book to update") final Long bookID,
            @RequestParam("image") MultipartFile imageFile) {

        try {
            byte[] imageBytes = imageFile.getBytes();
            bookService.addImageToBook(bookID, imageBytes, imageFile.getContentType());
            logger.info("Image added to book with ID: {}", bookID);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            logger.error("Error reading image file: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to read image file");
        }
    }

    @PostMapping("/sync")
    public ResponseEntity<Void> syncBook(@RequestBody Book book) {
        rabbitMQProducer.sendBookSyncEvent(book);
        logger.info("Book sync event sent: {}", book.getIsbn());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
