package com.example.bookservice.model;

import com.example.bookservice.model.Book;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.StaleObjectStateException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pk; // database primary key

    // optimistic-lock
    @Version
    private long version;

    private String name;
    private String biography;

    @Column(name = "authorID", unique = true, nullable = false)
    private String authorID;

    @ManyToMany(mappedBy = "author")
    @JsonIgnoreProperties("author")
    private List<Book> books = new ArrayList<>();

    @OneToOne(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private AuthorImage image;

    private static final long serialVersionUID = 1L;

    @Setter
    @Getter
    private boolean enabled = true;

    public Author() {
    }

    private static int currentYear = Year.now().getValue();
    private static int counter = 0;

    public void initCounter(String lastAuthorID) {
        if (lastAuthorID != null && !lastAuthorID.isBlank()) {
            // Split the lastAuthorID into year and counter
            String[] parts = lastAuthorID.split("/");
            if (parts.length == 2) {
                currentYear = Integer.parseInt(parts[0]);
                counter = Integer.parseInt(parts[1]);
            }
        }
    }

    private String generateUniqueAuthorID() {
        if (Year.now().getValue() != currentYear) {
            currentYear = Year.now().getValue();
            counter = 0;
        }

        counter++;
        String idCounter = String.format("%d", counter);
        return currentYear + "/" + idCounter;
    }

    public Author(String name, String biography) {
        this.name = name;
        this.biography = biography;
        this.authorID = generateUniqueAuthorID();
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        if (name == null || name.length() > 150) {
            throw new IllegalArgumentException("The name cannot be null, nor have more than 150 characters.");
        }
        this.name = name;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(final String biography) {
        if (biography == null || biography.length() > 4096) {
            throw new IllegalArgumentException("The biography cannot be null, nor have more than 4096 characters.");
        }
        this.biography = biography;
    }

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }
    public void setUniqueAuthorID(){
        this.authorID = generateUniqueAuthorID();
    }

    public Long getVersion() {  // for generating the etag
        return version;
    }

    public void setVersion(long version) { // Setter for version
        this.version = version;
    }

    public AuthorImage getImage() {
        return image;
    }

    public void setImage(AuthorImage image) {
        this.image = image;
    }

    public void applyPatch(final long desiredVersion, final String name, final String biography) {
        // check current version
        if (this.version != desiredVersion) throw new StaleObjectStateException("Object was already modified by another user", this.pk);


        if (biography!= null) setBiography(biography);
        if (name!= null) setName(name);
    }
}
