package com.example.readerserviceQuery.repositories;

import com.example.readerserviceQuery.model.Reader;
import com.example.readerserviceQuery.model.ReaderCountDTO;
import com.example.readerserviceQuery.service.Page;
import com.example.readerserviceQuery.service.SearchReadersQuery;

import java.util.List;
import java.util.Optional;

public interface ReaderRepository {

    Optional<Reader> findTopByOrderByReaderIDDesc();

    boolean existsByEmail(String email);

    Optional<Reader> findByReaderID(String readerID);

    Optional<Reader> findByEmail(String email);

    List<Reader> findByName(String fullName);

    List<Reader> findAll();

    <S extends Reader> S save(S entity);

    List<ReaderCountDTO> findTop5Readers();

    List<Reader> searchReaders(Page page, SearchReadersQuery query);
}
