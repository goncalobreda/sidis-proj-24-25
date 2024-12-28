package com.example.acquisitionserviceCommand.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Genre {


    public static final String ACAO = "Ação";
    public static final String FICCAO_CIENTIFICA = "Ficção Científica";
    public static final String ROMANCE = "Romance";
    public static final String MISTERIO = "Mistério";
    public static final String FANTASIA = "Fantasia";
    public static final String HISTORIA = "História";
    public static final String BIOGRAFIA = "Biografia";
    public static final String TERROR = "Terror";
    public static final String AVENTURA = "Aventura";
    public static final String DRAMA = "Drama";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String interest;

    @OneToMany(mappedBy = "genre", orphanRemoval = true,
            cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Book> books;

    public Genre(String interest) {
        this.interest = interest;
        this.books = new ArrayList<>();
    }

    public Genre() {}

    public Long getId() {
        return id;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

}
