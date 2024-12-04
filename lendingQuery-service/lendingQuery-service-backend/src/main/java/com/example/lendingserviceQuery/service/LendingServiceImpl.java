package com.example.lendingserviceQuery.service;

import com.example.lendingserviceQuery.dto.UserSyncDTO;
import com.example.lendingserviceQuery.model.Reader;
import com.example.lendingserviceQuery.repositories.ReaderRepository;
import com.example.lendingserviceQuery.service.CreateLendingRequest;
import com.example.lendingserviceQuery.exceptions.NotFoundException;
import com.example.lendingserviceQuery.messaging.RabbitMQProducer;
import com.example.lendingserviceQuery.model.Lending;
import com.example.lendingserviceQuery.repositories.LendingRepository;
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
    public Lending create(CreateLendingRequest request) {
        // Validação e sincronização via RabbitMQ
        Long bookID = request.getBookID();
        String readerID = request.getReaderID();

        // Verificar se o reader tem empréstimos em atraso
        boolean hasOverdueLending = lendingRepository.existsByReaderIDAndOverdueTrue(readerID);
        if (hasOverdueLending) {
            throw new IllegalArgumentException("Reader has overdue lending and cannot borrow more books.");
        }

        // Verificar se o reader já tem 3 livros emprestados
        long activeLendingsCount = lendingRepository.countActiveLendingsByReaderID(readerID);
        if (activeLendingsCount >= 3) {
            throw new IllegalArgumentException("Reader already has the maximum number of active lendings (3).");
        }

        // Criar o empréstimo com os dados obtidos
        LocalDate startDate = LocalDate.now();
        LocalDate expectedReturnDate = startDate.plusDays(14);

        Lending lending = new Lending(bookID, readerID, startDate, null, expectedReturnDate, false, 0);
        lending.updateOverdueStatus();
        Lending savedLending = lendingRepository.save(lending);

        // Publicar mensagem para sincronizar com outras instâncias
        rabbitMQProducer.sendMessage("lending.sync", savedLending);

        return savedLending;
    }

    @Override
    public Lending partialUpdate(int id1, int id2, EditLendingRequest resource, long desiredVersion) {
        String lendingID = id1 + "/" + id2;
        Lending lending = lendingRepository.findByLendingID(lendingID)
                .orElseThrow(() -> new NotFoundException("Lending not found."));

        // Atualiza os campos modificados
        if (resource.getReturnDate() != null) {
            lending.setReturnDate(resource.getReturnDate());
            lending.updateOverdueStatus();
            lending.setFine(calculateFine(lending));
        }

        lending.setNotes(resource.getNotes());
        Lending updatedLending = lendingRepository.save(lending);

        // Publicar mensagem para sincronizar com outras instâncias
        rabbitMQProducer.sendMessage("lending.sync", updatedLending);

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
