package com.example.acquisitionserviceQuery.messaging;

import com.example.acquisitionserviceQuery.dto.AcquisitionSyncDTO;
import com.example.acquisitionserviceQuery.service.AcquisitionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConsumer {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConsumer.class);

    private final AcquisitionService acquisitionService;

    @Value("${instance.id}")
    private String instanceId;

    public RabbitMQConsumer(AcquisitionService acquisitionService) {
        this.acquisitionService = acquisitionService;
    }

    @RabbitListener(queues = "${rabbitmq.command.queue.name}")
    public void processAcquisitionSync(AcquisitionSyncDTO syncDTO) {
        logger.info("Mensagem recebida para sincronização no Query: {}", syncDTO);

        try {
            acquisitionService.syncAcquisitionFromConsumer(syncDTO);
            logger.info("Sincronização aplicada com sucesso no Query para aquisição: {}", syncDTO.getAcquisitionId());
        } catch (Exception e) {
            logger.error("Erro ao processar mensagem de sincronização no Query: {}", e.getMessage(), e);
        }
    }
}
