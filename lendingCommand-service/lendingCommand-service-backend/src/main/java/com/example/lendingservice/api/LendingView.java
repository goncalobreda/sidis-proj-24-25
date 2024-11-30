package com.example.lendingservice.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "A Lending")
public class LendingView {
    private String id;
    private LocalDate startDate;
    private LocalDate expectedReturnDate;
    private LocalDate returnDate;
    private boolean overdue;
    private int fine;
    private String readerID;  // Agora ID vindo de microserviço externo
    private Long bookID;  // Agora ID vindo de microserviço externo
    private String bookTitle;  // Nome do livro via chamada externa ao microserviço de livros
    private long numberOfDaysInOverdue;
    private String notes;
}
