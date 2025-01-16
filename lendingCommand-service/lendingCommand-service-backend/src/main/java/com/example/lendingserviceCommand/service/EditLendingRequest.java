package com.example.lendingserviceCommand.service;

import jakarta.persistence.Column;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditLendingRequest {

    @Column(nullable = true)
    @FutureOrPresent(message = "A data de devolução não pode ser no passado.")
    private LocalDate returnDate;

    @Column(nullable = true)
    private String notes;

    // Novo campo (importante para Recommendation)
    private String recommendation; // Ex.: "positive" ou "negative"
}
