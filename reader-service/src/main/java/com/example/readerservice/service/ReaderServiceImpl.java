package com.example.readerservice.service;

import com.example.readerservice.client.*;
import com.example.readerservice.exceptions.ConflictException;
import com.example.readerservice.exceptions.NotFoundException;
import com.example.readerservice.model.Reader;
import com.example.readerservice.model.ReaderCountDTO;
import com.example.readerservice.repositories.ReaderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class ReaderServiceImpl implements ReaderService {

    private final ReaderRepository readerRepository;
    private final EditReaderMapper editReaderMapper;
    private final LendingServiceClient lendingServiceClient;
    private final BookServiceClient bookServiceClient;

    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(ReaderServiceImpl.class);



    @Value("${reader.instance1.url}")
    private String readerInstance1Url;

    @Value("${reader.instance2.url}")
    private String readerInstance2Url;

    @Value("${server.port}")
    private String currentPort;



    public ReaderServiceImpl(ReaderRepository readerRepository, EditReaderMapper editReaderMapper, LendingServiceClient lendingServiceClient, BookServiceClient bookServiceClient, RestTemplate restTemplate) {
        this.readerRepository = readerRepository;
        this.editReaderMapper = editReaderMapper;
        this.lendingServiceClient = lendingServiceClient;
        this.bookServiceClient = bookServiceClient;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<Reader> findAll() {
        return readerRepository.findAll();
    }

    // Método para criar o Reader e sincronizar com a outra instância
    public Reader createAndSync(CreateReaderRequest request) {
        Reader reader = create(request);

        // Garantir que o readerID está definido antes de sincronizar
        if (reader.getReaderID() == null) {
            reader.setUniqueReaderID();
        }

        notifyOtherInstance(reader);  // Sincroniza com a outra instância
        return reader;
    }



    @Override
    public Reader create(CreateReaderRequest request) {
        if (readerRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ConflictException("Email already exists! Cannot create a new reader.");
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
        logger.info("Generated readerID: ", reader.getReaderID()); // Verifique o valor aqui

        return readerRepository.save(reader);
    }

    // Método para notificar a outra instância após a criação
    public void notifyOtherInstance(Reader reader) {
        // Define o URL da outra instância com base na porta atual
        String otherInstanceUrl = currentPort.equals("8086") ? readerInstance2Url : readerInstance1Url;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Cria uma entidade HTTP com o Reader para envio
            HttpEntity<Reader> entity = new HttpEntity<>(reader, headers);

            // Envia para o endpoint interno de registro da outra instância
            ResponseEntity<Void> response = restTemplate.postForEntity(otherInstanceUrl + "/api/readers/internal/register", entity, Void.class);

            if (response.getStatusCode() != HttpStatus.CREATED) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao notificar a outra instância do Reader Service: " + response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Erro ao notificar a outra instância do Reader Service: {}", e.getMessage());
        }
    }


    public void syncReceivedReader(Reader reader) {
        // Certifica que o readerID não é nulo, gerando se necessário
        if (reader.getReaderID() == null) {
            reader.setUniqueReaderID();
        }

        Optional<Reader> existingReader = readerRepository.findByReaderID(reader.getReaderID());
        if (existingReader.isPresent()) {
            System.out.println("Reader já existente, ignorando criação: " + reader.getReaderID());
            return;  // Evita duplicação
        }

        readerRepository.save(reader);
        System.out.println("Reader sincronizado com ID: " + reader.getReaderID());
    }





    public Reader partialUpdate(final String readerID, final EditReaderRequest request, final long desiredVersion) {
        final var reader = readerRepository.findByReaderID(readerID)
                .orElseThrow(() -> new NotFoundException("Cannot update an object that does not yet exist"));

        if (request.getBirthdate() != null) {
            validateBirthdate(request.getBirthdate());
        }

        reader.applyPatch(desiredVersion, request.getFullName(), null, request.getEmail(), request.getBirthdate(),
                request.getPhoneNumber(), request.isGDPR(), request.getInterests());

        readerRepository.save(reader);

        return reader;
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
        if (birthdate == null) throw new IllegalArgumentException("Birthdate cannot be null");
        if (!birthdate.isBlank()) {
            String[] parts = birthdate.split("-");
            if (parts.length != 3) throw new IllegalArgumentException("Birthdate must be in the format YYYY-MM-DD");

            try {
                int birthdateDay = Integer.parseInt(parts[2]);
                int birthdateMonth = Integer.parseInt(parts[1]);
                int birthdateYear = Integer.parseInt(parts[0]);

                if (birthdateYear <= 0) throw new IllegalArgumentException("Year must be positive");
                if (birthdateMonth < 1 || birthdateMonth > 12) throw new IllegalArgumentException("Month must be between 1 and 12");
                if (birthdateDay < 1 || birthdateDay > 31) throw new IllegalArgumentException("Day must be between 1 and 31 for the given month");

                if ((birthdateMonth == 4 || birthdateMonth == 6 || birthdateMonth == 9 || birthdateMonth == 11) && birthdateDay > 30) {
                    throw new IllegalArgumentException("Day must be between 1 and 30 for the given month");
                }

                if (birthdateMonth == 2) {
                    boolean isLeapYear = (birthdateYear % 4 == 0 && birthdateYear % 100 != 0) || (birthdateYear % 400 == 0);
                    int maxDayInFebruary = isLeapYear ? 29 : 28;
                    if (birthdateDay > maxDayInFebruary) {
                        throw new IllegalArgumentException("Day must be between 1 and " + maxDayInFebruary + " for February");
                    }
                }

                if (LocalDate.of(birthdateYear, birthdateMonth, birthdateDay).isAfter(LocalDate.now().minusYears(12))) {
                    throw new IllegalArgumentException("Minimum age is 12");
                }

            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Birthdate must contain valid integers for day, month, and year", e);
            }
        }
    }

    public List<ReaderCountDTO> findTop5Readers() {
        // Obter todos os lendings
        List<LendingDTO> lendings = lendingServiceClient.getAllLendings();

        // Contar quantos empréstimos cada reader fez
        Map<String, Long> readerIdCounts = lendings.stream()
                .collect(Collectors.groupingBy(LendingDTO::getReaderID, Collectors.counting()));

        // Obter os 5 readers com mais empréstimos
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
