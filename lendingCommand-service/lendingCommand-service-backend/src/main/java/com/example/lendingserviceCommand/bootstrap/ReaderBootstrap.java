package com.example.lendingserviceCommand.bootstrap;

import com.example.lendingserviceCommand.model.Reader;
import com.example.lendingserviceCommand.repositories.ReaderRepository;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class ReaderBootstrap implements CommandLineRunner {

    private final ReaderRepository readerRepo;

    public ReaderBootstrap(ReaderRepository readerRepo) {
        this.readerRepo = readerRepo;
    }

    @Override
    @Transactional
    public void run(final String... args) {
        System.out.println("ReaderBootstrapper running in LendingService...");

        // Verificar o último Reader e inicializar o contador, se necessário
        Optional<Reader> lastReader = readerRepo.findTopByOrderByReaderIdDesc();
        if (lastReader.isPresent()) {
            Reader.initCounter(lastReader.get().getReaderId());
            System.out.println("Reader counter initialized with last ID: " + lastReader.get().getReaderId());
        } else {
            Reader.initCounter("2024/0"); // Começa com o ano atual e o contador em zero
            System.out.println("No previous reader found, initializing counter with 2024/0.");
        }

        // Criar leitores iniciais (caso não existam)
        createReaderIfNotExists("Josefino Amigalhaço das Coubes", "josefinoDasCOUBES@email.com", true, LocalDateTime.of(2024, 1, 1, 10, 0));
        createReaderIfNotExists("Maria Silva", "maria.silva@email.com", true, LocalDateTime.of(2024, 2, 5, 14, 30));
        createReaderIfNotExists("Joao Gomes", "joaogomes@mail.com", true, LocalDateTime.of(2024, 3, 10, 9, 45));
        createReaderIfNotExists("Ana Costa", "ana.costa@mail.com", false, LocalDateTime.of(2024, 4, 20, 16, 0));
        createReaderIfNotExists("Pedro Oliveira", "pedro.oliveira@mail.com", true, LocalDateTime.of(2024, 5, 30, 18, 15));
        createReaderIfNotExists("Claudia Ferreira", "claudia.ferreira@mail.com", true, LocalDateTime.of(2024, 6, 12, 10, 20));
        createReaderIfNotExists("Carlos Matos", "carlos.matos@mail.com", true, LocalDateTime.of(2024, 7, 8, 11, 45));
        createReaderIfNotExists("Filipa Carvalho", "filipa.carvalho@mail.com", true, LocalDateTime.of(2024, 8, 15, 14, 50));
        createReaderIfNotExists("Rui Sousa", "rui.sousa@mail.com", true, LocalDateTime.of(2024, 9, 1, 9, 0));
        createReaderIfNotExists("Marta Vieira", "marta.vieira@mail.com", true, LocalDateTime.of(2024, 10, 3, 12, 0));
        createReaderIfNotExists("Paulo Andrade", "paulo.andrade@mail.com", true, LocalDateTime.of(2024, 11, 4, 16, 20));
        createReaderIfNotExists("Teresa Lopes", "teresa.lopes@mail.com", true, LocalDateTime.of(2024, 12, 18, 13, 30));
        createReaderIfNotExists("José Antunes", "jose.antunes@mail.com", true, LocalDateTime.of(2024, 1, 11, 15, 0));
        createReaderIfNotExists("Joana Rocha", "joana.rocha@mail.com", true, LocalDateTime.of(2024, 2, 25, 17, 30));
        createReaderIfNotExists("Vitor Mota", "vitor.mota@mail.com", true, LocalDateTime.of(2024, 3, 16, 11, 10));
        createReaderIfNotExists("Andreia Melo", "andreia.melo@mail.com", true, LocalDateTime.of(2024, 4, 22, 10, 0));
        createReaderIfNotExists("Bruno Leite", "bruno.leite@mail.com", true, LocalDateTime.of(2024, 5, 14, 9, 25));
        createReaderIfNotExists("Helena Martins", "helena.martins@mail.com", true, LocalDateTime.of(2024, 6, 2, 18, 45));
        createReaderIfNotExists("Manuel Silva", "manuel.silva@mail.com", true, LocalDateTime.of(2024, 7, 7, 16, 35));
        createReaderIfNotExists("Inês Pereira", "ines.pereira@mail.com", true, LocalDateTime.of(2024, 8, 29, 17, 0));

        System.out.println("ReaderBootstrapper finished in LendingService.");
    }

    private void createReaderIfNotExists(String name, String email, boolean enabled, LocalDateTime createdAt) {
        if (!readerRepo.existsByEmail(email)) {
            Reader reader = new Reader(name, email, createdAt, enabled);
            readerRepo.save(reader);
            System.out.println("Leitor criado: " + name + " (" + email + ")");
        } else {
            System.out.println("Leitor já existe com o email: " + email);
        }
    }
}
