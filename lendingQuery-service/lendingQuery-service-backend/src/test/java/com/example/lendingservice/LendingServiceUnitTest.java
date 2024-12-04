/*package com.example.lendingservice;

import com.example.lendingserviceQuery.config.LendingConfiguration;
import com.example.lendingserviceQuery.exceptions.NotFoundException;
import com.example.lendingserviceQuery.model.Lending;
import com.example.lendingserviceQuery.repositories.LendingRepository;
import com.example.lendingserviceQuery.service.CreateLendingRequest;
import com.example.lendingserviceQuery.service.LendingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LendingServiceUnitTest {

    private LendingRepository lendingRepository;
    private LendingConfiguration lendingConfig;
    private LendingServiceImpl lendingService;
    private ExternalServiceHelper externalServiceHelper;
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        lendingRepository = Mockito.mock(LendingRepository.class);
        lendingConfig = Mockito.mock(LendingConfiguration.class);
        externalServiceHelper = Mockito.mock(ExternalServiceHelper.class);
        restTemplate = Mockito.mock(RestTemplate.class);

        lendingService = new LendingServiceImpl(lendingRepository, externalServiceHelper, restTemplate);
        ReflectionTestUtils.setField(lendingService, "currentPort", "8084");

    }

    @Test
    void findAll_shouldReturnAllLendings() {
        List<Lending> lendings = Arrays.asList(new Lending(), new Lending());
        when(lendingRepository.findAll()).thenReturn(lendings);

        Iterable<Lending> result = lendingService.findAll();

        assertNotNull(result);
        assertEquals(lendings, result);
        verify(lendingRepository, times(1)).findAll();
    }

    @Test
    void findById_shouldReturnLending_whenLendingExists() {
        Lending lending = new Lending();
        when(lendingRepository.findByLendingID("2024/1")).thenReturn(Optional.of(lending));

        Optional<Lending> result = lendingService.findById(2024, 1);

        assertTrue(result.isPresent());
        assertEquals(lending, result.get());
        verify(lendingRepository, times(1)).findByLendingID("2024/1");
    }

    @Test
    void findById_shouldReturnEmpty_whenLendingDoesNotExist() {
        when(lendingRepository.findByLendingID("2024/1")).thenReturn(Optional.empty());

        Optional<Lending> result = lendingService.findById(2024, 1);

        assertFalse(result.isPresent());
        verify(lendingRepository, times(1)).findByLendingID("2024/1");
    }

    @Test
    void create_shouldCreateNewLending() {
        CreateLendingRequest createRequest = new CreateLendingRequest();
        createRequest.setBookID(1L);
        createRequest.setReaderID("2024/1");

        when(externalServiceHelper.getBookIDFromService(1L)).thenReturn(1L);
        when(externalServiceHelper.getReaderIDFromService("2024", "1")).thenReturn("2024/1");
        when(lendingConfig.getMaxDaysWithoutFine()).thenReturn(10);

        Lending newLending = new Lending();
        newLending.setBookID(1L);
        newLending.setReaderID("2024/1");
        when(lendingRepository.save(any(Lending.class))).thenReturn(newLending);

        Lending createdLending = lendingService.create(createRequest);

        assertNotNull(createdLending);
        assertEquals(1L, createdLending.getBookID());
        assertEquals("2024/1", createdLending.getReaderID());
        verify(lendingRepository, times(1)).save(any(Lending.class));
    }

    @Test
    void create_shouldThrowException_whenBookNotFound() {
        CreateLendingRequest createRequest = new CreateLendingRequest();
        createRequest.setBookID(1L);
        createRequest.setReaderID("2024/1");

        when(externalServiceHelper.getBookIDFromService(1L)).thenThrow(new NotFoundException("Book not found"));

        assertThrows(NotFoundException.class, () -> lendingService.create(createRequest));
    }

    @Test
    void create_shouldThrowException_whenReaderHasOverdueLending() {
        CreateLendingRequest createRequest = new CreateLendingRequest();
        createRequest.setBookID(1L);
        createRequest.setReaderID("2024/1");

        when(externalServiceHelper.getBookIDFromService(1L)).thenReturn(1L);
        when(externalServiceHelper.getReaderIDFromService("2024", "1")).thenReturn("2024/1");
        when(lendingRepository.existsByReaderIDAndOverdueTrue("2024/1")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> lendingService.create(createRequest));
    }

    @Test
    void calculateFine_shouldReturnCorrectFine_whenLendingIsLate() {
        when(lendingConfig.getFinePerDay()).thenReturn(5);

        Lending lending = new Lending();
        lending.setExpectedReturnDate(LocalDate.of(2024, 5, 12));
        lending.setReturnDate(LocalDate.of(2024, 5, 15)); // 3 dias atrasado

        when(lendingRepository.findByLendingID("2024/1")).thenReturn(Optional.of(lending));

        int fine = lendingService.calculateFine("2024/1");

        assertEquals(15, fine); // 3 dias atrasado * 5 euros por dia

        verify(lendingRepository, times(1)).findByLendingID("2024/1");
    }

    @Test
    void calculateFine_shouldReturnZeroFine_whenLendingIsNotLate() {
        when(lendingConfig.getFinePerDay()).thenReturn(5);

        Lending lending = new Lending();
        lending.setExpectedReturnDate(LocalDate.of(2024, 5, 12));
        lending.setReturnDate(LocalDate.of(2024, 5, 10)); // Dentro do prazo

        when(lendingRepository.findByLendingID("2024/1")).thenReturn(Optional.of(lending));

        int fine = lendingService.calculateFine("2024/1");

        assertEquals(0, fine);

        verify(lendingRepository, times(1)).findByLendingID("2024/1");
    }
}

 */
