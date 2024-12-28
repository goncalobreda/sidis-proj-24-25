package com.example.acquisitionserviceCommand.dto;

import java.io.Serializable;
import java.util.List;

public class BookSyncDTO implements Serializable {

    private String isbn;
    private String title;
    private String genre;
    private String description;
    private List<AuthorDTO> authors; // Novo campo para autores

    public BookSyncDTO() {}

    public BookSyncDTO(String isbn, String title, String genre, String description, List<AuthorDTO> authors) {
        this.isbn = isbn;
        this.title = title;
        this.genre = genre;
        this.description = description;
        this.authors = authors;
    }

    // Getters e Setters
    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<AuthorDTO> getAuthors() {
        return authors;
    }

    public void setAuthors(List<AuthorDTO> authors) {
        this.authors = authors;
    }

    @Override
    public String toString() {
        return "BookSyncDTO{" +
                "isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", genre='" + genre + '\'' +
                ", description='" + description + '\'' +
                ", authors=" + authors +
                '}';
    }
}
