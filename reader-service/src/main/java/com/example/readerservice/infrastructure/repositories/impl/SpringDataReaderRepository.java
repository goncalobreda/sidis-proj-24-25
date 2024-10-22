package com.example.readerservice.infrastructure.repositories.impl;

import com.example.readerservice.model.Reader;
import com.example.readerservice.repositories.ReaderRepository;
import com.example.readerservice.service.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataReaderRepository extends ReaderRepository, ReaderRepoCustom, CrudRepository<Reader, Long> {

    @Override
    @Query("SELECT r FROM Reader r ORDER BY substring(r.readerID, 1, 4), cast(substring(r.readerID, 6, 10) AS int) DESC")
    Optional<Reader> findTopByOrderByReaderIDDesc();

    @Override
    @Query(value = "SELECT r FROM Reader r ORDER BY r.email DESC")
    List<Reader> findTop5Readers();

    @Override
    @Query("SELECT r FROM Reader r WHERE r.readerID LIKE :readerID")
    Optional<Reader> findByReaderID(@Param("readerID") String readerID);

    @Override
    @Query("SELECT r FROM Reader r WHERE r.email LIKE :email")
    Optional<Reader> findByEmail(@Param("email") String email);

    @Override
    @Query("SELECT r FROM Reader r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Reader> findByName(@Param("name") String name);

    @Query("SELECT COUNT(r) FROM Reader r")
    long count();
}
