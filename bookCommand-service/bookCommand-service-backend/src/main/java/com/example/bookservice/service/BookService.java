package com.example.bookservice.service;

import com.example.bookservice.model.Book;
import com.example.bookservice.model.BookCountDTO;
import com.example.bookservice.model.Genre;
import com.example.bookservice.model.GenreBookCountDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public interface BookService {


    Book create(CreateBookRequest request);

    Optional<Book> getBookByIsbn(final String isbn);

    List<Book> getAll();

    List<Book> getBookByGenre(final String genre);

    Genre getGenreByInterest(String interest);

    List<Book> getBookByTitle(final String title);

    List<Map.Entry<String, Long>> findTop5Genres();

    void saveBookWithImage(Book book, byte[] image, String contentType);

    void addImageToBook(Long bookID, byte[] image, String contentType);

 //   List<BookCountDTO> findTop5Books();

    Optional<Book> getBookById(final Long bookID);

    List<Book> getBooksByAuthorId(String authorID);

    boolean isBookIDUnique(Long bookID);

    Book partialUpdate(Long bookID, EditBookRequest request, long desiredVersion);

}
