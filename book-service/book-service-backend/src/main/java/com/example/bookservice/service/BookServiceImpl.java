package com.example.bookservice.service;

import com.example.bookservice.client.LendingDTO;
import com.example.bookservice.client.LendingServiceClient;
import com.example.bookservice.model.*;
import com.example.bookservice.repositories.AuthorRepository;
import com.example.bookservice.repositories.BookImageRepository;
import com.example.bookservice.repositories.GenreRepository;
import com.example.bookservice.exceptions.NotFoundException;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.example.bookservice.repositories.BookRepository;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${server.port}")
    private String currentPort; // Porta da instância atual

    @Value("${book.instance1.url}")
    private String bookInstance1Url;

    @Value("${book.instance2.url}")
    private String bookInstance2Url;

    private final BookRepository bookRepository;

    private final BookImageRepository bookImageRepository;

    private final AuthorRepository authorRepository;

    private final LendingServiceClient lendingServiceClient;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository, BookImageRepository bookImageRepository, GenreRepository genreRepository, AuthorRepository authorRepository, LendingServiceClient lendingServiceClient) {
        this.bookRepository = bookRepository;
        this.bookImageRepository = bookImageRepository;
        this.genreRepository = genreRepository;
        this.authorRepository = authorRepository;
        this.lendingServiceClient = lendingServiceClient;
    }

    @Override
    public Book create(CreateBookRequest request) {
        String isbn = request.getIsbn();
        Book book = new Book();
        if (!book.isValidIsbn(isbn)) {
            throw new IllegalArgumentException("Invalid ISBN");
        }

        Genre genre = genreRepository.findByInterest(request.getGenre());
        if (genre == null) {
            throw new NotFoundException("Genre not found");
        }

        List<Author> authors = new ArrayList<>();
        for (String authorId : request.getAuthorIds()) {
            Author author = authorRepository.findByAuthorID(authorId)
                    .orElseThrow(() -> new NotFoundException("Author not found with ID: " + authorId));
            authors.add(author);
        }

        BookImage bookImage = bookImageRepository.findById(request.getBookImageId())
                .orElseThrow(() -> new NotFoundException("Book image not found"));

        Book newBook = new Book();
        newBook.setIsbn(isbn);
        newBook.setTitle(request.getTitle());
        newBook.setGenre(genre);
        newBook.setDescription(request.getDescription());
        newBook.setAuthor(authors);
        newBook.setBookImage(bookImage);

        // Salva o livro na instância atual
        Book savedBook = bookRepository.save(newBook);

        // Obtenha a URL da outra instância para sincronização
        String otherInstanceUrl = getOtherInstanceUrl();
        System.out.println("Other instance url: " + otherInstanceUrl);
        BookDTO bookDTO = new BookDTO(book);
        bookDTO.setTitle(savedBook.getTitle());

        // Sincronizar com a outra instância
        try {
            // Usa o DTO ou a entidade que você precisa para representar o livro na requisição
            restTemplate.postForEntity(otherInstanceUrl + "/api/books/sync", savedBook, Book.class);
        } catch (Exception e) {
            System.err.println("Erro ao sincronizar o livro com a outra instância: " + e.getMessage());
        }

        return savedBook;
    }


    public String getOtherInstanceUrl() {
        if (currentPort.equals("8082")) {
            return bookInstance2Url;
        } else {
            return bookInstance1Url;
        }
    }



    @Override
    public boolean isBookIDUnique(Long bookID) {
        return bookRepository.findBookByBookID(bookID).isEmpty();
    }

    @Override
    public Optional<Book> getBookById(final Long bookID) {
        return bookRepository.findById(bookID);
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
        Book book = bookRepository.findBookByBookID(bookID)
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

    @Override
    public List<BookCountDTO> findTop5Books() {
        List<LendingDTO> lendings = lendingServiceClient.getAllLendings();

        Map<Long, Long> bookIdCounts = lendings.stream()
                .collect(Collectors.groupingBy(LendingDTO::getBookID, Collectors.counting()));

        List<Map.Entry<Long, Long>> top5Books = bookIdCounts.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());

        return top5Books.stream()
                .map(entry -> new BookCountDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }


}
