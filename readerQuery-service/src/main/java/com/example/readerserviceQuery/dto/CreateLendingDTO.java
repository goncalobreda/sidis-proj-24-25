package com.example.readerserviceQuery.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para criar um novo Lending via POST /api/lendings
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateLendingDTO {

    @NotNull
    private Long bookID;

    @NotNull
    private String readerID;
}
