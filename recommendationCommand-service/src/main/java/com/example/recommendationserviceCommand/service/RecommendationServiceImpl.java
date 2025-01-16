package com.example.recommendationserviceCommand.service;

import com.example.recommendationserviceCommand.model.Recommendation;
import com.example.recommendationserviceCommand.repositories.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationRepository recommendationRepository;

    @Override
    public Recommendation saveRecommendation(Long bookID, String readerID, String recValue) {
        // podes verificar se já existe e atualizar, ou criar sempre novo
        Recommendation recommendation = new Recommendation(bookID, readerID, recValue);
        return recommendationRepository.save(recommendation);
    }
}
