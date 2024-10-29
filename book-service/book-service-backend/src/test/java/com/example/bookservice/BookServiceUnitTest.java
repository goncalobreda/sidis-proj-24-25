package com.example.bookservice;

import com.example.bookservice.client.LendingServiceClient;
import com.example.bookservice.model.Author;
import com.example.bookservice.repositories.AuthorRepository;
import com.example.bookservice.model.Book;
import com.example.bookservice.model.BookImage;
import com.example.bookservice.model.Genre;
import com.example.bookservice.repositories.BookImageRepository;
import com.example.bookservice.repositories.BookRepository;
import com.example.bookservice.repositories.GenreRepository;
import com.example.bookservice.service.BookServiceImpl;
import com.example.bookservice.service.CreateBookRequest;
import com.example.bookservice.service.EditBookRequest;
import com.example.bookservice.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceUnitTest {

    @Value("${server.port}")
    private String currentPort;

    @Value("${book.instance1.url}")
    private String bookInstance1Url;

    @Value("${book.instance2.url}")
    private String bookInstance2Url;

    private BookRepository bookRepository;
    private GenreRepository genreRepository;
    private AuthorRepository authorRepository;
    private BookImageRepository bookImageRepository;
    private BookServiceImpl bookService;
    private LendingServiceClient lendingServiceClient;
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        bookRepository = Mockito.mock(BookRepository.class);
        genreRepository = Mockito.mock(GenreRepository.class);
        authorRepository = Mockito.mock(AuthorRepository.class);
        bookImageRepository = Mockito.mock(BookImageRepository.class);
        lendingServiceClient = Mockito.mock(LendingServiceClient.class);
        bookService = new BookServiceImpl(bookRepository, bookImageRepository, genreRepository, authorRepository,  lendingServiceClient);

        ReflectionTestUtils.setField(bookService, "currentPort", "${server.port}");
    }

    @Test
    void createBook_shouldCreateNewBook() {
        CreateBookRequest createRequest = new CreateBookRequest();
        createRequest.setIsbn("9781234567897");
        createRequest.setTitle("Test Book");
        createRequest.setGenre("Fiction");
        createRequest.setDescription("Test Description");
        createRequest.setAuthorIds(Arrays.asList("2024/1", "2024/2"));
        createRequest.setBookImageId(1L);

        Genre genre = new Genre();
        genre.setInterest("Fiction");

        Author author1 = new Author();
        author1.setAuthorID("2024/1");
        Author author2 = new Author();
        author2.setAuthorID("2024/2");

        BookImage bookImage = new BookImage();
        bookImage.setBookImageID(1L);

        Book savedBook = new Book();
        savedBook.setIsbn(createRequest.getIsbn());
        savedBook.setTitle(createRequest.getTitle());
        savedBook.setGenre(genre);
        savedBook.setDescription(createRequest.getDescription());
        savedBook.setAuthor(Arrays.asList(author1, author2));
        savedBook.setBookImage(bookImage);

        when(genreRepository.findByInterest("Fiction")).thenReturn(genre);
        when(authorRepository.findByAuthorID("2024/1")).thenReturn(Optional.of(author1));
        when(authorRepository.findByAuthorID("2024/2")).thenReturn(Optional.of(author2));
        when(bookImageRepository.findById(1L)).thenReturn(Optional.of(bookImage));
        when(bookRepository.save(any(Book.class))).thenReturn(savedBook); // Configure save to return savedBook

        Book createdBook = bookService.create(createRequest);

        assertNotNull(createdBook);
        assertEquals(createRequest.getIsbn(), createdBook.getIsbn());
        assertEquals(createRequest.getTitle(), createdBook.getTitle());
        assertEquals(createRequest.getGenre(), createdBook.getGenre().getInterest());
        assertEquals(createRequest.getDescription(), createdBook.getDescription());
        assertEquals(2, createdBook.getAuthor().size());
        assertEquals(bookImage, createdBook.getBookImage());
        verify(bookRepository, times(1)).save(any(Book.class));
    }


    @Test
    void createBook_shouldThrowException_whenGenreNotFound() {
        CreateBookRequest createRequest = new CreateBookRequest();
        createRequest.setIsbn("9781234567897");
        createRequest.setTitle("Test Book");
        createRequest.setGenre("Non-Existent Genre");
        createRequest.setDescription("Test Description");
        createRequest.setAuthorIds(Arrays.asList("2024/1", "2024/2"));
        createRequest.setBookImageId(1L);

        when(genreRepository.findByInterest("Non-Existent Genre")).thenReturn(null);

        assertThrows(NotFoundException.class, () -> bookService.create(createRequest));
        verify(genreRepository, times(1)).findByInterest("Non-Existent Genre");
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void createBook_shouldThrowException_whenAuthorNotFound() {
        CreateBookRequest createRequest = new CreateBookRequest();
        createRequest.setIsbn("9781234567897");
        createRequest.setTitle("Test Book");
        createRequest.setGenre("Fiction");
        createRequest.setDescription("Test Description");
        createRequest.setAuthorIds(Arrays.asList("2024/1", "2024/3"));
        createRequest.setBookImageId(1L);

        Genre genre = new Genre();
        genre.setInterest("Fiction");

        Author author1 = new Author();
        author1.setAuthorID("2024/1");

        when(genreRepository.findByInterest("Fiction")).thenReturn(genre);
        when(authorRepository.findByAuthorID("2024/1")).thenReturn(Optional.of(author1));
        when(authorRepository.findByAuthorID("2024/3")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookService.create(createRequest));
        verify(authorRepository, times(1)).findByAuthorID("2024/3");
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void createBook_shouldThrowException_whenBookImageNotFound() {
        CreateBookRequest createRequest = new CreateBookRequest();
        createRequest.setIsbn("9781234567897");
        createRequest.setTitle("Test Book");
        createRequest.setGenre("Fiction");
        createRequest.setDescription("Test Description");
        createRequest.setAuthorIds(Arrays.asList("2024/1", "2024/2"));
        createRequest.setBookImageId(1L);

        Genre genre = new Genre();
        genre.setInterest("Fiction");

        Author author1 = new Author();
        author1.setAuthorID("2024/1");
        Author author2 = new Author();
        author2.setAuthorID("2024/2");

        when(genreRepository.findByInterest("Fiction")).thenReturn(genre);
        when(authorRepository.findByAuthorID("2024/1")).thenReturn(Optional.of(author1));
        when(authorRepository.findByAuthorID("2024/2")).thenReturn(Optional.of(author2));
        when(bookImageRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookService.create(createRequest));
        verify(bookImageRepository, times(1)).findById(1L);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void createBook_shouldThrowException_whenISBNIsInvalid() {
        CreateBookRequest createRequest = new CreateBookRequest();
        createRequest.setIsbn("Invalid ISBN");
        createRequest.setTitle("Test Book");
        createRequest.setGenre("Fiction");
        createRequest.setDescription("Test Description");
        createRequest.setAuthorIds(Arrays.asList("2024/1", "2024/2"));
        createRequest.setBookImageId(1L);

        assertThrows(IllegalArgumentException.class, () -> bookService.create(createRequest));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void updateBook_shouldUpdateBookDetails() {
        Book existingBook = new Book();
        existingBook.setBookID(1L);
        existingBook.setIsbn("9781234567897");
        existingBook.setTitle("Old Title");
        existingBook.setDescription("Old Description");
        existingBook.setGenre(new Genre("Old Genre"));
        existingBook.setVersion(1L);

        Genre newGenre = new Genre("New Genre");
        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(genreRepository.findByInterest("New Genre")).thenReturn(newGenre);
        when(bookRepository.save(any(Book.class))).thenReturn(existingBook);

        EditBookRequest editRequest = new EditBookRequest();
        editRequest.setTitle("New Title");
        editRequest.setDescription("New Description");
        editRequest.setGenre("New Genre");

        Book updatedBook = bookService.partialUpdate(1L, editRequest, 1L);

        assertNotNull(updatedBook);
        assertEquals(editRequest.getTitle(), updatedBook.getTitle());
        assertEquals(editRequest.getDescription(), updatedBook.getDescription());
        assertEquals(newGenre, updatedBook.getGenre());
        verify(bookRepository, times(1)).findById(1L);
        verify(genreRepository, times(1)).findByInterest("New Genre");
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void updateBook_shouldThrowException_whenBookNotFound() {
        EditBookRequest editRequest = new EditBookRequest();
        editRequest.setTitle("New Title");
        editRequest.setDescription("New Description");
        editRequest.setGenre("New Genre");

        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookService.partialUpdate(1L, editRequest, 1L));
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void updateBook_shouldThrowException_whenGenreNotFound() {
        Book existingBook = new Book();
        existingBook.setBookID(1L);
        existingBook.setIsbn("9781234567897");
        existingBook.setTitle("Old Title");
        existingBook.setDescription("Old Description");
        existingBook.setGenre(new Genre("Old Genre"));
        existingBook.setVersion(1L);

        EditBookRequest editRequest = new EditBookRequest();
        editRequest.setTitle("New Title");
        editRequest.setDescription("New Description");
        editRequest.setGenre("Non-Existent Genre");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(genreRepository.findByInterest("Non-Existent Genre")).thenReturn(null);

        assertThrows(NotFoundException.class, () -> bookService.partialUpdate(1L, editRequest, 1L));
        verify(genreRepository, times(1)).findByInterest("Non-Existent Genre");
        verify(bookRepository, never()).save(any(Book.class));
    }

}
