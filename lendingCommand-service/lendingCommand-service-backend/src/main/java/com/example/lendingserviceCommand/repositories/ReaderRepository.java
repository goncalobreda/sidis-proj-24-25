package com.example.lendingserviceCommand.repositories;

import com.example.lendingserviceCommand.model.Reader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReaderRepository extends JpaRepository<Reader, String> {

    Optional<Reader> findTopByOrderByReaderIdDesc();




    boolean existsByEmail(String email);

    boolean existsByReaderId(String readerId);

    Reader findByReaderId(String readerId);

    Optional<Reader> findByEmail(String email);
}

