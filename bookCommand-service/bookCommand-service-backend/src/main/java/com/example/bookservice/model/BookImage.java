package com.example.bookservice.model;

import jakarta.persistence.*;

@Entity
public class BookImage {

    @Id
    @GeneratedValue
    private Long bookImageID;


    // in this case we are storing the image in the database, but it would be
    // "better" to store it in a server file system
    @Lob
    private byte[] image;

    @ManyToOne
    private Book book;

    private String contentType;

    public BookImage() {}

    public BookImage(Book book, byte[] image, String contentType) {
        this.book = book;
        this.image = image;
        this.contentType = contentType;
    }

    public Long getBookImageID() {
        return bookImageID;
    }

    public void setBookImageID(Long bookImageID) {
        this.bookImageID = bookImageID;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Book getBook() {
        return book;
    }
    public void setBook(Book book) {
        this.book = book;
    }

}
