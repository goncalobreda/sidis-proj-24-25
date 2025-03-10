package com.example.bookservice.service;

import com.example.bookservice.dto.BookSyncDTO;
import com.example.bookservice.dto.PartialUpdateDTO;
import com.example.bookservice.model.Book;
import com.example.bookservice.model.BookCountDTO;
import com.example.bookservice.model.Genre;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public interface BookService {

    Book createOrUpdateFromBookSyncDTO(BookSyncDTO bookSyncDTO);

    void applyPartialUpdate(PartialUpdateDTO partailUpdateDTO);

    Optional<Book> getBookByIsbn(final String isbn);

    List<Book> getAll();

    List<Book> getBookByGenre(final String genre);

    Genre getGenreByInterest(String interest);

    List<Book> getBookByTitle(final String title);

   List<Map.Entry<String, Long>> findTop5Genres();

    List<BookCountDTO> findTop5Books();

    Optional<Book> getBookById(final Long bookID);

    List<Book> getBooksByAuthorId(String authorID);

    boolean isBookIDUnique(Long bookID);

}
