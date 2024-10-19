package com.example.bookservice.service;

import com.example.bookservice.model.Author;
import com.example.bookservice.repositories.AuthorRepository;
import com.example.bookservice.model.Genre;
import com.example.bookservice.model.BookImage;
import com.example.bookservice.repositories.BookImageRepository;
import com.example.bookservice.repositories.GenreRepository;
import com.example.bookservice.exceptions.NotFoundException;


import org.springframework.stereotype.Service;
import com.example.bookservice.model.Book;
import com.example.bookservice.repositories.BookRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final BookImageRepository bookImageRepository;

    private final AuthorRepository authorRepository;

    public BookServiceImpl(BookRepository bookRepository, BookImageRepository bookImageRepository, GenreRepository genreRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.bookImageRepository = bookImageRepository;
        this.genreRepository = genreRepository;
        this.authorRepository = authorRepository;
    }

    public Book create(CreateBookRequest request) {
        String isbn = request.getIsbn();
        if (!isValidIsbn(isbn)) {
            throw new IllegalArgumentException("Invalid ISBN");
        }

        // Fetch the Genre entity
        Genre genre = genreRepository.findByInterest(request.getGenre());
        if (genre == null) {
            throw new NotFoundException("Genre not found");
        }

        // Fetch the Author entities
        List<Author> authors = new ArrayList<>();
        for (String authorId : request.getAuthorIds()) {
            Author author = authorRepository.findByAuthorID(authorId)
                    .orElseThrow(() -> new NotFoundException("Author not found with ID: " + authorId));
            authors.add(author);
        }

        // Fetch the BookImage entity
        BookImage bookImage = bookImageRepository.findById(request.getBookImageId())
                .orElseThrow(() -> new NotFoundException("Book image not found"));

        // Create and save the new Book entity
        Book newBook = new Book();
        newBook.setIsbn(isbn);
        newBook.setTitle(request.getTitle());
        newBook.setGenre(genre);
        newBook.setDescription(request.getDescription());
        newBook.setAuthor(authors); // Set the list of authors
        newBook.setBookImage(bookImage);

        bookRepository.save(newBook);
        return newBook;
    }


    @Override
    public boolean isBookIDUnique(Long bookID) {
        // Check if readerID already exists
        return bookRepository.findById(bookID).isEmpty();
    }

    @Override
    public Optional<Book> getBookById(final Long bookID) {
        return bookRepository.findById(bookID);
    }

    public boolean isValidISBN13(String isbn) {
        if (isbn == null || isbn.length() != 13) {
            return false;
        }
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = Character.getNumericValue(isbn.charAt(i));
            if (digit < 0 || digit > 9) {
                return false;
            }
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        int checkDigit = Character.getNumericValue(isbn.charAt(12));
        int calculatedCheckDigit = 10 - (sum % 10);
        if (calculatedCheckDigit == 10) {
            calculatedCheckDigit = 0;
        }
        return checkDigit == calculatedCheckDigit;
    }

    public boolean isValidISBN10(String isbn) {
        if (isbn == null || isbn.length() != 10) {
            return false;
        }
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            int digit = Character.getNumericValue(isbn.charAt(i));
            if (digit < 0 || digit > 9) {
                return false;
            }
            sum += (i + 1) * digit;
        }
        char lastChar = isbn.charAt(9);
        if (lastChar == 'X') {
            sum += 10;
        } else {
            int lastDigit = Character.getNumericValue(lastChar);
            if (lastDigit < 0 || lastDigit > 9) {
                return false;
            }
            sum += 10 * lastDigit;
        }
        return sum % 11 == 0;
    }

    @Override
    public boolean isValidIsbn(String isbn) {
        if (isbn == null || isbn.isEmpty()) {
            return false;
        }
        // Remove spaces and dashes
        isbn = isbn.replaceAll("[\\s-]", "");
        // Check if the length is valid for ISBN-10 or ISBN-13
        if (isbn.length() != 10 && isbn.length() != 13) {
            return false;
        }
        // Check if the ISBN-10 or ISBN-13 format is valid
        if (isbn.length() == 10) {
            return isValidISBN10(isbn);
        } else {
            return isValidISBN13(isbn);
        }
    }


    public List<Book> getAll() {
        return bookRepository.findAll();
    }


    @Override
    public Optional<Book> getBookByIsbn(final String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    @Override
    public List<Book> getBookByGenre(final String genre) {
        return bookRepository.findByGenre(genre);
    }

    @Override
    public Genre getGenreByInterest(String interest) {
        return genreRepository.findByInterest(interest);
    }

    @Override
    public List<Book> getBookByTitle(final String title) {
        return bookRepository.findByTitle(title);
    }

    private GenreRepository genreRepository;

    @Override
    public List<Map.Entry<String, Long>> findTop5Genres() {
        List<Genre> genres = genreRepository.findAll();
        Map<String, Long> genreBookCount = new HashMap<>();

        for (Genre genre : genres) {
            long count = genre.getBooks().size();
            genreBookCount.put(genre.getInterest(), count);
        }

        return genreBookCount.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());
    }


    @Override
    public List<Book> getBooksByAuthorId(String authorID) {
        return bookRepository.findByAuthorId(authorID);
    }

    public void addImageToBook(Long bookID, byte[] image, String contentType) {
        Book book = bookRepository.findById(bookID)
                .orElseThrow(() -> new NotFoundException("Book not found"));

        saveBookWithImage(book, image, contentType);
    }



    public void saveBookWithImage(Book book, byte[] image, String contentType) {
        Book savedBook = bookRepository.save(book); // Salva o livro no banco de dados

        BookImage bookImage = new BookImage();
        bookImage.setBook(savedBook); // Associa a imagem ao livro salvo
        bookImage.setImage(image);
        bookImage.setContentType(contentType);

        bookImageRepository.save(bookImage); // Salva a imagem no banco de dados
    }

    @Override
    public Book partialUpdate(final Long bookID, final EditBookRequest request, final long desiredVersion) {
        var existingBook = getBookById(bookID).orElseThrow(() -> new NotFoundException("Cannot update an object that does not yet exist"));

        Genre genre = genreRepository.findByInterest(request.getGenre());
        if (genre == null) {
            throw new NotFoundException("Genre not found");
        }

        existingBook.applyPatch(desiredVersion, request.getTitle(), genre, request.getDescription());
        bookRepository.save(existingBook);
        return existingBook;
    }

}
