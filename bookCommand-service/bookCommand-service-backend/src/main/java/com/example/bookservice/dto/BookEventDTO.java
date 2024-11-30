package com.example.bookservice.dto;

import com.example.bookservice.model.Author;
import com.example.bookservice.model.Book;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookEventDTO {

    private String eventType;   // Tipo de evento: "create", "update", "delete"
    private Long bookID;        // ID do livro
    private long version;       // Versionamento para controle otimista
    private String isbn;        // ISBN do livro
    private String title;       // Título do livro
    private String genre;       // Gênero (nome textual)
    private String description; // Descrição do livro
    private List<String> authorIds; // IDs dos autores
    private Long bookImageId;   // ID da imagem do livro

    // Método estático para criar um BookEventDTO a partir de um Book
    public static BookEventDTO fromBook(String eventType, Book book) {
        return new BookEventDTO(
                eventType,
                book.getBookID(),
                book.getVersion(),
                book.getIsbn(),
                book.getTitle(),
                book.getGenre() != null ? book.getGenre().getInterest() : null,
                book.getDescription(),
                book.getAuthor() != null ? book.getAuthor().stream().map(Author::getAuthorID).collect(Collectors.toList()) : null,
                book.getBookImage() != null ? book.getBookImage().getBookImageID() : null
        );
    }
}
