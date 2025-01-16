// RecommendationFailedEvent.java
package com.example.recommendationserviceCommand.dto;

import lombok.Data;

@Data
public class RecommendationFailedEvent {
    private String lendingID;
    private String reason;
}