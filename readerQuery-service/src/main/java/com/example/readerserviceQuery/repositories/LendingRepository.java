package com.example.readerserviceQuery.repositories;

import com.example.readerserviceQuery.model.Lending;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface LendingRepository {

    Optional<Lending> findByBookIDAndReaderIDAndStartDateAndExpectedReturnDate(Long bookID, String readerID, LocalDate startDate, LocalDate expectedReturnDate);

    Optional<Lending> findFirstByOrderByLendingIDDesc();

    Iterable<Lending> findAll();

    Lending save(Lending lending);

    Optional<Lending> findByLendingID(String lendingID);
}
