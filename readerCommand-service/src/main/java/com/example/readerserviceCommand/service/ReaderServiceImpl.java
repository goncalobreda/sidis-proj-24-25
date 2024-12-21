package com.example.readerserviceCommand.service;


import com.example.readerserviceCommand.dto.PartialUpdateDTO;
import com.example.readerserviceCommand.dto.UserSyncDTO;
import com.example.readerserviceCommand.messaging.RabbitMQProducer;
import com.example.readerserviceCommand.model.Reader;
import com.example.readerserviceCommand.repositories.ReaderRepository;
import com.example.readerserviceCommand.exceptions.ConflictException;
import com.example.readerserviceCommand.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ReaderServiceImpl implements ReaderService {

    private static final Logger logger = LoggerFactory.getLogger(ReaderServiceImpl.class);

    private final ReaderRepository readerRepository;
    private final RabbitMQProducer rabbitMQProducer;

    @Value("${instance.id}")
    private String instanceId;

    public ReaderServiceImpl(ReaderRepository readerRepository, RabbitMQProducer rabbitMQProducer) {
        this.readerRepository = readerRepository;
        this.rabbitMQProducer = rabbitMQProducer;
    }

    // Métodos de criação e atualização (Command)

    @Override
    public Reader create(CreateReaderRequest request) {
        if (readerRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ConflictException("Email já existe! Não é possível criar um novo leitor.");
        }

        validateBirthdate(request.getBirthdate());

        Reader reader = new Reader(
                request.getFullName(),
                request.getPassword(),
                request.getEmail(),
                request.getBirthdate(),
                request.getPhoneNumber(),
                request.isGDPR()
        );

        reader.setUniqueReaderID();
        logger.info("Generated readerID: {}", reader.getReaderID());

        Reader savedReader = readerRepository.save(reader);

        // Notifica outras instâncias sobre o novo leitor
        rabbitMQProducer.sendSyncMessage(savedReader);

        return savedReader;
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

        // Definir o readerID
        reader.setUniqueReaderID();
        reader.setBirthdate(String.valueOf(LocalDate.of(2000, 1, 1)));

        return readerRepository.save(reader);
    }

    public void syncReceivedReader(Reader reader) {
        if (readerRepository.existsByEmail(reader.getEmail())) {
            return;
        }
        readerRepository.save(reader);
        logger.info("Leitor sincronizado com sucesso: {}", reader.getEmail());
    }

    @Override
    public Reader partialUpdate(final String readerID, final EditReaderRequest request, final long desiredVersion) {
        final Reader reader = readerRepository.findByReaderID(readerID)
                .orElseThrow(() -> new NotFoundException("Reader não encontrado: " + readerID));

        if (request.getPhoneNumber() != null) {
            reader.setPhoneNumber(request.getPhoneNumber());
        } else {
            throw new IllegalArgumentException("PhoneNumber é obrigatório para partial update.");
        }

        Reader updatedReader = readerRepository.save(reader);

        // Criação do PartialUpdateDTO
        PartialUpdateDTO partialUpdateDTO = new PartialUpdateDTO(
                readerID,
                request.getPhoneNumber(),
                instanceId
        );

        // Sempre envie a mensagem
        logger.info("Enviando mensagem de partial update para sincronização.");
        rabbitMQProducer.sendPartialUpdateMessage(partialUpdateDTO);

        return updatedReader;
    }

    public Reader partialUpdateFromConsumer(final String readerID, final EditReaderRequest request, final long desiredVersion) {
        final Reader reader = readerRepository.findByReaderID(readerID)
                .orElseThrow(() -> new NotFoundException("Reader não encontrado: " + readerID));

        if (request.getPhoneNumber() != null) {
            reader.setPhoneNumber(request.getPhoneNumber());
        } else {
            throw new IllegalArgumentException("PhoneNumber é obrigatório para partial update.");
        }

        Reader updatedReader = readerRepository.save(reader);

        logger.info("Partial update aplicado (SEM republicação) para o Reader: {}", readerID);

        return updatedReader;
    }



    public void validateBirthdate(final String birthdate) {
        if (birthdate == null) throw new IllegalArgumentException("A data de nascimento não pode ser nula");
        if (!birthdate.isBlank()) {
            String[] parts = birthdate.split("-");
            if (parts.length != 3) throw new IllegalArgumentException("Data de nascimento deve estar no formato YYYY-MM-DD");

            try {
                int birthdateDay = Integer.parseInt(parts[2]);
                int birthdateMonth = Integer.parseInt(parts[1]);
                int birthdateYear = Integer.parseInt(parts[0]);

                if (birthdateYear <= 0) throw new IllegalArgumentException("Ano deve ser positivo");
                if (birthdateMonth < 1 || birthdateMonth > 12) throw new IllegalArgumentException("Mês deve estar entre 1 e 12");
                if (birthdateDay < 1 || birthdateDay > 31) throw new IllegalArgumentException("Dia deve estar entre 1 e 31 para o mês dado");

                if ((birthdateMonth == 4 || birthdateMonth == 6 || birthdateMonth == 9 || birthdateMonth == 11) && birthdateDay > 30) {
                    throw new IllegalArgumentException("Dia deve estar entre 1 e 30 para o mês dado");
                }

                if (birthdateMonth == 2) {
                    boolean isLeapYear = (birthdateYear % 4 == 0 && birthdateYear % 100 != 0) || (birthdateYear % 400 == 0);
                    int maxDayInFebruary = isLeapYear ? 29 : 28;
                    if (birthdateDay > maxDayInFebruary) {
                        throw new IllegalArgumentException("Dia deve estar entre 1 e " + maxDayInFebruary + " para fevereiro");
                    }
                }

                if (LocalDate.of(birthdateYear, birthdateMonth, birthdateDay).isAfter(LocalDate.now().minusYears(12))) {
                    throw new IllegalArgumentException("Idade mínima é 12 anos");
                }

            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Data de nascimento deve conter inteiros válidos para dia, mês e ano", e);
            }
        }
    }
}
