package com.example.bookservice.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditBookRequest {

    @Size(min = 1, max = 127)
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private String title; // serves as username

    @Size(min = 1, max = 2048)
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private String genre;

    @Size(min = 1, max = 2048)
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private String description;


}
