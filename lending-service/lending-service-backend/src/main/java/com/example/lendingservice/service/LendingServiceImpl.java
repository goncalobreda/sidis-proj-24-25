package com.example.lendingservice.service;

import com.example.lendingservice.dto.BookResponse;
import com.example.lendingservice.dto.ReaderResponse;
import com.example.lendingservice.exceptions.NotFoundException;
import com.example.lendingservice.model.Lending;
import com.example.lendingservice.repositories.LendingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class LendingServiceImpl implements LendingService {

    @Autowired
    private LendingRepository lendingRepository;
    @Autowired
    private RestTemplate restTemplate;

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
        // Fazer o HTTP GET para obter o bookID do book-service (instância 1 na porta 8082)
        String bookServiceUrl = "http://localhost:8082/api/books/id/{id}";
        ResponseEntity<BookResponse> bookResponse = restTemplate.getForEntity(bookServiceUrl, BookResponse.class, request.getBookID());
        Long bookID = bookResponse.getBody().getBookID(); // Obter bookID da resposta

        // Separar o readerID em duas partes (id1 e id2)
        String readerID = request.getReaderID(); // Exemplo: "2024/3"
        String[] readerParts = readerID.split("/"); // Dividir o readerID em duas partes
        String id1 = readerParts[0];
        String id2 = readerParts[1];

        // Fazer o HTTP GET para obter o readerID do reader-service (instância 1 na porta 8086)
        String readerServiceUrl = "http://localhost:8086/api/readers/id/{id1}/{id2}";
        ResponseEntity<ReaderResponse> readerResponse = restTemplate.getForEntity(readerServiceUrl, ReaderResponse.class, id1, id2);
        String readerIDResult = readerResponse.getBody().getReaderID(); // Obter readerID da resposta

        // Criar o empréstimo com os dados obtidos
        LocalDate startDate = LocalDate.now();
        LocalDate expectedReturnDate = startDate.plusDays(14); // Exemplo de prazo de devolução

        Lending lending = new Lending(bookID, readerIDResult, startDate, null, expectedReturnDate, false, 0);
        lending.updateOverdueStatus();
        Lending savedLending = lendingRepository.save(lending); // Guardar na instância 1

        // Sincronizar com a instância 2 via HTTP POST (porta 8085)
        String lendingServiceUrlInstance2 = "http://localhost:8085/api/lendings/sync";
        try {
            restTemplate.postForEntity(lendingServiceUrlInstance2, savedLending, Lending.class);
        } catch (Exception e) {
            System.err.println("Erro ao sincronizar o lending com a instância 2: " + e.getMessage());
        }

        return savedLending; // Retornar o empréstimo criado
    }

    @Override
    public void deleteLendingById(String lendingID) {
        Optional<Lending> lending = lendingRepository.findByLendingID(lendingID);
        if (lending.isPresent()) {
            lendingRepository.delete(lending.get());
        } else {
            throw new NotFoundException("Lending not found with ID: " + lendingID);
        }
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
