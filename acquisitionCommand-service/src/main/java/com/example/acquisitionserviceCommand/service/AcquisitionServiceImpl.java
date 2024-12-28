package com.example.acquisitionserviceCommand.service;

import com.example.acquisitionserviceCommand.dto.AcquisitionSyncDTO;
import com.example.acquisitionserviceCommand.dto.BookSyncDTO;
import com.example.acquisitionserviceCommand.dto.CreateAcquisitionDTO;
import com.example.acquisitionserviceCommand.messaging.RabbitMQProducer;
import com.example.acquisitionserviceCommand.model.Acquisition;
import com.example.acquisitionserviceCommand.dto.UserSyncDTO;
import com.example.acquisitionserviceCommand.model.AcquisitionStatus;
import com.example.acquisitionserviceCommand.model.Reader;
import com.example.acquisitionserviceCommand.repositories.AcquisitionRepository;
import com.example.acquisitionserviceCommand.repositories.ReaderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class AcquisitionServiceImpl implements AcquisitionService {

    private static final Logger logger = LoggerFactory.getLogger(AcquisitionServiceImpl.class);

    private final AcquisitionRepository acquisitionRepository;
    private final ReaderRepository readerRepository;
    private final RabbitMQProducer rabbitMQProducer;

    @Value("${instance.id}")
    private String instanceId;

    public AcquisitionServiceImpl(AcquisitionRepository acquisitionRepository, ReaderRepository readerRepository, RabbitMQProducer rabbitMQProducer) {
        this.acquisitionRepository = acquisitionRepository;
        this.readerRepository = readerRepository;
        this.rabbitMQProducer = rabbitMQProducer;
    }

    @Override
    @Transactional
    public Acquisition createAcquisition(CreateAcquisitionDTO dto) {
        if (acquisitionRepository.existsByIsbn(dto.getIsbn())) {
            throw new IllegalArgumentException("O ISBN já foi sugerido para aquisição.");
        }

        Acquisition acquisition = new Acquisition(
                dto.getReaderID(),
                dto.getIsbn(),
                dto.getTitle(),
                dto.getDescription(),
                dto.getReason(),
                dto.getAuthorIds(),
                dto.getGenre()
        );

        Acquisition savedAcquisition = acquisitionRepository.save(acquisition);

        sendSyncEvent(savedAcquisition);

        return savedAcquisition;
    }

    @Override
    @Transactional
    public Acquisition approveAcquisition(String acquisitionID) {
        Acquisition acquisition = acquisitionRepository.findById(acquisitionID)
                .orElseThrow(() -> new IllegalArgumentException("Aquisição não encontrada com o ID: " + acquisitionID));

        acquisition.setStatus(AcquisitionStatus.APPROVED);
        Acquisition approvedAcquisition = acquisitionRepository.save(acquisition);

        sendSyncStatus(approvedAcquisition);
        sendBookCreationEvent(approvedAcquisition);

        return approvedAcquisition;
    }

    @Override
    @Transactional
    public Acquisition rejectAcquisition(String acquisitionID, String reason) {
        Acquisition acquisition = acquisitionRepository.findById(acquisitionID)
                .orElseThrow(() -> new IllegalArgumentException("Aquisição não encontrada com o ID: " + acquisitionID));

        acquisition.setStatus(AcquisitionStatus.REJECTED);
        acquisition.setReason(reason);

        Acquisition rejectedAcquisition = acquisitionRepository.save(acquisition);

        sendSyncStatus(rejectedAcquisition);
        return rejectedAcquisition;
    }

    private void sendBookCreationEvent(Acquisition acquisition) {
        BookSyncDTO bookSyncDTO = new BookSyncDTO(
                acquisition.getIsbn(),
                acquisition.getTitle(),
                acquisition.getGenre(),
                acquisition.getDescription()
        );

        rabbitMQProducer.sendBookSyncEvent(bookSyncDTO);
        logger.info("Evento de sincronização de livro enviado para o RabbitMQ: {}", bookSyncDTO);
    }

    private void sendSyncEvent(Acquisition acquisition) {
        AcquisitionSyncDTO syncDTO = new AcquisitionSyncDTO(
                acquisition.getAcquisitionID(),
                acquisition.getStatus().name(),
                instanceId,
                acquisition.getReaderID(),
                acquisition.getIsbn(),
                acquisition.getTitle(),
                acquisition.getDescription(),
                acquisition.getReason(),
                acquisition.getAuthorIds(),
                acquisition.getGenre()
        );

        rabbitMQProducer.sendMessage("acquisition.sync", syncDTO);
        logger.info("Evento de sincronização de aquisição enviado: {}", syncDTO);
    }

    private void sendSyncStatus(Acquisition acquisition) {
        AcquisitionSyncDTO syncDTO = new AcquisitionSyncDTO(
                acquisition.getAcquisitionID(),
                acquisition.getStatus().name(),
                instanceId,
                acquisition.getReaderID(),
                acquisition.getIsbn(),
                acquisition.getTitle(),
                acquisition.getDescription(),
                acquisition.getReason(),
                acquisition.getAuthorIds(),
                acquisition.getGenre()
        );

        rabbitMQProducer.sendStatusSyncMessage(syncDTO);
        logger.info("Evento de sincronização de status enviado: {}", syncDTO);
    }


    public void updateAcquisitionStatus(String acquisitionId, String status) {
        Acquisition acquisition = acquisitionRepository.findById(acquisitionId)
                .orElseThrow(() -> new IllegalArgumentException("Aquisição não encontrada com o ID: " + acquisitionId));

        AcquisitionStatus acquisitionStatus = AcquisitionStatus.valueOf(status);

        if (!acquisition.getStatus().equals(acquisitionStatus)) {
            acquisition.setStatus(acquisitionStatus);
            acquisitionRepository.save(acquisition);
            logger.info("Status atualizado para aquisição: {} -> {}", acquisitionId, status);
        } else {
            logger.info("Aquisição já está sincronizada com o status: {}", status);
        }
    }




    public Reader createFromUserSyncDTO(UserSyncDTO userSyncDTO) {
        logger.info("Criando Reader a partir de UserSyncDTO: {}", userSyncDTO);

        if (readerRepository.existsByEmail(userSyncDTO.getUsername())) {
            return readerRepository.findByEmail(userSyncDTO.getUsername()).get();
        }

        Reader reader = new Reader();
        reader.setEmail(userSyncDTO.getUsername());
        reader.setFullName(userSyncDTO.getFullName());
        reader.setPassword(userSyncDTO.getPassword());
        reader.setEnabled(userSyncDTO.isEnabled());
        reader.setPhoneNumber(userSyncDTO.getPhoneNumber());

        logger.info("Criando Reader: username={}, phoneNumber={}", reader.getEmail(), reader.getPhoneNumber());

        reader.setUniqueReaderID();
        reader.setBirthdate(String.valueOf(LocalDate.of(2000, 1, 1)));

        return readerRepository.save(reader);
    }

    public void syncAcquisitionFromConsumer(AcquisitionSyncDTO syncDTO) {
        acquisitionRepository.findById(syncDTO.getAcquisitionId()).ifPresentOrElse(
                existingAcquisition -> {
                    logger.info("Aquisição já existe. Sincronização ignorada para ID: {}", syncDTO.getAcquisitionId());
                },
                () -> {
                    logger.info("Criando nova aquisição com ID: {}", syncDTO.getAcquisitionId());
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
                    logger.info("Nova aquisição criada e sincronizada: {}", syncDTO.getAcquisitionId());
                }
        );
    }

    public void approveAcquisitionFromConsumer(String acquisitionId, String status) {
        Acquisition acquisition = acquisitionRepository.findById(acquisitionId)
                .orElseThrow(() -> new IllegalArgumentException("Aquisição não encontrada com o ID: " + acquisitionId));

        AcquisitionStatus acquisitionStatus = AcquisitionStatus.valueOf(status);

        if (!acquisition.getStatus().equals(acquisitionStatus)) {
            acquisition.setStatus(acquisitionStatus);
            acquisitionRepository.save(acquisition);
            logger.info("Status atualizado para aprovação: {} -> {}", acquisitionId, status);
        } else {
            logger.info("Aquisição já está sincronizada com o status: {}", status);
        }
    }

    public void rejectAcquisitionFromConsumer(String acquisitionId, String status) {
        Acquisition acquisition = acquisitionRepository.findById(acquisitionId)
                .orElseThrow(() -> new IllegalArgumentException("Aquisição não encontrada com o ID: " + acquisitionId));

        AcquisitionStatus acquisitionStatus = AcquisitionStatus.valueOf(status);

        if (!acquisition.getStatus().equals(acquisitionStatus)) {
            acquisition.setStatus(acquisitionStatus);
            acquisitionRepository.save(acquisition);
            logger.info("Status atualizado para rejeição: {} -> {}", acquisitionId, status);
        } else {
            logger.info("Aquisição já está sincronizada com o status: {}", status);
        }
    }


}
