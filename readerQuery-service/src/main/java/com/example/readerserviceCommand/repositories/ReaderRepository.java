package com.example.readerserviceCommand.repositories;

import com.example.readerserviceCommand.model.Reader;
import com.example.readerserviceCommand.service.Page;
import com.example.readerserviceCommand.service.SearchReadersQuery;
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

    List<Reader> searchReaders(Page page, SearchReadersQuery query);
}
