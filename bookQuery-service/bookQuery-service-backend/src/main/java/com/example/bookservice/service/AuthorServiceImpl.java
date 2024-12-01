package com.example.bookservice.service;


import com.example.bookservice.model.*;
import com.example.bookservice.repositories.AuthorRepository;
import com.example.bookservice.repositories.BookRepository;
import com.example.bookservice.exceptions.NotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    private final BookRepository bookRepository;


    private final BookService bookService;

    public AuthorServiceImpl(AuthorRepository authorRepository, BookRepository bookRepository, BookService bookService) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
        this.bookService = bookService;
    }

    @Override
    public List<Author> findByName(String name) {
        return authorRepository.findByName(name);
    }

    @Override
    public Optional<Author> findByAuthorID(String authorID) {
        return authorRepository.findByAuthorID(authorID);
    }



    @Override
    @Transactional(readOnly = true)
    public List<CoAuthorDTO> getCoAuthorsAndBooks(String authorId) {
        Optional<Author> authorOpt = authorRepository.findByAuthorID(authorId);
        if (authorOpt.isEmpty()) {
            throw new EntityNotFoundException("Author not found");
        }

        Author author = authorOpt.get();
        System.out.println("Author found: " + author.getName());

        List<Book> books = authorRepository.findByAuthorsContaining(author);
        System.out.println("Books found: " + books.size());

        Map<Author, List<Book>> coAuthorBooksMap = new HashMap<>();

        for (Book book : books) {
            System.out.println("Processing book: " + book.getTitle());
            for (Author coAuthor : book.getAuthor()) {
                System.out.println("Checking co-author: " + coAuthor.getName());
                if (!coAuthor.equals(author)) {
                    System.out.println("Co-author found: " + coAuthor.getName());
                    coAuthorBooksMap.computeIfAbsent(coAuthor, k -> new ArrayList<>()).add(book);
                }
            }
        }

        System.out.println("Co-authors and books map size: " + coAuthorBooksMap.size());

        return coAuthorBooksMap.entrySet().stream()
                .map(entry -> new CoAuthorDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<AuthorDTO> getAuthorAndBooks(String authorId) {
        Optional<Author> authorOpt = authorRepository.findByAuthorID(authorId);
        if (authorOpt.isEmpty()) {
            throw new EntityNotFoundException("Author not found");
        }

        Author author = authorOpt.get();

        // Mapeia os livros do autor para BookDTO
        List<BookDTO> bookDTOs = authorRepository.findByAuthorsContaining(author).stream()
                .map(book -> new BookDTO(book))
                .collect(Collectors.toList());

        // Retorna um AuthorDTO contendo as informações do autor e a lista de BookDTOs
        return Optional.of(new AuthorDTO(author.getAuthorID(), author.getName(), bookDTOs));
    }

    public String getAuthorImageUrl(String authorID) {
        Author author = findByAuthorID(authorID).orElseThrow(() -> new NotFoundException(Author.class, authorID));
        if (author.getImage() != null && author.getImage().getAuthorImageID() != null) {
            String imageUrl = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/api/authors/")
                    .path(authorID.replace("/", "_"))
                    .path("/photo/")
                    .path(author.getImage().getAuthorImageID().toString())
                    .toUriString();
            System.out.println("Generated Image URL: " + imageUrl);
            return imageUrl;
        }
        System.out.println("No Image Found for Author ID: " + authorID);
        return null;
    }



    public void saveAuthor(Author author) {
        authorRepository.save(author);
    }
/*
    public List<TopAuthorLendingDTO> findTop5AuthorsPerLending() {
        List<LendingDTO> lendings = lendingServiceClient.getAllLendings();

        Map<Author, Long> authorLendingCounts = new HashMap<>();

        for (LendingDTO lending : lendings) {
            Book book = bookService.getBookById(lending.getBookID())  // Corrigido para pegar o bookID do LendingDTO
                    .orElseThrow(() -> new NotFoundException("Book not found with ID: " + lending.getBookID()));

            for (Author author : book.getAuthor()) {  // book.getAuthor() retorna a lista de autores
                authorLendingCounts.put(author, authorLendingCounts.getOrDefault(author, 0L) + 1);
            }
        }

        List<Map.Entry<Author, Long>> top5Authors = authorLendingCounts.entrySet().stream()
                .sorted(Map.Entry.<Author, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());

        return top5Authors.stream()
                .map(entry -> new TopAuthorLendingDTO(entry.getKey().getAuthorID(), entry.getKey().getName(), entry.getValue()))
                .collect(Collectors.toList());
    }*/

    @Override
    public Optional<Author> getLastId() {
        return authorRepository.findFirstByOrderByAuthorIDDesc();
    }
}
