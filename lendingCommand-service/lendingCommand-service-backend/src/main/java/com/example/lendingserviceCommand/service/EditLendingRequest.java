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
    @Getter
    @Setter
    @FutureOrPresent(message = "A data de devolução não pode ser no passado.")
    private LocalDate returnDate;

    @Column(nullable = true)
    @Getter
    @Setter
    private String notes;
}
