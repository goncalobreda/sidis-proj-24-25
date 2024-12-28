package com.example.bookservice.infraestructure.repositories.impl;

import com.example.bookservice.model.Lending;
import com.example.bookservice.repositories.LendingRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface SpringDataLendingRepository extends CrudRepository<Lending, Long>, LendingRepository {

    @Override
    @Query("SELECT l FROM Lending l WHERE l.bookID = :bookID AND l.readerID = :readerID AND l.startDate = :startDate AND l.expectedReturnDate = :expectedReturnDate")
    Optional<Lending> findByBookIDAndReaderIDAndStartDateAndExpectedReturnDate(
            @Param("bookID") Long bookID,
            @Param("readerID") String readerID,
            @Param("startDate") LocalDate startDate,
            @Param("expectedReturnDate") LocalDate expectedReturnDate
    );

    @Override
    @Query("SELECT l FROM Lending l ORDER BY l.lendingID DESC LIMIT 1")
    Optional<Lending> findFirstByOrderByLendingIDDesc();

    @Override
    @Query("SELECT l FROM Lending l")
    Iterable<Lending> findAll();
}
