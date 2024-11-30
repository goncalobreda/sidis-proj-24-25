package com.example.bookservice.model;

import jakarta.persistence.*;
@Entity
public class AuthorImage {
    @Id
    @GeneratedValue
    private Long authorImageID;

    @Lob
    private byte[] image;

    @OneToOne
    @JoinColumn(name = "author_id")
    private Author author;

    private String contentType;

    public AuthorImage() {}

    public AuthorImage(Author author, byte[] image, String contentType) {
        this.author = author;
        this.image = image;
        this.contentType = contentType;
    }

    public Long getAuthorImageID() {
        return authorImageID;
    }

    public void setAuthorImageID(Long authorImageID) {
        this.authorImageID = authorImageID;
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

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }
}