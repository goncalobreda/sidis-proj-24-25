package com.example.lendingserviceQuery.repositories;

import com.example.lendingserviceQuery.model.Reader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReaderRepository extends JpaRepository<Reader, String> {

    Optional<Reader> findTopByOrderByReaderIdDesc();




    boolean existsByEmail(String email);

    boolean existsByReaderId(String readerId);

    Reader findByReaderId(String readerId);

    Optional<Reader> findByEmail(String email);
}

