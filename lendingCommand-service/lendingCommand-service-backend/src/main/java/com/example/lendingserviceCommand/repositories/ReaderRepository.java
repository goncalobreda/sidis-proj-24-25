package com.example.lendingserviceCommand.repositories;

import com.example.lendingserviceCommand.model.Reader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReaderRepository extends JpaRepository<Reader, Long> {

    // "readerId" não é a PK no JPA, mas podes criar métodos custom:
    boolean existsByReaderId(String readerId);
    Optional<Reader> findByReaderId(String readerId);

    boolean existsByEmail(String email);
    Optional<Reader> findByEmail(String email);

    Optional<Reader> findTopByOrderByReaderIdDesc();
}


