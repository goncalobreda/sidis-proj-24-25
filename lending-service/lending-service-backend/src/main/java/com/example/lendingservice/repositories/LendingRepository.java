package com.example.lendingservice.repositories;

import com.example.lendingservice.model.Lending;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LendingRepository {

    Optional<Lending> findByLendingID(String lendingID);
    void delete(Lending lending);

    @NotNull Iterable<Lending> findAll();

    @NotNull Lending save(@NotNull Lending newLending);

    Optional<String> findReaderByLendingID(String lendingID);

    List<Lending> findByOverdueTrueOrderByTardinessDesc();

    long countActiveLendingsByReaderID(String readerID);

    boolean existsByReaderIDAndOverdueTrue(String readerID);

    List<Lending> findByOverdueTrueOrderByExpectedReturnDateDesc();
    List<Object[]> findLendingsCountByGenreAndMonth(int month, int year);

    Optional<Lending> findFirstByOrderByLendingIDDesc();

    List<Lending> findLendingsWithReturnDate();

    long countLendingsByReaderIDAndMonth(String readerID, int month, int year);

    Optional<Lending> findByBookIDAndReaderIDAndStartDateAndExpectedReturnDate(Long bookID, String readerID, LocalDate startDate, LocalDate expectedReturnDate);

}
