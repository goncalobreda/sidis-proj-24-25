package com.example.recommendationserviceCommand.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "RECOMMENDATION")
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private Long bookID;

    @Column(nullable=false)
    private String readerID;

    @Column(nullable=false)
    private String recommendation; // "positive"/"negative"

    private LocalDateTime createdAt;

    // getters, setters, construtores
    public Recommendation() {}

    public Recommendation(Long bookID, String readerID, String recommendation) {
        this.bookID = bookID;
        this.readerID = readerID;
        this.recommendation = recommendation;
        this.createdAt = LocalDateTime.now();
    }
}
