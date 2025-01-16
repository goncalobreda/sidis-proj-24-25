package com.example.recommendationserviceCommand.messaging;

import com.example.recommendationserviceCommand.dto.RecommendationFailedEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMQProducer {

    private final Logger logger = LoggerFactory.getLogger(RabbitMQProducer.class);
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    public void sendRecommendationFailed(RecommendationFailedEvent event) {
        String routingKey = "recommendation.failed"; // ex.: recommendation.failed
        rabbitTemplate.convertAndSend(exchangeName, routingKey, event);
        logger.info("Enviado RecommendationFailedEvent via routingKey='{}': {}", routingKey, event);
    }
}
