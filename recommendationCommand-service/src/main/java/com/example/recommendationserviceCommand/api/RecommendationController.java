package com.example.recommendationserviceCommand.api;

import com.example.recommendationserviceCommand.model.Recommendation;
import com.example.recommendationserviceCommand.repositories.RecommendationRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationRepository recommendationRepository;

    public RecommendationController(RecommendationRepository recommendationRepository) {
        this.recommendationRepository = recommendationRepository;
    }

    @GetMapping
    public List<Recommendation> findAll() {
        // Retorna todas as recomendações
        return recommendationRepository.findAll();
    }

    @GetMapping("/book/{bookID}")
    public List<Recommendation> findByBook(@PathVariable Long bookID) {
        // Exemplo de método se criaste no repository
        return recommendationRepository.findByBookID(bookID);
    }
}
