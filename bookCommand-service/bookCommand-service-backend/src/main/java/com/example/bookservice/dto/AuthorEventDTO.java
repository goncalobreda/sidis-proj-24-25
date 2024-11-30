package com.example.bookservice.dto;

import com.example.bookservice.model.Author;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorEventDTO {
    private String eventType;  // Type of event: "create", "update", "delete"
    private String authorID;   // Author ID
    private String name;       // Name of the author
    private String biography;  // Author biography

    public static AuthorEventDTO fromAuthor(String eventType, Author author) {
        return new AuthorEventDTO(
                eventType,
                author.getAuthorID(),
                author.getName(),
                author.getBiography()
        );
    }
}
