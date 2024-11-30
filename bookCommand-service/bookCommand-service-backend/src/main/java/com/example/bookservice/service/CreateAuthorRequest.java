package com.example.bookservice.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor

public class CreateAuthorRequest{

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @NotBlank
    private String biography;

    public CreateAuthorRequest(final String name, final String biography) {
        this.name = name;
        this.biography = biography;
    }
}

