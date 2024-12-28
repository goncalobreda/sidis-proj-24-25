package com.example.bookservice.model;

public class BookCountDTO {

    private Long bookID;      // The ID of the book
    private Long bookCount;   // The count of books (optional, could be removed if unused)
    private Long lendingCount; // The count of lendings for the book

    // Default constructor
    public BookCountDTO() {}

    // Constructor to initialize all fields
    public BookCountDTO(Long bookID, Long bookCount, Long lendingCount) {
        this.bookID = bookID;
        this.bookCount = bookCount;
        this.lendingCount = lendingCount;
    }

    // Constructor for when you don't need bookCount
    public BookCountDTO(Long bookID, Long lendingCount) {
        this.bookID = bookID;
        this.lendingCount = lendingCount;
    }

    // Getter and setter for bookID
    public Long getBookID() {
        return bookID;
    }

    public void setBookID(Long bookID) {
        this.bookID = bookID;
    }

    // Getter and setter for bookCount
    public Long getBookCount() {
        return bookCount;
    }

    public void setBookCount(Long bookCount) {
        this.bookCount = bookCount;
    }

    // Getter and setter for lendingCount
    public Long getLendingCount() {
        return lendingCount;
    }

    public void setLendingCount(Long lendingCount) {
        this.lendingCount = lendingCount;
    }
}
