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

        // Criar alguns leitores de exemplo
        createReaderIfNotExists("Josefino Amigalhaço das Coubes", "Password1", "josefinoDasCOUBES@email.com", "1991-01-01", "916325614", true);
        createReaderIfNotExists("Maria Silva", "Password2", "maria.silva@email.com", "1985-10-10", "917654321", true);
        createReaderIfNotExists("Joao Gomes", "Password3", "joaogomes@mail.com", "2000-01-02", "913456789", true);

        System.out.println("ReaderBootstrapper finished.");
    }

    // Método para verificar se o leitor já existe com base no email
    private void createReaderIfNotExists(String name, String password, String email, String birthdate, String phoneNumber, boolean GDPR) {
        // Usar o método existsByEmail para verificar a existência
        if (!readerRepo.existsByEmail(email)) {
            Reader reader = new Reader(name, password, email, birthdate, phoneNumber, GDPR);
            reader.setCreatedAt(LocalDateTime.now());  // Definir o createdAt manualmente
            reader.setCreatedBy("system"); // ou outro utilizador apropriado
            readerRepo.save(reader);
            System.out.println("Leitor criado: " + reader.getName());
        } else {
            // Se o leitor já existir, exibe mensagem
            System.out.println("Leitor já existe com o email: " + email);
        }
    }
}
