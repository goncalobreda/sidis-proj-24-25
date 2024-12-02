package com.example.readerserviceQuery.infrastructure.repositories.impl;

import com.example.readerserviceQuery.model.Reader;
import com.example.readerserviceQuery.model.ReaderCountDTO;
import com.example.readerserviceQuery.repositories.ReaderRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataReaderRepository extends ReaderRepository, ReaderRepoCustom, CrudRepository<Reader, Long> {

    @Override
    @Query(value = "SELECT * FROM Reader ORDER BY readerID DESC LIMIT 1", nativeQuery = true)
    Optional<Reader> findTopByOrderByReaderIDDesc();


    @Override
    @Query("SELECT r FROM Reader r WHERE r.readerID LIKE :readerID")
    Optional<Reader> findByReaderID(@Param("readerID") String readerID);

    @Override
    @Query("SELECT r FROM Reader r WHERE r.email LIKE :email")
    Optional<Reader> findByEmail(@Param("email") String email);

    @Override
    @Query("SELECT r FROM Reader r WHERE LOWER(r.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Reader> findByName(@Param("name") String name);

    @Query("SELECT COUNT(r) FROM Reader r")
    long count();

    @Query("SELECT r FROM Reader r ORDER BY r.createdAt DESC")
    List<ReaderCountDTO> findTop5Readers();
}
