package com.example.bookservice.dto;

import com.example.bookservice.service.CreateBookRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreateBookRequestDTO {

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
    private List<String> authorIds;

    @NotNull
    private Long bookImageId;

    @Size(min = 1, max = 2048)
    @NotNull
    @NotBlank
    private String genre;

    // Conversão para o CreateBookRequest
    public CreateBookRequest toCreateBookRequest() {
        return new CreateBookRequest(
                this.isbn,
                this.title,
                this.description,
                this.authorIds,
                this.bookImageId,
                this.genre
        );
    }
}
