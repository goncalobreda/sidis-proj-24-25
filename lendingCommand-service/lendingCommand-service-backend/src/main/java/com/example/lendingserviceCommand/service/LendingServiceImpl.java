package com.example.lendingserviceCommand.service;

import com.example.lendingserviceCommand.dto.CreateLendingDTO;
import com.example.lendingserviceCommand.dto.UserSyncDTO;
import com.example.lendingserviceCommand.model.Reader;
import com.example.lendingserviceCommand.repositories.ReaderRepository;
import com.example.lendingserviceCommand.service.CreateLendingRequest;
import com.example.lendingserviceCommand.exceptions.NotFoundException;
import com.example.lendingserviceCommand.messaging.RabbitMQProducer;
import com.example.lendingserviceCommand.model.Lending;
import com.example.lendingserviceCommand.dto.BookReturnedEvent;
import com.example.lendingserviceCommand.repositories.LendingRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class LendingServiceImpl implements LendingService {

    private static final Logger logger = LoggerFactory.getLogger(LendingServiceImpl.class);


    private final LendingRepository lendingRepository;
    private final ReaderRepository readerRepository;
    private final RabbitMQProducer rabbitMQProducer;

    private final ReaderService readerService;


    @Value("${instance.id}")
    private String instanceId; // Identificador da instância atual


    public Reader syncUserLendingData(UserSyncDTO userSyncDTO) {
        logger.info("Sincronizando dados de UserSyncDTO no contexto de Lending: {}", userSyncDTO);

        // Verificar se o leitor já existe pelo email
        Optional<Reader> existingReader = readerRepository.findByEmail(userSyncDTO.getUsername());
        if (existingReader.isPresent()) {
            logger.info("Usuário já existe: {}", userSyncDTO.getUsername());
            return existingReader.get(); // Retorna o leitor existente
        }

        // Determinar o próximo ID de Reader


        // Criar um novo Reader
        Reader reader = new Reader();
        reader.setEmail(userSyncDTO.getUsername());
        reader.setFullName(userSyncDTO.getFullName());
        reader.setEnabled(userSyncDTO.isEnabled());
        reader.setVersion(0L); // Inicializar versão para evitar erro de persistência

        logger.info("Usuário criado para Lending: email={}, fullName={}", reader.getEmail(), reader.getFullName());

        reader.setUniqueReaderID();

        return readerRepository.save(reader); // Salva no repositório
    }



    @Override
    public Iterable<Lending> findAll() {
        return lendingRepository.findAll();
    }

    @Override
    public Optional<Lending> findById(int id1, int id2) {
        String lendingID = id1 + "/" + id2;
        return lendingRepository.findByLendingID(lendingID);
    }

    @Override
    public Optional<String> findReaderByLendingID(String lendingID) {
        return lendingRepository.findReaderByLendingID(lendingID);
    }

    @Override
    public Optional<Lending> getLastId() {
        return lendingRepository.findFirstByOrderByLendingIDDesc();
    }

    public List<Lending> getOverdueLendingsSortedByTardiness() {
        return lendingRepository.findByOverdueTrueOrderByTardinessDesc();
    }

    @Override
    public Lending create(CreateLendingDTO dto) {
        // 1) Validações
        Long bookID = dto.getBookID();
        String readerID = dto.getReaderID();

        if (lendingRepository.existsByReaderIDAndOverdueTrue(readerID)) {
            throw new IllegalArgumentException("Reader has overdue lending and cannot borrow more books.");
        }
        long activeLendingsCount = lendingRepository.countActiveLendingsByReaderID(readerID);
        if (activeLendingsCount >= 3) {
            throw new IllegalArgumentException("Reader already has the maximum number of active lendings (3).");
        }

        // 2) Cria Lending
        LocalDate startDate = LocalDate.now();
        LocalDate expectedReturnDate = startDate.plusDays(14);
        Lending lending = new Lending(bookID, readerID, startDate, null, expectedReturnDate, false, 0);
        lending.updateOverdueStatus();
        Lending savedLending = lendingRepository.save(lending);

        // 3) Envia Mensagem p/ Query
        rabbitMQProducer.sendCreateMessage(savedLending);

        return savedLending;
    }


    @Override
    public Lending partialUpdate(int id1, int id2, EditLendingRequest resource, long desiredVersion) {
        String lendingID = id1 + "/" + id2;
        Lending lending = lendingRepository.findByLendingID(lendingID)
                .orElseThrow(() -> new NotFoundException("Lending not found."));

        // 1) returnDate
        if (resource.getReturnDate() != null) {
            lending.setReturnDate(resource.getReturnDate());
            lending.updateOverdueStatus();
            lending.setFine(calculateFine(lending));
        }

        // 2) notes
        lending.setNotes(resource.getNotes());

        // 3) Salva no repositório

        Lending updatedLending = lendingRepository.save(lending);

        // 4) Continua a enviar partial update p/ Query (como antes)
        rabbitMQProducer.sendPartialUpdateMessage(updatedLending);

        // 5) Verifica recommendation
        String recommendation = resource.getRecommendation();
        if (recommendation != null && !recommendation.isBlank()) {
            // Construir BookReturnedEvent
            BookReturnedEvent event = new BookReturnedEvent();
            event.setLendingID(lending.getLendingID());
            event.setBookID(lending.getBookID());
            event.setReaderID(lending.getReaderID());
            event.setRecommendation(recommendation);

            // Enviar ao RecommendationCommand
            rabbitMQProducer.sendBookReturnedEvent(event);
            logger.info("BookReturnedEvent enviado com recommendation={}", recommendation);
        }

        return updatedLending;
    }


    @Override
    public int calculateFine(String lendingID) {
        Optional<Lending> lending = lendingRepository.findByLendingID(lendingID);
        return lending.map(this::calculateFine).orElse(0);
    }

    private int calculateFine(Lending lending) {
        LocalDate expectedReturnDate = lending.getExpectedReturnDate();
        LocalDate returnDate = lending.getReturnDate();
        if (returnDate != null && returnDate.isAfter(expectedReturnDate)) {
            long daysLate = ChronoUnit.DAYS.between(expectedReturnDate, returnDate);
            return (int) (daysLate * 5);
        }
        return 0;
    }

    @Override
    public double getAverageLendingDuration() {
        List<Lending> lendings = lendingRepository.findLendingsWithReturnDate();
        if (lendings.isEmpty()) {
            return 0;
        }

        long totalDays = lendings.stream()
                .mapToLong(l -> ChronoUnit.DAYS.between(l.getStartDate(), l.getReturnDate()))
                .sum();

        return (double) totalDays / lendings.size();
    }

    @Override
    public Map<String, Double> getAverageLendingsPerGenre(int month, int year) {
        Map<String, Double> genreLendingAverage = new HashMap<>();
        Map<String, Integer> genreCount = new HashMap<>();

        // Obter os empréstimos para cada livro no mês/ano
        List<Object[]> lendings = lendingRepository.findLendingsDurationByBookAndMonth(month, year);

        // Total de empréstimos para o cálculo da média
        int totalLendings = lendings.size();

        // Iterar pelos resultados e contar o número de empréstimos para cada género
        for (Object[] lending : lendings) {
            String genre = (String) lending[1]; // Considera que o género está presente na consulta SQL
            genreCount.put(genre, genreCount.getOrDefault(genre, 0) + 1);
        }

        // Calcular a média de empréstimos por género
        for (String genre : genreCount.keySet()) {
            int count = genreCount.get(genre);
            genreLendingAverage.put(genre, (double) count / totalLendings);
        }

        return genreLendingAverage;
    }
}
