package com.example.lendingservice.service;

import com.example.lendingservice.exceptions.NotFoundException;
import com.example.lendingservice.model.Lending;
import com.example.lendingservice.repositories.LendingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class LendingServiceImpl implements LendingService {

    @Autowired
    private LendingRepository lendingRepository;

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
        // Corrigido para retornar apenas o ID do leitor
        return lendingRepository.findReaderByLendingID(lendingID);
    }

    @Override
    public Optional<Lending> getLastId() {
        return lendingRepository.findFirstByOrderByLendingIDDesc();
    }

    @Override
    public List<Lending> getOverdueLendingsSortedByTardiness() {
        return lendingRepository.findByOverdueTrueOrderByExpectedReturnDateDesc(); // Usando o método correto
    }

    @Override
    public Lending create(CreateLendingRequest request) {
        // Simular os dados recebidos via HTTP para BookID e ReaderID
        Long bookID = request.getBookID();
        String readerID = request.getReaderID();

        // Criar o objeto Lending sem dependências diretas de Book ou Reader
        LocalDate startDate = LocalDate.now();
        LocalDate expectedReturnDate = startDate.plusDays(14); // Exemplo de prazo de devolução

        Lending lending = new Lending(null, null, startDate, null, expectedReturnDate, false, 0);
        lending.updateOverdueStatus();
        return lendingRepository.save(lending);
    }

    @Override
    public Lending partialUpdate(int id1, int id2, EditLendingRequest resource, long desiredVersion) {
        String lendingID = id1 + "/" + id2;
        Lending lending = lendingRepository.findByLendingID(lendingID)
                .orElseThrow(() -> new NotFoundException("Lending not found."));

        if (resource.getReturnDate() != null) {
            lending.setReturnDate(resource.getReturnDate());
            lending.updateOverdueStatus();
            lending.setFine(calculateFine(lending));
        }

        lending.setNotes(resource.getNotes());
        return lendingRepository.save(lending);
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
            return (int) (daysLate * 5); // Exemplo de multa de 5 por dia de atraso
        }
        return 0;
    }

    @Override
    public Map<String, Double> getAverageLendingPerGenreForMonth(int month, int year) {
        return new HashMap<>(); // Simulação até que a lógica de comunicação HTTP esteja implementada
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
    public Map<String, Map<String, Long>> getLendingsPerMonthByGenreForLastYear() {
        return new HashMap<>(); // Simulação até que a lógica de comunicação HTTP esteja implementada
    }

    @Override
    public long getLendingCountByReaderForMonth(String readerID, int month, int year) {
        return 0; // Simulação até que a lógica de comunicação HTTP esteja implementada
    }

    @Override
    public Map<String, Double> getAverageLendingDurationPerGenre(int month, int year) {
        return new HashMap<>(); // Simulação até que a lógica de comunicação HTTP esteja implementada
    }

    @Override
    public Map<String, Double> getAverageLendingDurationPerBook() {
        return new HashMap<>(); // Simulação até que a lógica de comunicação HTTP esteja implementada
    }
}
