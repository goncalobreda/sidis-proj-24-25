package com.example.bookservice.service;

import com.example.bookservice.dto.BookSyncDTO;
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

    void syncBook(BookSyncDTO bookSyncDTO);

    Book create(CreateBookRequest request);

    void saveBookWithImage(Book book, byte[] image, String contentType);

    void addImageToBook(Long bookID, byte[] image, String contentType);

    boolean isBookIDUnique(Long bookID);

    Book partialUpdate(Long bookID, EditBookRequest request, long desiredVersion);

}
