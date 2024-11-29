/*
package com.example.readerservice;

import com.example.readerservice.client.BookServiceClient;
import com.example.readerservice.client.LendingServiceClient;
import com.example.readerservice.model.Reader;
import com.example.readerservice.repositories.ReaderRepository;
import com.example.readerservice.service.EditReaderMapper;
import com.example.readerservice.service.ReaderServiceImpl;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ReaderServiceUnitTest {

    private ReaderRepository readerRepository;
    private ReaderServiceImpl readerService;
    private EditReaderMapper editReaderMapper;
    private BookServiceClient bookServiceClient;
    private RestTemplate restTemplate;
    private LendingServiceClient lendingServiceClient;

    private PasswordEncoder passwordEncoder;


    @BeforeEach
    void setUp() {
        readerRepository = Mockito.mock(ReaderRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        editReaderMapper = Mockito.mock(EditReaderMapper.class);
        bookServiceClient = Mockito.mock(BookServiceClient.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        lendingServiceClient = Mockito.mock(LendingServiceClient.class);
       // restTemplate = new RestTemplate();


        readerService = new ReaderServiceImpl(readerRepository, editReaderMapper, lendingServiceClient, bookServiceClient, restTemplate);
    }


    @Test
    void testFindByID() {

        String readerID = "2024/52";
        Reader existingReader = new Reader();
        existingReader.setReaderID(readerID);

        when(readerRepository.findByReaderID(readerID)).thenReturn(Optional.of(existingReader));

        Optional<Reader> foundReader = readerService.getReaderByID(readerID);

        assertTrue(foundReader.isPresent() );
        assertEquals(readerID, foundReader.get().getReaderID());

        verify(readerRepository, times(1)).findByReaderID(readerID);
    }

    @Test
    void testFindByEmail() {
        String email = "bacjb@mail.com";
        Reader existingReader = new Reader();
        existingReader.setEmail(email);

        when(readerRepository.findByEmail(email)).thenReturn(Optional.of(existingReader));

        Optional<Reader> foundReader = readerService.getReaderByEmail(email);

        assertTrue(foundReader.isPresent());
        assertEquals(email, foundReader.get().getEmail());

        verify(readerRepository, times(1)).findByEmail(email);
    }

    @Test
    void testFindByName() {
        String name = "João Sidónio";
        List<Reader> existingReader = List.of(new Reader(), new Reader());

        when(readerRepository.findByName(name)).thenReturn(existingReader);

        List<Reader> foundReader = readerService.getReaderByName(name);

        assertTrue(!foundReader.isEmpty());
        assertEquals(existingReader.size(), foundReader.size());

        verify(readerRepository, times(1)).findByName(name);
    }
}
*/