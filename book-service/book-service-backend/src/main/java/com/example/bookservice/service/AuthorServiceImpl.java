package com.example.bookservice.service;

import com.example.bookservice.model.Author;
import com.example.bookservice.model.CoAuthorDTO;
import com.example.bookservice.repositories.AuthorRepository;
import com.example.bookservice.model.Book;
import com.example.bookservice.repositories.BookRepository;
import com.example.bookservice.exceptions.NotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    private final BookRepository bookRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public Author create(CreateAuthorRequest request) {
        if (request.getBiography() == null || request.getBiography().length() > 4096) {
            throw new IllegalArgumentException("The biography cannot be null, nor have more than 4096 characters.");
        }
        if (request.getName() == null || request.getName().length() > 150) {
            throw new IllegalArgumentException("The name cannot be null, nor have more than 150 characters.");
        }

        final Author author = new Author(request.getName(), request.getBiography());
        author.setUniqueAuthorID();
        return authorRepository.save(author);
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
    public Author partialUpdate(final String authorID, final EditAuthorRequest request, final long desiredVersion) {
        final var author = authorRepository.findByAuthorID(authorID)
                .orElseThrow(() -> new NotFoundException("Cannot update an object that does not yet exist"));

        author.applyPatch(desiredVersion, request.getName(), request.getBiography());
        return authorRepository.save(author);
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

}
