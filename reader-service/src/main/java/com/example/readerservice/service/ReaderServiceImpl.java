package com.example.readerservice.service;

import com.example.readerservice.client.*;
import com.example.readerservice.dto.UserSyncDTO;
import com.example.readerservice.messaging.RabbitMQProducer;
import com.example.readerservice.model.Reader;
import com.example.readerservice.model.ReaderCountDTO;
import com.example.readerservice.repositories.ReaderRepository;
import com.example.readerservice.exceptions.ConflictException;
import com.example.readerservice.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReaderServiceImpl implements ReaderService {

    private static final Logger logger = LoggerFactory.getLogger(ReaderServiceImpl.class);

    private final ReaderRepository readerRepository;
    private final EditReaderMapper editReaderMapper;
    private final LendingServiceClient lendingServiceClient;
    private final BookServiceClient bookServiceClient;
    private final RabbitMQProducer rabbitMQProducer;

    @Value("${instance.id}")
    private String instanceId;

    public ReaderServiceImpl(ReaderRepository readerRepository,
                             EditReaderMapper editReaderMapper,
                             LendingServiceClient lendingServiceClient,
                             BookServiceClient bookServiceClient,
                             RabbitMQProducer rabbitMQProducer) {
        this.readerRepository = readerRepository;
        this.editReaderMapper = editReaderMapper;
        this.lendingServiceClient = lendingServiceClient;
        this.bookServiceClient = bookServiceClient;
        this.rabbitMQProducer = rabbitMQProducer;
    }

    @Override
    public List<Reader> findAll() {
        return readerRepository.findAll();
    }

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
        logger.info("PhoneNumber recebido no UserSyncDTO: {}", userSyncDTO.getPhoneNumber());

        if (readerRepository.existsByEmail(userSyncDTO.getUsername())) {
            logger.info("Leitor já existe: {}", userSyncDTO.getUsername());
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

        // Definir um valor padrão para o birthdate
        reader.setBirthdate(String.valueOf(LocalDate.of(2000, 1, 1)));

        return readerRepository.save(reader);
    }



    public void syncReceivedReader(Reader reader) {
        if (readerRepository.existsByEmail(reader.getEmail())) {
            logger.info("Leitor já existente: {}", reader.getEmail());
            return;
        }

        readerRepository.save(reader);
        logger.info("Leitor sincronizado com sucesso: {}", reader.getEmail());
    }

    public Reader partialUpdate(final String readerID, final EditReaderRequest request, final long desiredVersion) {
        final var reader = readerRepository.findByReaderID(readerID)
                .orElseThrow(() -> new NotFoundException("Não é possível atualizar um objeto que não existe"));

        if (request.getBirthdate() != null) {
            validateBirthdate(request.getBirthdate());
        }

        reader.applyPatch(desiredVersion, request.getFullName(), null, request.getEmail(), request.getBirthdate(),
                request.getPhoneNumber(), request.isGDPR(), request.getInterests());

        return readerRepository.save(reader);
    }

    @Override
    public Optional<Reader> getReaderByID(final String readerID) {
        return readerRepository.findByReaderID(readerID);
    }

    @Override
    public Optional<Reader> getReaderByEmail(final String email) {
        return readerRepository.findByEmail(email);
    }

    @Override
    public List<Reader> getReaderByName(final String fullName) {
        return readerRepository.findByName(fullName);
    }

    public List<Reader> searchReaders(Page page, SearchReadersQuery query) {
        if (page == null) {
            page = new Page(1, 10);
        }
        if (query == null) {
            query = new SearchReadersQuery("", "", "");
        }
        return readerRepository.searchReaders(page, query);
    }

    private void validateBirthdate(final String birthdate) {
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

    public List<ReaderCountDTO> findTop5Readers() {
        List<LendingDTO> lendings = lendingServiceClient.getAllLendings();

        Map<String, Long> readerIdCounts = lendings.stream()
                .collect(Collectors.groupingBy(LendingDTO::getReaderID, Collectors.counting()));

        List<Map.Entry<String, Long>> top5Readers = readerIdCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());

        return top5Readers.stream()
                .map(entry -> new ReaderCountDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public List<GenreDTO> getBookSuggestions(Reader reader) {
        Set<String> interests = getInterestsByReader(reader);
        List<GenreDTO> suggestions = new ArrayList<>();

        for (String interest : interests) {
            suggestions.addAll(bookServiceClient.getBooksByGenre(interest));
        }

        return suggestions;
    }

    public Set<String> getInterestsByReader(Reader reader) {
        return reader.getInterests();
    }
}
