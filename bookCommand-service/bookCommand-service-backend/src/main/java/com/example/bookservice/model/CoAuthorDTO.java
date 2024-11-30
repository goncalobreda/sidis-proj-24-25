package com.example.bookservice.model;

import com.example.bookservice.model.Book;
import com.example.bookservice.model.BookDTO;

import java.util.List;
import java.util.stream.Collectors;

public class CoAuthorDTO {

    private String coAuthorId;
    private String coAuthorName;
    private List<BookDTO> books;

    public CoAuthorDTO(Author author, List<Book> books) {
        this.coAuthorId = author.getAuthorID();
        this.coAuthorName = author.getName();
        this.books = books.stream().map(BookDTO::new).collect(Collectors.toList());
    }

    // Getters and setters

    public String getCoAuthorId() {
        return coAuthorId;
    }

    public void setCoAuthorId(String coAuthorId) {
        this.coAuthorId = coAuthorId;
    }

    public String getCoAuthorName() {
        return coAuthorName;
    }

    public void setCoAuthorName(String coAuthorName) {
        this.coAuthorName = coAuthorName;
    }

    public List<BookDTO> getBooks() {
        return books;
    }

    public void setBooks(List<BookDTO> books) {
        this.books = books;
    }
}
