package com.example.bookservice.model;

import jakarta.persistence.*;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Book {
    @Version
    private long version;

    @Column(unique = true, updatable = false, nullable = false)
    private String isbn;

    @Column(unique = false, updatable = true, nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "genre_id")
    private Genre genre;

    @Column(unique = false, updatable = true, nullable = false)
    private String description;

    @ManyToMany
    @JoinTable(
            name = "book_author",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private List<Author> author = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "bookimage_id")
    private BookImage bookImage;

    @Id
    @GeneratedValue
    @Column(unique = true, updatable = false, nullable = false)
    private Long bookID;

    public Book() {}

    public Book(final String isbn, final String title, final Genre genre, final String description, final List<Author> author, final BookImage bookImage) {
        this.isbn = isbn;
        this.title = title;
        this.genre = genre;
        this.description = description;
        this.author = author;
        this.bookImage = bookImage;
    }


    public Long getBookID() {
        return bookID;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(final Genre genre) {
        if (genre == null || genre.getInterest().isBlank()) {
            throw new IllegalArgumentException("Genre must not be null, nor blank");
        }
        this.genre = genre;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title must not be null, nor blank");
        }
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description must not be null, nor blank");
        }
        this.description = description;
    }

    public List<Author> getAuthor() {
        return author;
    }

    public void setAuthor(List<Author> author) {
        this.author = author;
    }

    public BookImage getBookImage() {
        return bookImage;
    }

    public void setBookImage(BookImage bookImage) {
        this.bookImage = bookImage;
    }

    public Long getVersion() {
        return version;
    }

    public void applyPatch(long desiredVersion, String title, Genre genre, String description) {
        if (this.version != desiredVersion) {
            throw new OptimisticLockingFailureException("The entity was updated by another transaction");
        }
        this.title = title;
        this.genre = genre;
        this.description = description;
    }

    public void setBookID(long bookID) {
        this.bookID=bookID;
    }

    public void setVersion(long version) {
        this.version = version;
    }

}
