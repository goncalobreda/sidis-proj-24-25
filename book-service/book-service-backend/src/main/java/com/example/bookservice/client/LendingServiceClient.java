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
        String urlInstance1 = "http://localhost:8084/api/lendings";
        String urlInstance2 = "http://localhost:8085/api/lendings";

        List<LendingDTO> allLendings = new ArrayList<>();

        try {
            // Tente buscar da primeira instância
            log.debug("Fetching lending records from lending-instance1-service");
            List<LendingDTO> lendingsFromInstance1 = fetchLendingRecords(urlInstance1);
            allLendings.addAll(lendingsFromInstance1);
        } catch (Exception e) {
            log.warn("Instance 1 unavailable, trying instance 2: " + e.getMessage());

            // Se falhar, tenta buscar da segunda instância
            try {
                log.debug("Fetching lending records from lending-instance2-service");
                List<LendingDTO> lendingsFromInstance2 = fetchLendingRecords(urlInstance2);
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
}
