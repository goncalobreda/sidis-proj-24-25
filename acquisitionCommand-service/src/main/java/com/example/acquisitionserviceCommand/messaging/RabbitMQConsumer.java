package com.example.acquisitionserviceCommand.messaging;

import com.example.acquisitionserviceCommand.dto.AcquisitionSyncDTO;
import com.example.acquisitionserviceCommand.dto.BookCreationResponseDTO;
import com.example.acquisitionserviceCommand.dto.UserSyncDTO;
import com.example.acquisitionserviceCommand.service.AcquisitionServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConsumer {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConsumer.class);

    private final AcquisitionServiceImpl acquisitionService;

    @Value("${instance.id}")
    private String instanceId;

    @Value("${rabbitmq.queue.book.creation.result:acquisition.book.creation.result.queue}")
    private String bookCreationResultQueueName;

    public RabbitMQConsumer(AcquisitionServiceImpl acquisitionService) {
        this.acquisitionService = acquisitionService;
    }

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void processSyncMessage(UserSyncDTO userSyncDTO) {
        logger.info("Mensagem recebida para sincronizar User: {}", userSyncDTO);

        // Verificar se a mensagem não é da própria instância
        if (instanceId.equals(userSyncDTO.getOriginInstanceId())) {
            logger.info("Mensagem ignorada. Originada da própria instância: {}", instanceId);
            return;
        }

        try {
            logger.info("Processando sincronização para o User: {}", userSyncDTO.getUsername());
            acquisitionService.createFromUserSyncDTO(userSyncDTO);
            logger.info("User sincronizado com sucesso: {}", userSyncDTO.getUsername());
        } catch (Exception e) {
            logger.error("Erro ao processar mensagem de sincronização: {}", e.getMessage(), e);
        }
    }

    @RabbitListener(queues = "${rabbitmq.command.queue.name}")
    public void processAcquisitionSync(AcquisitionSyncDTO syncDTO) {
        logger.info("Mensagem recebida para sincronização de aquisição: {}", syncDTO);

        if (instanceId.equals(syncDTO.getOriginInstanceId())) {
            logger.info("Mensagem ignorada. Originada da mesma instância: {}", instanceId);
            return;
        }

        try {
            acquisitionService.syncAcquisitionFromConsumer(syncDTO);
            logger.info("Sincronização aplicada com sucesso para a aquisição: {}", syncDTO.getAcquisitionId());
        } catch (Exception e) {
            logger.error("Erro ao processar mensagem de sincronização: {}", e.getMessage(), e);
        }
    }

    @RabbitListener(queues = "${rabbitmq.status.sync.queue.name}")
    public void processStatusSyncMessage(AcquisitionSyncDTO syncDTO) {
        logger.info("Mensagem recebida para sincronização de status: {}", syncDTO);

        // Ignorar mensagens originadas da mesma instância
        if (instanceId.equals(syncDTO.getOriginInstanceId())) {
            logger.info("Mensagem ignorada. Originada da própria instância: {}", instanceId);
            return;
        }

        try {
            logger.info("Processando sincronização de status para aquisição: {}", syncDTO.getAcquisitionId());
            acquisitionService.updateAcquisitionStatus(syncDTO.getAcquisitionId(), syncDTO.getStatus());
            logger.info("Sincronização de status aplicada com sucesso para a aquisição: {}", syncDTO.getAcquisitionId());
        } catch (Exception e) {
            logger.error("Erro ao processar mensagem de sincronização de status: {}", e.getMessage(), e);
        }
    }

    @RabbitListener(queues = "${rabbitmq.queue.book.creation.result:acquisition.book.creation.result.queue}")
    public void handleBookCreationResult(BookCreationResponseDTO response) {
        logger.info("Recebida BookCreationResponseDTO: {}", response);

        try {
            if (response.isSuccess()) {
                acquisitionService.markAcquisitionAsApproved(response.getIsbn());
                logger.info("Aquisição com ISBN={} agora APPROVED.", response.getIsbn());
            } else {
                acquisitionService.markAcquisitionAsRejected(response.getIsbn(), response.getErrorReason());
                logger.warn("Aquisição com ISBN={} agora REJECTED. Motivo: {}",
                        response.getIsbn(), response.getErrorReason());
            }
        } catch (Exception e) {
            logger.error("Erro ao processar BookCreationResponseDTO: {}", e.getMessage(), e);
        }
    }


}
