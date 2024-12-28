package com.example.bookservice.service;



import com.example.bookservice.dto.BookSyncDTO;
import com.example.bookservice.messaging.RabbitMQProducer;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final BookImageRepository bookImageRepository;
    private final RabbitMQProducer rabbitMQProducer;
    private static final Logger logger = LoggerFactory.getLogger(BookServiceImpl.class);


    public BookServiceImpl(
            BookRepository bookRepository,
            AuthorRepository authorRepository,
            GenreRepository genreRepository,
            BookImageRepository bookImageRepository,
            RabbitMQProducer rabbitMQProducer) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.genreRepository = genreRepository;
        this.bookImageRepository = bookImageRepository;
        this.rabbitMQProducer = rabbitMQProducer;
    }

    @Override
    public void syncBook(BookSyncDTO bookSyncDTO) {
        logger.info("Iniciando sincronização do livro com ISBN: {}", bookSyncDTO.getIsbn());

        // Validar o DTO recebido
        if (bookSyncDTO.getIsbn() == null || bookSyncDTO.getTitle() == null || bookSyncDTO.getAuthors() == null) {
            logger.error("Dados incompletos no BookSyncDTO: {}", bookSyncDTO);
            return;
        }

        try {
            // Buscar ou criar o livro
            Book book = bookRepository.findByIsbn(bookSyncDTO.getIsbn())
                    .orElseGet(() -> {
                        logger.info("Livro não encontrado, criando um novo com ISBN: {}", bookSyncDTO.getIsbn());
                        return new Book();
                    });

            // Atualizar informações do livro
            book.setIsbn(bookSyncDTO.getIsbn());
            book.setTitle(bookSyncDTO.getTitle());
            book.setDescription(bookSyncDTO.getDescription());
            logger.info("Dados do livro atualizados: Title = {}, Description = {}", book.getTitle(), book.getDescription());

            // Buscar ou criar o gênero
            Genre genre = genreRepository.findByInterest(bookSyncDTO.getGenre());
            if (genre != null) {
                book.setGenre(genre);
                logger.info("Gênero associado ao livro: {}", genre.getInterest());
            } else {
                logger.warn("Gênero não encontrado para o interesse: {}", bookSyncDTO.getGenre());
            }

            // Processar autores
            List<Author> authors = bookSyncDTO.getAuthors().stream()
                    .map(authorDTO -> {
                        logger.info("Sincronizando autor com ID: {}", authorDTO.getAuthorID());
                        return authorRepository.findByAuthorID(authorDTO.getAuthorID())
                                .orElseGet(() -> {
                                    logger.info("Autor não encontrado, criando um novo com ID: {}", authorDTO.getAuthorID());
                                    Author newAuthor = new Author();
                                    newAuthor.setAuthorID(authorDTO.getAuthorID());
                                    newAuthor.setName(authorDTO.getName());
                                    newAuthor.setBiography(authorDTO.getBiography());
                                    return authorRepository.save(newAuthor);
                                });
                    })
                    .collect(Collectors.toList());

            // Associar os autores ao livro
            book.setAuthor(authors);
            logger.info("Autores associados ao livro: {}", authors.stream().map(Author::getAuthorID).toList());

            // Salvar o livro no banco de dados
            bookRepository.save(book);
            logger.info("Livro sincronizado e salvo com sucesso: ISBN = {}", book.getIsbn());

        } catch (Exception e) {
            logger.error("Erro ao sincronizar livro: {}", e.getMessage(), e);
        }
    }



    @Override
    public Book create(CreateBookRequest request) {
        if (request.getIsbn() == null || request.getIsbn().isBlank()) {
            throw new IllegalArgumentException("O ISBN não pode ser vazio.");
        }

        if (bookRepository.findByIsbn(request.getIsbn()).isPresent()) {
            throw new IllegalArgumentException("O ISBN já está em uso.");
        }

        Genre genre = genreRepository.findByInterest(request.getGenre());
        if (genre == null) {
            throw new IllegalArgumentException("Gênero não encontrado: " + request.getGenre());
        }

        List<Author> authors = new ArrayList<>();
        if (request.getAuthorIds() != null) {
            authors = request.getAuthorIds().stream()
                    .map(authorId -> authorRepository.findByAuthorID(authorId)
                            .orElseThrow(() -> new IllegalArgumentException("Autor não encontrado com ID: " + authorId)))
                    .collect(Collectors.toList());
        }

        BookImage bookImage = bookImageRepository.findById(request.getBookImageId())
                .orElseThrow(() -> new IllegalArgumentException("Imagem não encontrada com ID: " + request.getBookImageId()));

        Book book = new Book();
        book.setIsbn(request.getIsbn());
        book.setTitle(request.getTitle());
        book.setDescription(request.getDescription());
        book.setGenre(genre);
        book.setAuthor(authors);
        book.setBookImage(bookImage); // Certifique-se de que o campo existe no request.


        book = bookRepository.save(book);

        rabbitMQProducer.sendBookSyncEvent(book);

        return book;
    }


    @Override
    public Book partialUpdate(Long bookID, EditBookRequest request, long desiredVersion) {
        Book book = bookRepository.findById(bookID)
                .orElseThrow(() -> new IllegalArgumentException("Livro não encontrado"));

        if (request.getTitle() != null) {
            book.setTitle(request.getTitle());
        }

        if (request.getDescription() != null) {
            book.setDescription(request.getDescription());
        }

        if (request.getGenre() != null) {
            Genre genre = genreRepository.findByInterest(request.getGenre());
            if (genre != null) {
                book.setGenre(genre);
            }
        }

        book.applyPatch(desiredVersion, book.getTitle(), book.getGenre(), book.getDescription());
        book = bookRepository.save(book);

        rabbitMQProducer.sendPartialUpdateEvent(book);

        return book;
    }


    @Override
    public boolean isBookIDUnique(Long bookID) {
        return bookRepository.findBookByBookID(bookID).isEmpty();
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


}
