package com.example.lendingserviceCommand.dto;

import lombok.Data;

@Data
public class BookReturnedEvent {

    private String lendingID;       // ex: "2025/10"
    private Long bookID;            // ex: 101
    private String readerID;        // ex: "2024/7"
    private String recommendation;  // "positive" / "negative"

    // Podes adicionar returnDate se achares útil
}
