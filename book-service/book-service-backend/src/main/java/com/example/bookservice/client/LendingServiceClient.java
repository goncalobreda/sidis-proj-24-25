package com.example.bookservice.client;

import com.example.bookservice.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class LendingServiceClient {

    private static final Logger log = LoggerFactory.getLogger(LendingServiceClient.class);
    private final RestTemplate restTemplate;

    // URLs das outras instâncias
    @Value("${lending.instance1.url}")
    private String lendingInstance1Url;

    @Value("${lending.instance2.url}")
    private String lendingInstance2Url;

    @Value("${book.instance1.url}")
    private String bookInstance1Url;

    @Value("${book.instance2.url}")
    private String bookInstance2Url;

    @Value("${server.port}")
    private String currentPort;

    @Autowired
    public LendingServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<LendingDTO> getAllLendings() {
        List<LendingDTO> allLendings = new ArrayList<>();

        try {
            // Tente buscar da primeira instância
            log.debug("Fetching lending records from lending-instance1-service");
            List<LendingDTO> lendingsFromInstance1 = fetchLendingRecords(lendingInstance1Url + "/api/lendings");
            allLendings.addAll(lendingsFromInstance1);
        } catch (Exception e) {
            log.warn("Instance 1 unavailable, trying instance 2: " + e.getMessage());

            // Se falhar, tenta buscar da segunda instância
            try {
                log.debug("Fetching lending records from lending-instance2-service");
                List<LendingDTO> lendingsFromInstance2 = fetchLendingRecords(lendingInstance2Url + "/api/lendings");
                allLendings.addAll(lendingsFromInstance2);
            } catch (Exception ex) {
                log.error("Both instances of lending-service are unavailable: " + ex.getMessage());
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Both instances of lending-service are unavailable");
            }
        }

        return allLendings;
    }

    private List<LendingDTO> fetchLendingRecords(String url) {
        ResponseEntity<LendingDTO[]> response = restTemplate.getForEntity(url, LendingDTO[].class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return Arrays.asList(response.getBody());
        } else {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Unable to fetch lending records from " + url);
        }
    }


    public Optional<Book> getBookFromOtherInstance(Long bookID) {
        try {
            log.debug("Pergunta à instância 1 se o book existe " + bookID);
            ResponseEntity<Book> response = restTemplate.getForEntity(bookInstance1Url + "/api/books/id/" + bookID, Book.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return Optional.ofNullable(response.getBody());
            } else {
                log.warn("Book ID " + bookID + " não encontrado.");
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Erro na procura do livro na instância 2: " + e.getMessage());
            return Optional.empty();
        }
    }
}
