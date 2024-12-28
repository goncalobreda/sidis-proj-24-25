package com.example.acquisitionserviceCommand.bootstrap;

import com.example.acquisitionserviceCommand.model.Acquisition;
import com.example.acquisitionserviceCommand.repositories.AcquisitionRepository;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class AcquisitionBootstrap implements CommandLineRunner {

    private final AcquisitionRepository acquisitionRepository;

    public AcquisitionBootstrap(AcquisitionRepository acquisitionRepository) {
        this.acquisitionRepository = acquisitionRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("AcquisitionBootstrap running...");

        // Inicializar o contador com base na última aquisição
        Optional<Acquisition> lastAcquisition = acquisitionRepository.findTopByOrderByAcquisitionIDDesc();
        if (lastAcquisition.isPresent()) {
            lastAcquisition.get().initCounter(lastAcquisition.get().getAcquisitionID());
            System.out.println("Acquisition counter initialized with last ID: " + lastAcquisition.get().getAcquisitionID());
        } else {
            System.out.println("No previous acquisition found, counter initialization skipped.");
        }

        // Criar exemplos de aquisições se não existirem
        createAcquisitionIfNotExists("2024/1", "1234567891", "Aventura nas Estrelas", "Uma jornada intergaláctica cheia de aventuras.",
                "Muito interessante!", List.of("2024/1", "2024/2"), "Aventura");
        createAcquisitionIfNotExists("2024/2", "1234567892", "Romance Proibido", "Um amor impossível em tempos difíceis.",
                "História envolvente!", List.of("2024/3", "2024/4"), "Romance");

        System.out.println("AcquisitionBootstrap finished.");
    }

    private void createAcquisitionIfNotExists(String readerID, String isbn, String title, String description,
                                              String reason, List<String> authorIds, String genre) {
        if (!acquisitionRepository.existsByIsbn(isbn)) {
            Acquisition acquisition = new Acquisition(readerID, isbn, title, description, reason, authorIds, genre);
            acquisitionRepository.save(acquisition);
            System.out.println("Acquisition created: " + acquisition.getTitle());
        } else {
            System.out.println("Acquisition already exists with ISBN: " + isbn);
        }
    }
}
