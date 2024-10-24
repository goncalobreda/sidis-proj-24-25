package com.example.readerservice.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class BookServiceClient {

    private static final Logger log = LoggerFactory.getLogger(BookServiceClient.class);
    private final RestTemplate restTemplate;

    @Value("${book.instance1.url}")
    private String bookInstance1Url;

    @Value("${book.instance2.url}")
    private String bookInstance2Url;

    public BookServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<GenreDTO> getBooksByGenre(String genre) {
        // Verifique se os dados estão sendo retornados corretamente
        List<GenreDTO> books = fetchBooksByGenre(bookInstance1Url + "/api/books/genre/" + genre);
        // Veja aqui se a lista 'books' contém os dados corretos
        if (books.isEmpty()) {
            log.warn("Nenhum livro encontrado para o gênero: " + genre);
        } else {
            log.debug("Livros retornados: " + books);
        }
        return books;
    }



    private List<GenreDTO> fetchBooksByGenre(String url) {
        ResponseEntity<GenreDTO[]> response = restTemplate.getForEntity(url, GenreDTO[].class);

        if (response.getStatusCode() == HttpStatus.OK) {
            List<GenreDTO> books = Arrays.asList(response.getBody());

            // Log para garantir que os livros foram mapeados corretamente
            log.debug("Books received from API: " + books);

            return books;
        } else {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Unable to fetch books from " + url);
        }
    }

}
