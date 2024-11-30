package com.example.bookservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"authors", "bookImage", "genre"}) // Ignore todas as relações profundas
public class BookDTO {

    private Long bookId;
    private String title;

    public BookDTO(Book book) {
        this.bookId = book.getBookID();
        this.title = book.getTitle();
    }

    // Getters and setters

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
