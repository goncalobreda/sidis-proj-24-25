package com.example.acquisitionserviceQuery.service;

import com.example.acquisitionserviceQuery.dto.AcquisitionSyncDTO;
import com.example.acquisitionserviceQuery.model.Acquisition;
import com.example.acquisitionserviceQuery.model.AcquisitionStatus;
import com.example.acquisitionserviceQuery.repositories.AcquisitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AcquisitionServiceImpl implements AcquisitionService {

    private static final Logger logger = LoggerFactory.getLogger(AcquisitionServiceImpl.class);

    private final AcquisitionRepository acquisitionRepository;

    public AcquisitionServiceImpl(AcquisitionRepository acquisitionRepository) {
        this.acquisitionRepository = acquisitionRepository;
    }

    public List<Acquisition> findAllAcquisitions() {
        return acquisitionRepository.findAll();
    }

    public List<Acquisition> findPendingAcquisitions() {
        return acquisitionRepository.findByStatus(AcquisitionStatus.PENDING_APPROVAL);
    }

    @Override
    public void syncAcquisitionFromConsumer(AcquisitionSyncDTO syncDTO) {
        acquisitionRepository.findById(syncDTO.getAcquisitionId()).ifPresentOrElse(
                existingAcquisition -> {
                    logger.info("Aquisição já existe no Query. Sincronização ignorada para ID: {}", syncDTO.getAcquisitionId());
                },
                () -> {
                    logger.info("Criando nova aquisição no Query com ID: {}", syncDTO.getAcquisitionId());
                    Acquisition newAcquisition = new Acquisition();
                    newAcquisition.setAcquisitionID(syncDTO.getAcquisitionId());
                    newAcquisition.setReaderID(syncDTO.getReaderID());
                    newAcquisition.setIsbn(syncDTO.getIsbn());
                    newAcquisition.setTitle(syncDTO.getTitle());
                    newAcquisition.setDescription(syncDTO.getDescription());
                    newAcquisition.setReason(syncDTO.getReason());
                    newAcquisition.setAuthorIds(syncDTO.getAuthorIds());
                    newAcquisition.setGenre(syncDTO.getGenre());
                    newAcquisition.setStatus(AcquisitionStatus.valueOf(syncDTO.getStatus()));

                    acquisitionRepository.save(newAcquisition);
                    logger.info("Nova aquisição criada no Query e sincronizada: {}", syncDTO.getAcquisitionId());
                }
        );
    }
}
