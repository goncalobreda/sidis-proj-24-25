package com.example.bookservice.service;


import com.example.bookservice.dto.BookSyncDTO;
import com.example.bookservice.dto.CreateLendingDTO;
import com.example.bookservice.dto.PartialUpdateDTO;
import com.example.bookservice.model.*;
import com.example.bookservice.repositories.*;
import com.example.bookservice.exceptions.NotFoundException;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookServiceImpl.class);

    private final BookRepository bookRepository;
    private final BookImageRepository bookImageRepository;
    private final AuthorRepository authorRepository;
    private final LendingRepository lendingRepository;
    private final GenreRepository genreRepository;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository, BookImageRepository bookImageRepository,
                           GenreRepository genreRepository, AuthorRepository authorRepository,
                           LendingRepository lendingRepository) {
        this.bookRepository = bookRepository;
        this.bookImageRepository = bookImageRepository;
        this.genreRepository = genreRepository;
        this.authorRepository = authorRepository;
        this.lendingRepository = lendingRepository;
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

    @Override
    public List<BookCountDTO> findTop5Books() {
        List<Object[]> results = bookRepository.findTop5BooksNative();

        List<BookCountDTO> topBooks = new ArrayList<>();
        for (Object[] row : results) {
            Long bookId = ((Number) row[0]).longValue();
            Long lendingCount = ((Number) row[1]).longValue();
            topBooks.add(new BookCountDTO(bookId, lendingCount));
        }

        return topBooks;
    }

    public Book createOrUpdateFromBookSyncDTO(BookSyncDTO bookSyncDTO) {
        logger.info("Sincronizando livro a partir do BookSyncDTO: {}", bookSyncDTO);

        // Validação dos dados do DTO
        if (bookSyncDTO.getIsbn() == null || bookSyncDTO.getIsbn().isBlank()) {
            throw new IllegalArgumentException("O ISBN é obrigatório para sincronizar um livro.");
        }
        if (bookSyncDTO.getTitle() == null || bookSyncDTO.getTitle().isBlank()) {
            throw new IllegalArgumentException("O título é obrigatório para sincronizar um livro.");
        }

        // Buscar ou criar o livro
        Book book = bookRepository.findByIsbn(bookSyncDTO.getIsbn())
                .orElseGet(() -> {
                    logger.info("Livro não encontrado, criando um novo com ISBN: {}", bookSyncDTO.getIsbn());
                    Book newBook = new Book();
                    newBook.setIsbn(bookSyncDTO.getIsbn());
                    return newBook;
                });

        // Atualizar os dados do livro
        book.setTitle(bookSyncDTO.getTitle());
        book.setDescription(bookSyncDTO.getDescription());
        logger.info("Livro atualizado: ISBN = {}, Title = {}, Description = {}",
                book.getIsbn(), book.getTitle(), book.getDescription());

        // Associar ou criar o gênero
        Genre genre = genreRepository.findByInterest(bookSyncDTO.getGenre());
        if (genre != null) {
            book.setGenre(genre);
            logger.info("Gênero associado ao livro: {}", genre.getInterest());
        } else {
            logger.warn("Gênero não encontrado para interesse: {}", bookSyncDTO.getGenre());
            throw new IllegalArgumentException("Gênero inválido: " + bookSyncDTO.getGenre());
        }

        // Associar ou criar autores
        List<Author> authors = bookSyncDTO.getAuthors().stream()
                .map(authorDTO -> authorRepository.findByAuthorID(authorDTO.getAuthorID())
                        .orElseGet(() -> {
                            logger.info("Criando novo autor com ID: {}", authorDTO.getAuthorID());
                            Author newAuthor = new Author();
                            newAuthor.setAuthorID(authorDTO.getAuthorID());
                            newAuthor.setName(authorDTO.getName());
                            newAuthor.setBiography(authorDTO.getBiography());
                            return authorRepository.save(newAuthor);
                        }))
                .collect(Collectors.toList());

        book.setAuthor(authors);
        logger.info("Autores associados ao livro: {}",
                authors.stream().map(Author::getAuthorID).toList());

        // Ignorar a imagem do livro
        book.setBookImage(null);
        logger.info("A sincronização da imagem do livro foi ignorada e definida como null.");

        // Salvar o livro no repositório
        Book savedBook = bookRepository.save(book);
        logger.info("Livro sincronizado com sucesso: ISBN = {}", savedBook.getIsbn());
        return savedBook;
    }


    @Override
    public void applyPartialUpdate(PartialUpdateDTO partialUpdateDTO) {
        logger.info("Aplicando partial update no livro: bookID={}", partialUpdateDTO.getBookID());

        if (partialUpdateDTO.getBookID() == null) {
            throw new IllegalArgumentException("O ID do livro é obrigatório para a atualização parcial.");
        }

        Book book = bookRepository.findById(partialUpdateDTO.getBookID())
                .orElseThrow(() -> new NotFoundException("Livro não encontrado para atualização parcial"));

        if (partialUpdateDTO.getTitle() != null && !partialUpdateDTO.getTitle().isBlank()) {
            book.setTitle(partialUpdateDTO.getTitle());
        }
        if (partialUpdateDTO.getDescription() != null && !partialUpdateDTO.getDescription().isBlank()) {
            book.setDescription(partialUpdateDTO.getDescription());
        }

        bookRepository.save(book);
        logger.info("Livro atualizado com sucesso: bookID={}", book.getBookID());
    }
}
