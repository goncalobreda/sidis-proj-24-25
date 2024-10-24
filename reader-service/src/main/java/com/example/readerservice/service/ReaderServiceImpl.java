package com.example.readerservice.service;

import com.example.readerservice.client.*;
import com.example.readerservice.exceptions.ConflictException;
import com.example.readerservice.exceptions.NotFoundException;
import com.example.readerservice.model.Reader;
import com.example.readerservice.model.ReaderCountDTO;
import com.example.readerservice.repositories.ReaderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReaderServiceImpl implements ReaderService {

    private final ReaderRepository readerRepository;
    private final EditReaderMapper editReaderMapper;
    private final LendingServiceClient lendingServiceClient;
    private final BookServiceClient bookServiceClient;


    public ReaderServiceImpl(ReaderRepository readerRepository, EditReaderMapper editReaderMapper, LendingServiceClient lendingServiceClient, BookServiceClient bookServiceClient) {
        this.readerRepository = readerRepository;
        this.editReaderMapper = editReaderMapper;
        this.lendingServiceClient = lendingServiceClient;
        this.bookServiceClient = bookServiceClient;
    }

    @Override
    public List<Reader> findAll() {
        return readerRepository.findAll();
    }

    @Override
    public Reader create(CreateReaderRequest request) {
        if (readerRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ConflictException("Email already exists! Cannot create a new reader.");
        }

        validateBirthdate(request.getBirthdate());

        final Reader reader = editReaderMapper.create(request);
        reader.setUniqueReaderID();

        readerRepository.save(reader);

        return reader;
    }

    public Reader partialUpdate(final String readerID, final EditReaderRequest request, final long desiredVersion) {
        final var reader = readerRepository.findByReaderID(readerID)
                .orElseThrow(() -> new NotFoundException("Cannot update an object that does not yet exist"));

        if (request.getBirthdate() != null) {
            validateBirthdate(request.getBirthdate());
        }

        reader.applyPatch(desiredVersion, request.getName(), null, request.getEmail(), request.getBirthdate(),
                request.getPhoneNumber(), request.isGDPR(), request.getInterests());

        readerRepository.save(reader);

        return reader;
    }



    @Override
    public Optional<Reader> getReaderByID(final String readerID) {
        return readerRepository.findByReaderID(readerID);
    }

    @Override
    public Optional<Reader> getReaderByEmail(final String email) {
        return readerRepository.findByEmail(email);
    }

    @Override
    public List<Reader> getReaderByName(final String name) {
        return readerRepository.findByName(name);
    }

    public List<Reader> searchReaders(Page page, SearchReadersQuery query) {
        if (page == null) {
            page = new Page(1, 10);
        }
        if (query == null) {
            query = new SearchReadersQuery("", "", "");
        }
        return readerRepository.searchReaders(page, query);
    }

    private void validateBirthdate(final String birthdate) {
        if (birthdate == null) throw new IllegalArgumentException("Birthdate cannot be null");
        if (!birthdate.isBlank()) {
            String[] parts = birthdate.split("-");
            if (parts.length != 3) throw new IllegalArgumentException("Birthdate must be in the format YYYY-MM-DD");

            try {
                int birthdateDay = Integer.parseInt(parts[2]);
                int birthdateMonth = Integer.parseInt(parts[1]);
                int birthdateYear = Integer.parseInt(parts[0]);

                if (birthdateYear <= 0) throw new IllegalArgumentException("Year must be positive");
                if (birthdateMonth < 1 || birthdateMonth > 12) throw new IllegalArgumentException("Month must be between 1 and 12");
                if (birthdateDay < 1 || birthdateDay > 31) throw new IllegalArgumentException("Day must be between 1 and 31 for the given month");

                if ((birthdateMonth == 4 || birthdateMonth == 6 || birthdateMonth == 9 || birthdateMonth == 11) && birthdateDay > 30) {
                    throw new IllegalArgumentException("Day must be between 1 and 30 for the given month");
                }

                if (birthdateMonth == 2) {
                    boolean isLeapYear = (birthdateYear % 4 == 0 && birthdateYear % 100 != 0) || (birthdateYear % 400 == 0);
                    int maxDayInFebruary = isLeapYear ? 29 : 28;
                    if (birthdateDay > maxDayInFebruary) {
                        throw new IllegalArgumentException("Day must be between 1 and " + maxDayInFebruary + " for February");
                    }
                }

                if (LocalDate.of(birthdateYear, birthdateMonth, birthdateDay).isAfter(LocalDate.now().minusYears(12))) {
                    throw new IllegalArgumentException("Minimum age is 12");
                }

            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Birthdate must contain valid integers for day, month, and year", e);
            }
        }
    }

    public List<ReaderCountDTO> findTop5Readers() {
        // Obter todos os lendings
        List<LendingDTO> lendings = lendingServiceClient.getAllLendings();

        // Contar quantos empréstimos cada reader fez
        Map<String, Long> readerIdCounts = lendings.stream()
                .collect(Collectors.groupingBy(LendingDTO::getReaderID, Collectors.counting()));

        // Obter os 5 readers com mais empréstimos
        List<Map.Entry<String, Long>> top5Readers = readerIdCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());


        return top5Readers.stream()
                .map(entry -> new ReaderCountDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }


    public List<GenreDTO> getBookSuggestions(Reader reader) {
        Set<String> interests = getInterestsByReader(reader);
        List<GenreDTO> suggestions = new ArrayList<>();

        for (String interest : interests) {
            suggestions.addAll(bookServiceClient.getBooksByGenre(interest));
        }

        return suggestions;
    }

    public Set<String> getInterestsByReader(Reader reader) {
        return reader.getInterests();

    }



}
