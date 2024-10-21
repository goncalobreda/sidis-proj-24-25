package com.example.lendingservice.bootstrap;

import com.example.lendingservice.model.Lending;
import com.example.lendingservice.repositories.LendingRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Component
@Profile("bootstrap")
public class LendingBootstrap implements CommandLineRunner {

    private final LendingRepository lendingRepo;

    public LendingBootstrap(@Qualifier("springDataLendingRepository") LendingRepository lendingRepo) {
        this.lendingRepo = lendingRepo;
    }

    @Override
    @Transactional
    public void run(final String... args) throws Exception {
        System.out.println("LendingBootstrapper running...");
        System.out.println("LendingBootstrapper running with profile: " + System.getProperty("spring.profiles.active"));

        // Verificar o último empréstimo e inicializar o contador se necessário
        Optional<Lending> lastLending = lendingRepo.findFirstByOrderByLendingIDDesc();
        if (lastLending.isPresent()) {
            lastLending.get().initCounter(lastLending.get().getLendingID());
            System.out.println("Lending counter initialized with last ID: " + lastLending.get().getLendingID());
        } else {
            System.out.println("No previous lending found, counter initialization skipped.");
        }

        // Criar alguns exemplos de empréstimos devolvidos e ativos
        createLendingIfNotExists(1L, "2024/1", "2024-02-01", "2024-02-15", "2024-02-16", true, 10);
        createLendingIfNotExists(2L, "2024/2", "2024-03-01", "2024-03-10", "2024-03-16", false, 0);
        createActiveLendingIfNotExists(1L, "2024/3", "2024-04-01", "2024-04-16");

        System.out.println("LendingBootstrapper finished.");
    }

    private void createLendingIfNotExists(Long bookID, String readerID, String startDate, String returnDate, String expectedReturnDate, boolean isOverdue, int fine) {
        if (!lendingExists(bookID, readerID, LocalDate.parse(startDate), LocalDate.parse(expectedReturnDate))) {
            Lending lending = new Lending(bookID, readerID, LocalDate.parse(startDate), LocalDate.parse(returnDate), LocalDate.parse(expectedReturnDate), isOverdue, fine);
            lendingRepo.save(lending);
            System.out.println("Lending created for Book ID: " + bookID + " and Reader ID: " + readerID);
        } else {
            System.out.println("Lending already exists for Book ID: " + bookID + " and Reader ID: " + readerID);
        }
    }

    private void createActiveLendingIfNotExists(Long bookID, String readerID, String startDate, String expectedReturnDate) {
        if (!lendingExists(bookID, readerID, LocalDate.parse(startDate), LocalDate.parse(expectedReturnDate))) {
            Lending lending = new Lending(bookID, readerID, LocalDate.parse(startDate), null, LocalDate.parse(expectedReturnDate), false, 0);
            lendingRepo.save(lending);
            System.out.println("Active Lending created for Book ID: " + bookID + " and Reader ID: " + readerID);
        } else {
            System.out.println("Active Lending already exists for Book ID: " + bookID + " and Reader ID: " + readerID);
        }
    }

    private boolean lendingExists(Long bookID, String readerID, LocalDate startDate, LocalDate expectedReturnDate) {
        boolean exists = lendingRepo.findByBookIDAndReaderIDAndStartDateAndExpectedReturnDate(bookID, readerID, startDate, expectedReturnDate).isPresent();
        System.out.println("Lending exists: " + exists + " for Book ID: " + bookID + " and Reader ID: " + readerID);
        return exists;
    }

}
