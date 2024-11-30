package com.example.bookservice.model;

public class BookCountDTO {

    private Long bookID;
    private Long bookCount;

    public BookCountDTO(){}

    public BookCountDTO(Long bookID, Long bookCount) {
        this.bookID = bookID;
        this.bookCount = bookCount;
    }

    public Long getBookID() {
        return bookID;
    }

    public void setBookID(String genre) {
        this.bookID = bookID;
    }

    public Long getBookCount() {
        return bookCount;
    }

    public void setBookCount(Long bookCount) {
        this.bookCount = bookCount;
    }

}
