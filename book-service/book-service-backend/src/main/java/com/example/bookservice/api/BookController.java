package com.example.bookservice.api;

import com.example.bookservice.model.Book;
import com.example.bookservice.model.BookCountDTO;
import com.example.bookservice.model.BookImage;
import com.example.bookservice.model.GenreBookCountDTO;
import com.example.bookservice.repositories.BookImageRepository;
import com.example.bookservice.repositories.BookRepository;
import com.example.bookservice.service.BookServiceImpl;
import com.example.bookservice.service.EditBookRequest;
import com.example.bookservice.api.BookViewMapper;
import com.example.bookservice.service.CreateBookRequest;
import com.example.bookservice.exceptions.NotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.IF_MATCH;


@Tag(name = "Books", description = "Endpoints for managing Books")
@RestController
@RequestMapping("/api/books")
class BookController {

    private static final Logger log = LoggerFactory.getLogger(BookController.class);
    private final BookServiceImpl bookService;

    private final BookRepository bookRepository;

    private final BookViewMapper bookMapper;

    private final BookImageRepository bookImageRepo;

    @Autowired
    public BookController(BookServiceImpl bookService, BookViewMapper bookMapper, BookImageRepository bookImageRepo, BookRepository bookRepository) {
        this.bookService = bookService;
        this.bookMapper = bookMapper;
        this.bookImageRepo = bookImageRepo;
        this.bookRepository = bookRepository;
    }

    @Operation(summary = "Get a specific book by genre")
    @GetMapping(value = "/genre/{genre}")
    public List<BookView> findByGenre(
            @PathVariable("genre") @Parameter(description = "The genre of the book to find") final String genre) {
        List<Book> books = bookService.getBookByGenre(genre);
        if (books.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Books not found!");
        }

        return bookMapper.toBookView(books);
    }

    @Operation(summary = "Get a specific book by ID")
    @GetMapping(value = "/id/{id}")
    public ResponseEntity<BookView> findBookByBookID(
            @PathVariable("id") @Parameter(description = "The ID of the book to find") final Long id) {
        log.debug("Fetching book by ID: " + id);
        final var book = bookService.getBookById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        return ResponseEntity.ok().eTag(Long.toString(book.getVersion())).body(bookMapper.toBookView(book));
    }



    @Operation(summary = "Get a specific book be title")
    @GetMapping(value = "/title/{title}")
    public List<BookView> findByTitle(
            @PathVariable("title") @Parameter(description = "The title of the book to find") final String title) {
        System.out.println("apiBookTitle");
        final var book = bookService.getBookByTitle(title);
        if (book.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found!");
        }

        return bookMapper.toBookView(book);
    }

    @Operation(summary = "Gets all book")
    @ApiResponse(description = "Success", responseCode = "200", content = {@Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = BookView.class)))})
    @GetMapping
    public Iterable<BookView> getAll() {
        return bookMapper.toBookView(bookService.getAll());
    }

    @Operation(summary = "Gets a specific Book by ISBN")
    @GetMapping(value = "/isbn/{isbn}")
    public ResponseEntity<BookView> findByIsbn(
            @PathVariable("isbn") @Parameter(description = "The isbn of the book to find") final String isbn) {
        System.out.println("apiBooks");
        final var book = bookService.getBookByIsbn(isbn).orElseThrow(() -> new NotFoundException("Cannot find a book"));

        return ResponseEntity.ok().eTag(Long.toString(book.getVersion())).body(bookMapper.toBookView(book));
    }

    @GetMapping("/top5Genres")
    public List<GenreBookCountDTO> findTop5Genres() {
        List<Map.Entry<String, Long>> topGenres = bookService.findTop5Genres();
        return bookMapper.mapTopGenresToGenreBookCountDTOs(topGenres);
    }


    @Operation(summary = "Get books by author ID")
    @GetMapping(value = "/author/{id1}/{id2}")
    public List<BookView> findByAuthorId(
            @PathVariable("id1") @Parameter(description = "The first part of the author ID") final String id1,
            @PathVariable("id2") @Parameter(description = "The second part of the author ID") final String id2) {
        String authorID = id1 + "/" + id2;
        log.debug("Fetching books for authorID: " + authorID);
        List<Book> books = bookService.getBooksByAuthorId(authorID);
        if (books.isEmpty()) {
            log.debug("No books found for authorID: " + authorID);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Books not found for this author!");
        }
        log.debug("Number of books found: " + books.size());
        return bookMapper.toBookView(books);
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


    @GetMapping("/images/{imageId}")
    public ResponseEntity<byte[]> getBookImage(@PathVariable Long imageId) {
        BookImage bookImage = bookImageRepo.findById(imageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(bookImage.getContentType()))
                .body(bookImage.getImage());
    }

    @Operation(summary = "Creates a new Book")
    @PostMapping
    public ResponseEntity<BookView> createBook(@Valid @RequestBody CreateBookRequest request) {
        Book createdBook = bookService.create(request);
        return ResponseEntity.ok(bookMapper.toBookView(createdBook));
    }




    @PatchMapping(value = "/{bookID}")
    public ResponseEntity<BookView> partialUpdate(final WebRequest request,
                                                  @PathVariable("bookID") @Parameter(description = "The id of the book to update") final Long bookID,
                                                  @Valid @RequestBody final EditBookRequest resource) {
        final String ifMatchValue = request.getHeader(IF_MATCH);
        if (ifMatchValue == null || ifMatchValue.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You must issue a conditional PATCH using 'if-match'");
        }

        final var book = bookService.partialUpdate(bookID, resource, getVersionFromIfMatchHeader(ifMatchValue));
        return ResponseEntity.ok().eTag(Long.toString(book.getVersion())).body(bookMapper.toBookView(book));
    }

    private Long getVersionFromIfMatchHeader(final String ifMatchHeader) {
        if (ifMatchHeader.startsWith("\"")) {
            return Long.parseLong(ifMatchHeader.substring(1, ifMatchHeader.length() - 1));
        }
        return Long.parseLong(ifMatchHeader);
    }

    @Operation(summary = "Get top 5 books by number of lendings")
    @GetMapping("/top5Books")
    public List<BookCountDTO> getTop5Books() {
        return bookService.findTop5Books();
    }
}


