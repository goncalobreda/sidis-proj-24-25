package com.example.bookservice.model;

import java.util.List;

public class AuthorDTO {
    private String name;
    private String biography;
    private List<BookDTO> books;

    public AuthorDTO(String authorID, String name, List<BookDTO> books) {
        this.name = authorID;
        this.biography = name;
        this.books = books;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public List<BookDTO> getBooks() {
        return books;
    }

    public void setBooks(List<BookDTO> books) {
        this.books = books;
    }

}

