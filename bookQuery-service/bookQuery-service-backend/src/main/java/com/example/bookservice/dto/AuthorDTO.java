package com.example.bookservice.dto;

import com.example.bookservice.model.Author;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDTO {
    private String authorID;
    private String name;
    private String biography;
    private String originInstanceId; // Novo campo para rastrear a instância de origem

    public static AuthorDTO fromAuthor(Author author) {
        return new AuthorDTO(author.getAuthorID(), author.getName(), author.getBiography(), null);
    }
}


