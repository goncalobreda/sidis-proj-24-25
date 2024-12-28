package com.example.bookservice.bootstraping;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import com.example.bookservice.model.Author;
import com.example.bookservice.repositories.AuthorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Profile("bootstrap")
@Order(1)
public class AuthorBootstrap implements CommandLineRunner {

    @Qualifier("authorRepository")
    private final AuthorRepository authorRepo;

    @Override
    @Transactional
    public void run(final String... args) throws Exception {
        System.out.println("AuthorBootstrap.run");

        // Verificar o último ID de autor para inicializar o contador, se necessário
        final Optional<Author> lastAuthor = authorRepo.findTopByOrderByAuthorIDDesc();
        lastAuthor.ifPresent(author -> {
            System.out.println("Last authorID is " + author.getAuthorID());
            author.initCounter(author.getAuthorID());
        });

        // Adicionar autores individualmente, com verificação de existência
        addAuthors();
        System.out.println("AuthorBootstrap completed\n");
    }

    private void addAuthors() {
        addAuthorIfNotExists("William Shakespeare", "England, 1564-1616");
        addAuthorIfNotExists("Jane Austen", "England, 1775-1817");
        addAuthorIfNotExists("Fernando Pessoa", "Portugal, 1888-1935");
        addAuthorIfNotExists("Mark Twain", "United States, 1835-1910");
        addAuthorIfNotExists("Leo Tolstoy", "Russia, 1828-1910");
        addAuthorIfNotExists("Charles Dickens", "England, 1812-1870");
        addAuthorIfNotExists("Sophia de Mello Breyner Andresen", "Portugal, 1919-2004");
        addAuthorIfNotExists("Homer", "Ancient Greece, c. 8th century BC");
        addAuthorIfNotExists("Gabriel Garcia Marquez", "Colombia, 1927-2014");
        addAuthorIfNotExists("Franz Kafka", "Austria-Hungary, 1883-1924");
        addAuthorIfNotExists("George Orwell", "India (British Raj), 1903-1950");
        addAuthorIfNotExists("Fyodor Dostoevsky", "Russia, 1821-1881");
        addAuthorIfNotExists("Herman Melville", "United States, 1819-1891");
        addAuthorIfNotExists("José Saramago", "Portugal, 1922-2010");
        addAuthorIfNotExists("Virginia Woolf", "England, 1882-1941");
        addAuthorIfNotExists("James Joyce", "Ireland, 1882-1941");
        addAuthorIfNotExists("Marcel Proust", "France, 1871-1922");
        addAuthorIfNotExists("Ernest Hemingway", "United States, 1899-1961");
        addAuthorIfNotExists("Eça de Queirós", "Portugal, 1845-1900");
        addAuthorIfNotExists("Camilo Castelo Branco", "Portugal, 1825-1890");
        addAuthorIfNotExists("Miguel Torga", "Portugal, 1907-1995");
        addAuthorIfNotExists("Antero de Quental", "Portugal, 1842-1891");
        addAuthorIfNotExists("Almeida Garrett", "Portugal, 1799-1854");
        addAuthorIfNotExists("Florbela Espanca", "Portugal, 1894-1930");
        addAuthorIfNotExists("António Lobo Antunes", "Portugal, 1942-");
        addAuthorIfNotExists("Agustina Bessa-Luís", "Portugal, 1922-2019");
        addAuthorIfNotExists("Luís de Camões", "Portugal, 1524-1580");
        addAuthorIfNotExists("Alexandre Herculano", "Portugal, 1810-1877");
        addAuthorIfNotExists("Vergílio Ferreira", "Portugal, 1916-1996");
    }

    private void addAuthorIfNotExists(final String name, final String biography) {
        if (authorRepo.findByNameAndBiography(name, biography).isEmpty()) {
            Author author = new Author(name, biography);
            authorRepo.save(author);
            System.out.println("Author " + name + " added successfully.");
        } else {
            System.out.println("Author " + name + " already exists in the database.");
        }
    }
}
