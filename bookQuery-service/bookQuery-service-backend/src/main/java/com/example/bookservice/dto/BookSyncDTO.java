package com.example.bookservice.dto;

import com.example.bookservice.model.Book;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookSyncDTO {

    private Long bookID; // Identificador único do livro
    private String isbn;
    private String title;
    private String description;
    private String genre;
    private List<AuthorDTO> authors;
    private byte[] bookImage; // Imagem do livro
    private String originInstanceId;

    public static BookSyncDTO fromBook(String action, Book book, String originInstanceId) {
        BookSyncDTO bookSyncDTO = new BookSyncDTO();
        bookSyncDTO.setBookID(book.getBookID());
        bookSyncDTO.setIsbn(book.getIsbn());
        bookSyncDTO.setTitle(book.getTitle());
        bookSyncDTO.setDescription(book.getDescription());
        bookSyncDTO.setGenre(book.getGenre().getInterest());
        bookSyncDTO.setAuthors(book.getAuthor().stream()
                .map(AuthorDTO::fromAuthor)
                .collect(Collectors.toList()));
        if (book.getBookImage() != null) {
            bookSyncDTO.setBookImage(book.getBookImage().getImage());
        }
        bookSyncDTO.setOriginInstanceId(originInstanceId);
        return bookSyncDTO;
    }
}
