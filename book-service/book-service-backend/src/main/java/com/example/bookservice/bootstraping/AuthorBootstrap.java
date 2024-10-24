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
//@Profile("bootstrap")
@Order(1)
public class AuthorBootstrap implements CommandLineRunner {

    @Qualifier("authorRepository")
    private final AuthorRepository authorRepo;

    @Override
    @Transactional
    public void run(final String... args) throws Exception {
        System.out.println("auBt.run");

        final Optional<Author> a = authorRepo.findTopByOrderByAuthorIDDesc();
        if (a.isPresent()) {
            System.out.println("Last authorID is " + a.get().getAuthorID());
            a.get().initCounter(a.get().getAuthorID());
        }

        addAuthorIndividually();
        System.out.println("auBt.a4.exit\n");
    }


    private void addAuthorIndividually() {
        addAuthor("William Shakespeare", "England, 1564-1616");
        addAuthor("Jane Austen", "England, 1775-1817");
        addAuthor("Fernando Pessoa", "Portugal, 1888-1935");
        addAuthor("Mark Twain", "United States, 1835-1910");
        addAuthor("Leo Tolstoy", "Russia, 1828-1910");
        addAuthor("Charles Dickens", "England, 1812-1870");
        addAuthor("Sophia de Mello Breyner Andresen", "Portugal, 1919-2004");
        addAuthor("Homer", "Ancient Greece, c. 8th century BC");
        addAuthor("Gabriel Garcia Marquez", "Colombia, 1927-2014");
        addAuthor("Franz Kafka", "Austria-Hungary, 1883-1924");
        addAuthor("George Orwell", "India (British Raj), 1903-1950");
        addAuthor("Fyodor Dostoevsky", "Russia, 1821-1881");
        addAuthor("Herman Melville", "United States, 1819-1891");
        addAuthor("José Saramago", "Portugal, 1922-2010");
        addAuthor("Virginia Woolf", "England, 1882-1941");
        addAuthor("James Joyce", "Ireland, 1882-1941");
        addAuthor("Marcel Proust", "France, 1871-1922");
        addAuthor("Ernest Hemingway", "United States, 1899-1961");
        addAuthor("Eça de Queirós", "Portugal, 1845-1900");
        addAuthor("Camilo Castelo Branco", "Portugal, 1825-1890");
        addAuthor("Miguel Torga", "Portugal, 1907-1995");
        addAuthor("Antero de Quental", "Portugal, 1842-1891");
        addAuthor("Almeida Garrett", "Portugal, 1799-1854");
        addAuthor("Florbela Espanca", "Portugal, 1894-1930");
        addAuthor("António Lobo Antunes", "Portugal, 1942-");
        addAuthor("Agustina Bessa-Luís", "Portugal, 1922-2019");
        addAuthor("Luís de Camões", "Portugal, 1524-1580");
        addAuthor("Alexandre Herculano", "Portugal, 1810-1877");
        addAuthor("Vergílio Ferreira", "Portugal, 1916-1996");

    }


    private void addAuthor(final String name, final String biography) {
        Author author = new Author(name, biography);
        authorRepo.save(author);
    }
}



