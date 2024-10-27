package com.example.readerservice.bootstrap;

import com.example.readerservice.model.Reader;
import com.example.readerservice.repositories.ReaderRepository;
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
    public void run(final String... args) throws Exception {
        System.out.println("ReaderBootstrapper running...");

        // Verificar o último Reader e inicializar o contador, se necessário
        Optional<Reader> lastReader = readerRepo.findTopByOrderByReaderIDDesc();
        if (lastReader.isPresent()) {
            lastReader.get().initCounter(lastReader.get().getReaderID());
            System.out.println("Reader counter initialized with last ID: " + lastReader.get().getReaderID());
        } else {
            System.out.println("No previous reader found, counter initialization skipped.");
        }


        createReaderIfNotExists("Josefino Amigalhaço das Coubes", "Password1", "josefinoDasCOUBES@email.com", "1991-01-01", "916325614", true, LocalDateTime.of(2024, 1, 1, 10, 0));
        createReaderIfNotExists("Maria Silva", "Password2", "maria.silva@email.com", "1985-10-10", "917654321", true, LocalDateTime.of(2024, 2, 5, 14, 30));
        createReaderIfNotExists("Joao Gomes", "Password3", "joaogomes@mail.com", "2000-01-02", "913456789", true, LocalDateTime.of(2024, 3, 10, 9, 45));
        createReaderIfNotExists("Ana Costa", "Password4", "ana.costa@mail.com", "1995-04-15", "918123456", false, LocalDateTime.of(2024, 4, 20, 16, 0));
        createReaderIfNotExists("Pedro Oliveira", "Password5", "pedro.oliveira@mail.com", "1993-07-22", "912345678", true, LocalDateTime.of(2024, 5, 30, 18, 15));
        createReaderIfNotExists("Claudia Ferreira", "Password6", "claudia.ferreira@mail.com", "1997-09-09", "917987654", true, LocalDateTime.of(2024, 6, 12, 10, 20));
        createReaderIfNotExists("Carlos Matos", "Password7", "carlos.matos@mail.com", "1988-05-05", "911223344", true, LocalDateTime.of(2024, 7, 8, 11, 45));
        createReaderIfNotExists("Filipa Carvalho", "Password8", "filipa.carvalho@mail.com", "1996-08-20", "913345566", true, LocalDateTime.of(2024, 8, 15, 14, 50));
        createReaderIfNotExists("Rui Sousa", "Password9", "rui.sousa@mail.com", "1990-03-30", "914556677", true, LocalDateTime.of(2024, 9, 1, 9, 0));
        createReaderIfNotExists("Marta Vieira", "Password10", "marta.vieira@mail.com", "1994-02-14", "915667788", true, LocalDateTime.of(2024, 10, 3, 12, 0));
        createReaderIfNotExists("Paulo Andrade", "Password11", "paulo.andrade@mail.com", "1998-07-12", "913998877", true, LocalDateTime.of(2024, 11, 4, 16, 20));
        createReaderIfNotExists("Teresa Lopes", "Password12", "teresa.lopes@mail.com", "1989-12-25", "919876543", true, LocalDateTime.of(2024, 12, 18, 13, 30));
        createReaderIfNotExists("José Antunes", "Password13", "jose.antunes@mail.com", "1992-09-18", "912112233", true, LocalDateTime.of(2024, 1, 11, 15, 0));
        createReaderIfNotExists("Joana Rocha", "Password14", "joana.rocha@mail.com", "1986-11-03", "917654122", true, LocalDateTime.of(2024, 2, 25, 17, 30));
        createReaderIfNotExists("Vitor Mota", "Password15", "vitor.mota@mail.com", "1991-10-10", "916547843", true, LocalDateTime.of(2024, 3, 16, 11, 10));
        createReaderIfNotExists("Andreia Melo", "Password16", "andreia.melo@mail.com", "1993-06-09", "912304567", true, LocalDateTime.of(2024, 4, 22, 10, 0));
        createReaderIfNotExists("Bruno Leite", "Password17", "bruno.leite@mail.com", "1995-05-22", "916478596", true, LocalDateTime.of(2024, 5, 14, 9, 25));
        createReaderIfNotExists("Helena Martins", "Password18", "helena.martins@mail.com", "1997-03-03", "914123789", true, LocalDateTime.of(2024, 6, 2, 18, 45));
        createReaderIfNotExists("Manuel Silva", "Password19", "manuel.silva@mail.com", "1985-04-15", "912222333", true, LocalDateTime.of(2024, 7, 7, 16, 35));
        createReaderIfNotExists("Inês Pereira", "Password20", "ines.pereira@mail.com", "1989-06-20", "911334455", true, LocalDateTime.of(2024, 8, 29, 17, 0));

        System.out.println("ReaderBootstrapper finished.");
    }

    // Método para verificar se o leitor já existe com base no email e adiciona a data de criação
    private void createReaderIfNotExists(String name, String password, String email, String birthdate, String phoneNumber, boolean GDPR, LocalDateTime createdAt) {
        // Usar o método existsByEmail para verificar a existência
        if (!readerRepo.existsByEmail(email)) {
            Reader reader = new Reader(name, password, email, birthdate, phoneNumber, GDPR);
            reader.setCreatedAt(createdAt);  // Definir o createdAt com base no argumento
            reader.setCreatedBy("system"); // ou outro utilizador apropriado
            readerRepo.save(reader);
            System.out.println("Leitor criado: " + reader.getFullName() + " em " + createdAt);
        } else {
            // Se o leitor já existir, exibe mensagem
            System.out.println("Leitor já existe com o email: " + email);
        }
    }
}
