package com.example.bookservice.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookRequest {

    @Size(min = 10, max = 13)
    @NotNull
    @NotBlank
    private String isbn;

    @Size(min = 1, max = 127)
    @NotNull
    @NotBlank
    private String title;

    @Size(min = 1, max = 2048)
    @NotNull
    @NotBlank
    private String description;

    @NotNull
    private List<String> authorIds; // Only the ID of the author

    @NotNull
    private Long bookImageId; // Only the ID of the book image

    @Size(min = 1, max = 2048)
    @NotNull
    @NotBlank
    private String genre; // Genre as a string for validation
}
