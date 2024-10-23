package com.example.bookservice.model;

public class GenreBookCountDTO {

    private String bookID;
    private Long bookCount;

    public GenreBookCountDTO(){}

    public GenreBookCountDTO(String genre, Long bookCount) {
        this.bookID = genre;
        this.bookCount = bookCount;
    }

    public String getBookID() {
        return bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

    public Long getBookCount() {
        return bookCount;
    }

    public void setBookCount(Long bookCount) {
        this.bookCount = bookCount;
    }

}
