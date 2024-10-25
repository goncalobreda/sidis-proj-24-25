package com.example.authservice.usermanagement.services;

import com.example.authservice.dto.CreateReaderRequestDTO;
import com.example.authservice.dto.ReaderViewDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ExternalServiceHelper {

    private final RestTemplate restTemplate;

    @Value("${reader.instance1.url}")
    private String readerInstance1Url;

    @Value("${reader.instance2.url}")
    private String readerInstance2Url;

    public ExternalServiceHelper(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Método para registrar um Reader no ReaderService
    public ResponseEntity<?> registerReaderInService(CreateReaderRequestDTO request) {
        String readerServiceUrlInstance1 = readerInstance1Url + "/api/readers";
        String readerServiceUrlInstance2 = readerInstance2Url + "/api/readers";

        try {
            ResponseEntity<ReaderViewDTO> response = restTemplate.postForEntity(readerServiceUrlInstance1, request, ReaderViewDTO.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            System.err.println("Instância 1 do reader-service indisponível, tentando instância 2: " + e.getMessage());
            try {
                ResponseEntity<ReaderViewDTO> response = restTemplate.postForEntity(readerServiceUrlInstance2, request, ReaderViewDTO.class);
                return ResponseEntity.ok(response.getBody());
            } catch (Exception ex) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Ambas as instâncias do reader-service estão indisponíveis");
            }
        }
    }
}
