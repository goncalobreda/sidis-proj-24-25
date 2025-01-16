package com.example.recommendationserviceCommand.service;

import com.example.recommendationserviceCommand.model.Recommendation;

public interface RecommendationService {

    Recommendation saveRecommendation(Long bookID, String readerID, String recValue);

    // se quiseres partialUpdate, etc.
}
