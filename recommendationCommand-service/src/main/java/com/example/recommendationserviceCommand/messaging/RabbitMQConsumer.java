package com.example.recommendationserviceCommand.messaging;

import com.example.recommendationserviceCommand.dto.BookReturnedEvent;
import com.example.recommendationserviceCommand.dto.RecommendationFailedEvent;
import com.example.recommendationserviceCommand.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMQConsumer {

    private final Logger logger = LoggerFactory.getLogger(RabbitMQConsumer.class);
    private final RecommendationService recommendationService;
    private final RabbitMQProducer producer;

    @RabbitListener(queues = "${rabbitmq.bookreturned.queue.name}")
    public void processBookReturned(BookReturnedEvent event) {
        logger.info("Recebido BookReturnedEvent: {}", event);

        try {
            recommendationService.saveRecommendation(
                    event.getBookID(),
                    event.getReaderID(),
                    event.getRecommendation() // "positive"/"negative"
            );
            // Se quiseres emitir RecommendationCreatedEvent, podes aqui
        } catch (Exception e) {
            logger.error("Falha ao processar recomendação: {}", e.getMessage());

            RecommendationFailedEvent failEvent = new RecommendationFailedEvent();
            failEvent.setLendingID(event.getLendingID());
            failEvent.setReason("Could not save recommendation");
            producer.sendRecommendationFailed(failEvent);
        }
    }
}
