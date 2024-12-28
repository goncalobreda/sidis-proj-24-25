package com.example.bookservice.dto;

import com.example.bookservice.model.Author;
import com.example.bookservice.model.Book;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookEventDTO {

    private String eventType;       // Tipo de evento: "create", "update", "delete"
    private Long bookID;            // ID único do livro
    private long version;           // Versão para controle otimista
    private String isbn;            // ISBN do livro
    private String title;           // Título do livro
    private String genre;           // Gênero do livro
    private String description;     // Descrição do livro
    private List<String> authorIds; // IDs dos autores relacionados ao livro
    private Long bookImageId;       // ID da imagem associada ao livro

    /**
     * Cria um BookEventDTO a partir de uma entidade Book.
     *
     * @param eventType Tipo do evento (create, update, delete)
     * @param book      Entidade Book
     * @return Instância preenchida de BookEventDTO
     */
    public static BookEventDTO fromBook(String eventType, Book book) {
        return new BookEventDTO(
                eventType,
                book.getBookID(),
                book.getVersion(),
                book.getIsbn(),
                book.getTitle(),
                Optional.ofNullable(book.getGenre()).map(g -> g.getInterest()).orElse(null),
                book.getDescription(),
                Optional.ofNullable(book.getAuthor())
                        .map(authors -> authors.stream()
                                .map(Author::getAuthorID)
                                .collect(Collectors.toList()))
                        .orElse(null),
                Optional.ofNullable(book.getBookImage()).map(image -> image.getBookImageID()).orElse(null)
        );
    }
}
