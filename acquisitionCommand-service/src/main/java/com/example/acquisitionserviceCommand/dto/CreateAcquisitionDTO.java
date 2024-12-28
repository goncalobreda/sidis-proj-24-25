package com.example.acquisitionserviceCommand.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAcquisitionDTO {

    @NotNull
    @NotBlank
    private String readerID; // ID do leitor que sugeriu a aquisição

    @NotNull
    @NotBlank
    @Size(min = 10, max = 10) // ISBN-10 padrão
    private String isbn; // ISBN do livro sugerido

    @NotNull
    @NotBlank
    @Size(max = 127)
    private String title; // Título do livro sugerido

    @NotNull
    @NotBlank
    @Size(max = 2048)
    private String description; // Descrição do livro sugerido

    @NotNull
    @NotBlank
    @Size(max = 2048)
    private String reason; // Razão pela qual o leitor sugeriu o livro

    @NotNull
    private List<String> authorIds; // IDs dos autores do livro sugerido

    @NotNull
    @NotBlank
    private String genre; // Gênero do livro sugerido
}
