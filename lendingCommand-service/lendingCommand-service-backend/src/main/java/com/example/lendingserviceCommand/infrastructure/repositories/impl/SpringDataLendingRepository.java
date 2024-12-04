package com.example.lendingserviceCommand.infrastructure.repositories.impl;

import com.example.lendingserviceCommand.model.Lending;
import com.example.lendingserviceCommand.repositories.LendingRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataLendingRepository extends CrudRepository<Lending, Long>, LendingRepository {

    @Override
    @Query("SELECT l FROM Lending l WHERE l.lendingID = :lendingID")
    Optional<Lending> findByLendingID(@Param("lendingID") String lendingID);

    @Override
    @Query("SELECT l FROM Lending l")
    Iterable<Lending> findAll();

    @Override
    @Query("SELECT l.readerID FROM Lending l WHERE l.lendingID = :lendingID")
    Optional<String> findReaderByLendingID(@Param("lendingID") String lendingID);

    @Override
    @Query(value = "SELECT * FROM Lending ORDER BY lendingID DESC LIMIT 1", nativeQuery = true)
    Optional<Lending> findFirstByOrderByLendingIDDesc();

    @Override
    @Query("SELECT COUNT(l) FROM Lending l WHERE l.readerID = :readerID AND l.returnDate IS NULL")
    long countActiveLendingsByReaderID(@Param("readerID") String readerID);

    @Override
    @Query("SELECT COUNT(l) > 0 FROM Lending l WHERE l.readerID = :readerID AND l.overdue = true AND l.returnDate IS NULL")
    boolean existsByReaderIDAndOverdueTrue(@Param("readerID") String readerID);

    @Override
    @Query("SELECT l FROM Lending l WHERE l.overdue = true AND l.returnDate IS NULL ORDER BY FUNCTION('timestampdiff', DAY, l.expectedReturnDate, CURRENT_DATE) DESC")
    List<Lending> findByOverdueTrueOrderByTardinessDesc();

    @Query("SELECT l.bookID, COUNT(l), TIMESTAMPDIFF(DAY, l.expectedReturnDate, CURRENT_DATE) FROM Lending l WHERE MONTH(l.startDate) = :month AND YEAR(l.startDate) = :year GROUP BY l.bookID")
    List<Object[]> findLendingsDurationByBookAndMonth(@Param("month") int month, @Param("year") int year);



    @Override
    @Query("SELECT l FROM Lending l WHERE l.returnDate IS NOT NULL")
    List<Lending> findLendingsWithReturnDate();


    @Override
    @Query("SELECT COUNT(l) FROM Lending l WHERE l.readerID = :readerID AND MONTH(l.startDate) = :month AND YEAR(l.startDate) = :year")
    long countLendingsByReaderIDAndMonth(@Param("readerID") String readerID, @Param("month") int month, @Param("year") int year);

    @Override
    @Query("SELECT l FROM Lending l WHERE l.bookID = :bookID AND l.readerID = :readerID AND l.startDate = :startDate AND l.expectedReturnDate = :expectedReturnDate")
    Optional<Lending> findByBookIDAndReaderIDAndStartDateAndExpectedReturnDate(
            @Param("bookID") Long bookID,
            @Param("readerID") String readerID,
            @Param("startDate") LocalDate startDate,
            @Param("expectedReturnDate") LocalDate expectedReturnDate
    );
}
