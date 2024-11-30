package com.example.lendingservice.service;

import com.example.lendingservice.dto.BookResponse;
import com.example.lendingservice.dto.ReaderResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ExternalServiceHelper {

    private final RestTemplate restTemplate;

    @Value("${book.instance1.url}")
    private String bookInstance1Url;

    @Value("${book.instance2.url}")
    private String bookInstance2Url;

    @Value("${reader.instance1.url}")
    private String readerInstance1Url;

    @Value("${reader.instance2.url}")
    private String readerInstance2Url;

    public ExternalServiceHelper(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Método para tentar obter o bookID (instância 1 ou fallback para instância 2)
    public Long getBookIDFromService(Long bookID) {
        String bookServiceUrlInstance1 = bookInstance1Url + "/api/books/id/{id}";
        String bookServiceUrlInstance2 = bookInstance2Url + "/api/books/id/{id}";
        try {
            ResponseEntity<BookResponse> bookResponse = restTemplate.getForEntity(bookServiceUrlInstance1, BookResponse.class, bookID);
            return bookResponse.getBody().getBookID(); // Obter bookID da resposta
        } catch (Exception e) {
            System.err.println("Instância 1 do book-service indisponível, tentando instância 2: " + e.getMessage());
            try {
                ResponseEntity<BookResponse> bookResponse = restTemplate.getForEntity(bookServiceUrlInstance2, BookResponse.class, bookID);
                return bookResponse.getBody().getBookID();
            } catch (Exception ex) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Ambas as instâncias do book-service estão indisponíveis");
            }
        }
    }

    // Método para tentar obter o readerID (instância 1 ou fallback para instância 2)
    public String getReaderIDFromService(String id1, String id2) {
        String readerServiceUrlInstance1 = readerInstance1Url + "/api/readers/id/{id1}/{id2}";
        String readerServiceUrlInstance2 = readerInstance2Url + "/api/readers/id/{id1}/{id2}";
        try {
            ResponseEntity<ReaderResponse> readerResponse = restTemplate.getForEntity(readerServiceUrlInstance1, ReaderResponse.class, id1, id2);
            return readerResponse.getBody().getReaderID(); // Obter readerID da resposta
        } catch (Exception e) {
            System.err.println("Instância 1 do reader-service indisponível, tentando instância 2: " + e.getMessage());
            try {
                ResponseEntity<ReaderResponse> readerResponse = restTemplate.getForEntity(readerServiceUrlInstance2, ReaderResponse.class, id1, id2);
                return readerResponse.getBody().getReaderID();
            } catch (Exception ex) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Ambas as instâncias do reader-service estão indisponíveis");
            }
        }
    }

    // Método para obter o género de um livro (instância 1 ou fallback para instância 2)
    public String getBookGenreFromService(Long bookID) {
        String bookServiceUrlInstance1 = bookInstance1Url + "/api/books/id/{id}";
        String bookServiceUrlInstance2 = bookInstance2Url + "/api/books/id/{id}";
        try {
            ResponseEntity<BookResponse> bookResponse = restTemplate.getForEntity(bookServiceUrlInstance1, BookResponse.class, bookID);
            return bookResponse.getBody().getGenre(); // Obter género da resposta
        } catch (Exception e) {
            System.err.println("Instância 1 do book-service indisponível, tentando instância 2: " + e.getMessage());
            try {
                ResponseEntity<BookResponse> bookResponse = restTemplate.getForEntity(bookServiceUrlInstance2, BookResponse.class, bookID);
                return bookResponse.getBody().getGenre();
            } catch (Exception ex) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Ambas as instâncias do book-service estão indisponíveis");
            }
        }
    }

}
