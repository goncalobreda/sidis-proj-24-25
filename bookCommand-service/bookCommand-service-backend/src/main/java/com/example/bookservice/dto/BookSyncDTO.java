package com.example.bookservice.dto;

import java.io.Serializable;

public class BookSyncDTO implements Serializable {

    private String isbn;
    private String title;
    private String genre;
    private String description;

    // Construtor padrão (necessário para deserialização)
    public BookSyncDTO() {}

    // Construtor com argumentos
    public BookSyncDTO(String isbn, String title, String genre, String description) {
        this.isbn = isbn;
        this.title = title;
        this.genre = genre;
        this.description = description;
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

    @Override
    public String toString() {
        return "BookSyncDTO{" +
                "isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", genre='" + genre + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
