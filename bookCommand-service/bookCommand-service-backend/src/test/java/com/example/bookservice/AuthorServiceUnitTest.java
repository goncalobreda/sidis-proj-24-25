/*
package com.example.bookservice;


import com.example.bookservice.model.Author;
import com.example.bookservice.model.AuthorImage;
import com.example.bookservice.model.CoAuthorDTO;
import com.example.bookservice.repositories.AuthorRepository;
import com.example.bookservice.service.AuthorServiceImpl;
import com.example.bookservice.service.BookService;
import com.example.bookservice.service.CreateAuthorRequest;
import com.example.bookservice.service.EditAuthorRequest;
import com.example.bookservice.model.Book;
import com.example.bookservice.repositories.BookRepository;
import com.example.bookservice.exceptions.NotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthorServiceUnitTest {

    private AuthorRepository authorRepository;
    private AuthorServiceImpl authorService;
    private BookRepository bookRepository;

    private BookService bookService;

    @BeforeEach
    void setUp() {
        authorRepository = Mockito.mock(AuthorRepository.class);
        bookRepository = Mockito.mock(BookRepository.class);
        bookService = Mockito.mock(BookService.class);
        authorService = new AuthorServiceImpl(authorRepository, bookRepository, bookService);

        // Set up mock HTTP request
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    void findByName_shouldReturnAuthors_whenAuthorsExist() {
        List<Author> authors = Arrays.asList(new Author("Author1", "Bio1"), new Author("Author2", "Bio2"));
        when(authorRepository.findByName("Author")).thenReturn(authors);

        List<Author> result = authorService.findByName("Author");

        assertNotNull(result);
        assertEquals(authors, result);
        verify(authorRepository, times(1)).findByName("Author");
    }

    @Test
    void findByAuthorID_shouldReturnAuthor_whenAuthorExists() {
        Author author = new Author("Author", "Bio");
        when(authorRepository.findByAuthorID("2024/1")).thenReturn(Optional.of(author));

        Optional<Author> result = authorService.findByAuthorID("2024/1");

        assertTrue(result.isPresent());
        assertEquals(author, result.get());
        verify(authorRepository, times(1)).findByAuthorID("2024/1");
    }

    @Test
    void findByAuthorID_shouldReturnEmpty_whenAuthorDoesNotExist() {
        when(authorRepository.findByAuthorID("2024/1")).thenReturn(Optional.empty());

        Optional<Author> result = authorService.findByAuthorID("2024/1");

        assertFalse(result.isPresent());
        verify(authorRepository, times(1)).findByAuthorID("2024/1");
    }

    @Test
    void create_shouldCreateNewAuthor() {
        CreateAuthorRequest createRequest = new CreateAuthorRequest("Author", "Bio");
        Author author = new Author(createRequest.getName(), createRequest.getBiography());

        when(authorRepository.save(any(Author.class))).thenReturn(author);

        Author createdAuthor = authorService.create(createRequest);

        assertNotNull(createdAuthor);
        assertEquals(createRequest.getName(), createdAuthor.getName());
        assertEquals(createRequest.getBiography(), createdAuthor.getBiography());
        verify(authorRepository, times(1)).save(any(Author.class));
    }

    @Test
    void create_shouldThrowException_whenBiographyExceedsMaxLength() {
        String longBio = "A".repeat(4097);
        CreateAuthorRequest createRequest = new CreateAuthorRequest("Author", longBio);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> authorService.create(createRequest));
        assertEquals("The biography cannot be null, nor have more than 4096 characters.", exception.getMessage());
        verify(authorRepository, never()).save(any(Author.class));
    }

    @Test
    void create_shouldThrowException_whenNameExceedsMaxLength() {
        String name = "A".repeat(151);
        CreateAuthorRequest createRequest = new CreateAuthorRequest(name, "Aurelio");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> authorService.create(createRequest));
        assertEquals("The name cannot be null, nor have more than 150 characters.", exception.getMessage());
        verify(authorRepository, never()).save(any(Author.class));
    }

    @Test
    void partialUpdate_shouldReturnUpdatedAuthor() {
        EditAuthorRequest editRequest = new EditAuthorRequest("Updated Author", "Updated Bio");
        Author existingAuthor = new Author("Author", "Bio");
        existingAuthor.setAuthorID("2024/1");
        existingAuthor.setVersion(1L); // Set the version to 1

        when(authorRepository.findByAuthorID("2024/1")).thenReturn(Optional.of(existingAuthor));
        when(authorRepository.save(any(Author.class))).thenReturn(existingAuthor);

        Author updatedAuthor = authorService.partialUpdate("2024/1", editRequest, 1L); // Pass version 1

        assertNotNull(updatedAuthor);
        assertEquals(editRequest.getName(), updatedAuthor.getName());
        assertEquals(editRequest.getBiography(), updatedAuthor.getBiography());
        verify(authorRepository, times(1)).findByAuthorID("2024/1");
        verify(authorRepository, times(1)).save(any(Author.class));
    }

    @Test
    void partialUpdate_shouldThrowException_whenAuthorNotFound() {
        EditAuthorRequest editRequest = new EditAuthorRequest("Updated Author", "Updated Bio");

        when(authorRepository.findByAuthorID("2024/1")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> authorService.partialUpdate("2024/1", editRequest, 1L));
        verify(authorRepository, times(1)).findByAuthorID("2024/1");
        verify(authorRepository, never()).save(any(Author.class));
    }


    @Test
    void getCoAuthorsAndBooks_shouldReturnCoAuthorsAndBooks() {
        Author author = new Author("Author", "Bio");
        author.setAuthorID("2024/1");
        Book book1 = new Book();
        Book book2 = new Book();
        book1.setAuthor(Collections.singletonList(author));
        book2.setAuthor(Collections.singletonList(author));

        when(authorRepository.findByAuthorID("2024/1")).thenReturn(Optional.of(author));
        when(authorRepository.findByAuthorsContaining(author)).thenReturn(Arrays.asList(book1, book2));

        List<CoAuthorDTO> coAuthors = authorService.getCoAuthorsAndBooks("2024/1");

        assertNotNull(coAuthors);
        assertTrue(coAuthors.isEmpty()); // No co-authors for single author books
        verify(authorRepository, times(1)).findByAuthorID("2024/1");
        verify(authorRepository, times(1)).findByAuthorsContaining(author);
    }

    @Test
    void getCoAuthorsAndBooks_shouldThrowException_whenAuthorNotFound() {
        when(authorRepository.findByAuthorID("2024/1")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> authorService.getCoAuthorsAndBooks("2024/1"));
        verify(authorRepository, times(1)).findByAuthorID("2024/1");
        verify(authorRepository, never()).findByAuthorsContaining(any(Author.class));
    }

    @Test
    void getAuthorImageUrl_shouldReturnImageUrl_whenImageExists() {
        Author author = new Author("Author", "Bio");
        AuthorImage image = new AuthorImage(author, new byte[0], "image/jpeg");
        image.setAuthorImageID(1L);
        author.setAuthorID("2024/1");
        author.setImage(image);

        when(authorRepository.findByAuthorID("2024/1")).thenReturn(Optional.of(author));

        String expectedUrl = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/authors/")
                .path("2024_1")
                .path("/photo/")
                .path("1")
                .toUriString();

        String imageUrl = authorService.getAuthorImageUrl("2024/1");

        assertEquals(expectedUrl, imageUrl);
        verify(authorRepository, times(1)).findByAuthorID("2024/1");
    }

    @Test
    void getAuthorImageUrl_shouldReturnNull_whenImageDoesNotExist() {
        Author author = new Author("Author", "Bio");
        author.setAuthorID("2024/1");

        when(authorRepository.findByAuthorID("2024/1")).thenReturn(Optional.of(author));

        String imageUrl = authorService.getAuthorImageUrl("2024/1");

        assertNull(imageUrl);
        verify(authorRepository, times(1)).findByAuthorID("2024/1");
    }

    @Test
    void saveAuthor_shouldSaveAuthor() {
        Author author = new Author("Author", "Bio");

        authorService.saveAuthor(author);

        verify(authorRepository, times(1)).save(author);
    }
}
*/