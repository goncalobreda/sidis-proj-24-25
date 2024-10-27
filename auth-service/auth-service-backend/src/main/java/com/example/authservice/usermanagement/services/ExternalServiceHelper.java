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

    // Método para registrar um Reader em uma das instâncias do ReaderService
    public ResponseEntity<ReaderViewDTO> registerReaderInService(CreateReaderRequestDTO request) {
        String readerServiceUrlInstance1 = readerInstance1Url + "/api/readers/internal/register";
        String readerServiceUrlInstance2 = readerInstance2Url + "/api/readers/internal/register";

        System.out.println("Tentando registrar Reader com os dados: " + request);


        // Tenta enviar a requisição para a primeira instância
        try {
            return restTemplate.postForEntity(readerServiceUrlInstance1, request, ReaderViewDTO.class);
        } catch (Exception e) {
            System.err.println("Instância 1 indisponível, tentando instância 2: " + e.getMessage());
        }

        // Tenta enviar a requisição para a segunda instância
        try {
            return restTemplate.postForEntity(readerServiceUrlInstance2, request, ReaderViewDTO.class);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Ambas as instâncias estão indisponíveis.");
        }
    }
}
