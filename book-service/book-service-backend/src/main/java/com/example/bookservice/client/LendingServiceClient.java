package com.example.bookservice.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class LendingServiceClient {

    private static final Logger log = LoggerFactory.getLogger(LendingServiceClient.class);
    private final RestTemplate restTemplate;

    @Autowired
    public LendingServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<LendingDTO> getAllLendings() {
        // URLs para ambas as instâncias do serviço de empréstimo
        String urlInstance1 = "http://localhost:8084/api/lendings"; // lending-service instance 1
        String urlInstance2 = "http://localhost:8085/api/lendings"; // lending-service instance 2

        List<LendingDTO> allLendings = new ArrayList<>();

        // Fetch lending records from instance 1
        log.debug("Fetching lending records from lending-instance1-service");
        List<LendingDTO> lendingsFromInstance1 = fetchLendingRecords(urlInstance1);
        allLendings.addAll(lendingsFromInstance1);

        // Fetch lending records from instance 2
        log.debug("Fetching lending records from lending-instance2-service");
        List<LendingDTO> lendingsFromInstance2 = fetchLendingRecords(urlInstance2);
        allLendings.addAll(lendingsFromInstance2);

        return allLendings;
    }

    // Método auxiliar para buscar os empréstimos de uma URL específica
    private List<LendingDTO> fetchLendingRecords(String url) {
        ResponseEntity<LendingDTO[]> response = restTemplate.getForEntity(url, LendingDTO[].class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return Arrays.asList(response.getBody());
        } else {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Unable to fetch lending records from " + url);
        }
    }
}
