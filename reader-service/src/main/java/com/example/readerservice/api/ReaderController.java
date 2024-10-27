package com.example.readerservice.api;

import com.example.readerservice.client.BookDTO;
import com.example.readerservice.client.GenreDTO;
import com.example.readerservice.exceptions.NotFoundException;
import com.example.readerservice.model.Reader;
import com.example.readerservice.model.ReaderCountDTO;
import com.example.readerservice.repositories.ReaderRepository;
import com.example.readerservice.service.CreateReaderRequest;
import com.example.readerservice.service.EditReaderRequest;
import com.example.readerservice.service.ReaderServiceImpl;
import com.example.readerservice.api.ReaderView;
import com.example.readerservice.service.SearchReadersQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.awt.print.Book;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Tag(name = "Readers", description = "Endpoints for managing readers")
@RestController
@RequestMapping("/api/readers")
class ReaderController {

    private static final String IF_MATCH = "If-Match";
    private static final Logger logger = LoggerFactory.getLogger(ReaderController.class);

    private final ReaderServiceImpl readerService;
    private final ReaderViewMapper readerMapper;
    private final ReaderRepository readerRepository;

    @Autowired
    public ReaderController(ReaderServiceImpl readerService, ReaderViewMapper readerMapper, ReaderRepository readerRepo) {
        this.readerService = readerService;
        this.readerMapper = readerMapper;
        this.readerRepository = readerRepo;
    }

    @Operation(summary = "Gets all readers")
    @ApiResponse(description = "Success", responseCode = "200", content = {@Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = ReaderView.class)))})
    @GetMapping
    public Iterable<ReaderView> findAll() {
        return readerMapper.toReaderView(readerService.findAll());
    }

    @Operation(summary = "Gets a specific Reader by id")
    @GetMapping(value = "/id/{id1}/{id2}")
    public ResponseEntity<ReaderView> findById(
            @PathVariable("id1") @Parameter(description = "The id of the reader to find") final String id1,
            @PathVariable("id2") final String id2) {
        String readerID = id1 + "/" + id2;
        final var reader = readerService.getReaderByID(readerID).orElseThrow(() -> new NotFoundException(Reader.class, readerID));
        return ResponseEntity.ok().eTag(Long.toString(reader.getVersion())).body(readerMapper.toReaderView(reader));
    }

    @Operation(summary = "Gets a specific Reader by email")
    @GetMapping(value = "/email/{email}")
    public ResponseEntity<ReaderView> findByEmail(
            @PathVariable("email") @Parameter(description = "The email of the reader to find") final String email) {
        Reader reader = readerService.getReaderByEmail(email).orElseThrow(() -> new NotFoundException(Reader.class, email));
        return ResponseEntity.ok().eTag(Long.toString(reader.getVersion())).body(readerMapper.toReaderView(reader));
    }


    @Operation(summary = "Partially updates an existing reader")
    @PatchMapping(value = "/{id1}/{id2}")
    public ResponseEntity<ReaderView> partialUpdate(final WebRequest request,
                                                    @PathVariable("id1") final String id1, @PathVariable("id2") final String id2,
                                                    @Valid @RequestBody final EditReaderRequest resource) {
        String readerID = id1 + "/" + id2;

        final String ifMatchValue = request.getHeader(IF_MATCH);
        if (ifMatchValue == null || ifMatchValue.isEmpty()) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "You must issue a conditional PATCH using 'if-match'");
        }

        final var reader = readerService.partialUpdate(readerID, resource, getVersionFromIfMatchHeader(ifMatchValue));
        return ResponseEntity.ok().eTag(Long.toString(reader.getVersion())).body(readerMapper.toReaderView(reader));
    }

    private Long getVersionFromIfMatchHeader(final String ifMatchHeader) {
        if (ifMatchHeader.startsWith("\"")) {
            return Long.parseLong(ifMatchHeader.substring(1, ifMatchHeader.length() - 1));
        }
        return Long.parseLong(ifMatchHeader);
    }

    @Operation(summary = "Get top 5 Readers by number of lendings")
    @GetMapping("/top5Readers")
    public List<ReaderCountDTO> getTop5Readers() {
        return readerService.findTop5Readers();
    }

    @Operation(summary = "Get a Reader by name")
    @GetMapping(value = "/name/{name}")
    public List<ReaderView> findByName(
            @PathVariable("name") @Parameter(description = "The name of the Reader to find") final String name) {
        System.out.println("apiBookTitle");
        final var reader = readerService.getReaderByName(name);
        if (reader.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reader not found!");
        }

        return readerMapper.toReaderView(reader);
    }

    @Operation(summary = "Gets book suggestions based on the list of interests of the Reader")
    @GetMapping(value = "/suggestions/{id1}/{id2}")
    public List<GenreDTO> findSuggestions(@PathVariable("id1") final String id1, @PathVariable("id2") final String id2) {
        String readerID = id1 + "/" + id2;
        Reader reader = readerService.getReaderByID(readerID).orElseThrow(() -> new NotFoundException(Reader.class, readerID));

        return readerService.getBookSuggestions(reader);
    }

    @PatchMapping("/readers/{id1}/{id2}/interests")
    public ResponseEntity<ReaderView> updateInterests(
            @PathVariable("id1") String id1,
            @PathVariable("id2") String id2,
            @RequestBody Set<String> newInterests) {

        // Concatena o id1 e o id2 para formar o readerID
        String readerID = id1 + "/" + id2;

        // Busca o reader com o readerID completo (ano/counter)
        Reader reader = readerService.getReaderByID(readerID)
                .orElseThrow(() -> new NotFoundException(Reader.class, readerID));

        // Adiciona os novos interesses ao leitor
        reader.addInterests(newInterests);

        // Salva o reader atualizado
        readerRepository.save(reader);

        // Retorna o reader atualizado na resposta
        return ResponseEntity.ok(readerMapper.toReaderView(reader));
    }

    @Operation(summary = "Registra um novo Reader (Uso Interno)")
    @PostMapping("/internal/register")
    public ResponseEntity<Void> registerReader(@RequestBody Reader reader) {
        try {
            // Tenta sincronizar o reader recebido, sem criar um novo ID
            readerService.syncReceivedReader(reader);

            readerService.notifyOtherInstance(reader);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            logger.error("Erro ao processar a criação do reader: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

}
