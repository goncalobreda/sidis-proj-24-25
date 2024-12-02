package com.example.readerserviceCommand.repositories;

import com.example.readerserviceCommand.model.Reader;
import java.util.Optional;

public interface ReaderRepository {

    Optional<Reader> findByReaderID(String readerID);

    Optional<Reader> findTopByOrderByReaderIDDesc();

    Optional<Reader> findByEmail(String email);

    <S extends Reader> S save(S entity);

    boolean existsByEmail(String email);
}
