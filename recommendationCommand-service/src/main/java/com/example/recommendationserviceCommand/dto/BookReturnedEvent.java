// BookReturnedEvent.java
package com.example.recommendationserviceCommand.dto;

import lombok.Data;

@Data
public class BookReturnedEvent {
    private String lendingID;   // "2025/10"
    private Long bookID;
    private String readerID;
    private String recommendation; // "positive" / "negative"
}
