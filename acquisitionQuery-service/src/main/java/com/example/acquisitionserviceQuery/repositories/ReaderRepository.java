package com.example.acquisitionserviceQuery.repositories;

import com.example.acquisitionserviceQuery.model.Reader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReaderRepository extends JpaRepository<Reader, String> {

    Optional<Reader> findTopByOrderByReaderIdDesc();

    boolean existsByEmail(String email);

    boolean existsByReaderId(String readerID);

    Reader findByReaderId(String readerID);

    Optional<Reader> findByEmail(String email);
}

