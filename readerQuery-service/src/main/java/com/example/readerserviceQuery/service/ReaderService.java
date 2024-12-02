package com.example.readerserviceQuery.service;

import com.example.readerserviceQuery.model.Reader;
import com.example.readerserviceQuery.model.ReaderCountDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public interface ReaderService {

    List<Reader> findAll();

    //Reader create(CreateReaderRequest request);

    //Reader partialUpdate(String readerID, EditReaderRequest request, long parseLong);

    Set<String> getInterestsByReader(Reader reader);

    List<ReaderCountDTO> findTop5Readers();

    Optional<Reader> getReaderByID(String readerID);

    Optional<Reader> getReaderByEmail(String email);

    List<Reader> getReaderByName(final String name);

    List<Reader> searchReaders(Page page, SearchReadersQuery query);
}
