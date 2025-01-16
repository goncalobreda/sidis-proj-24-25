package com.example.recommendationserviceCommand.repositories;

import com.example.recommendationserviceCommand.model.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    Optional<Recommendation> findByBookIDAndReaderID(Long bookID, String readerID);
}
