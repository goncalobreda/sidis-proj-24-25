package com.example.readerserviceQuery.api;

import com.example.readerserviceQuery.client.GenreDTO;
import com.example.readerserviceQuery.model.Reader;
import com.example.readerserviceQuery.model.ReaderCountDTO;
import com.example.readerserviceQuery.service.ReaderServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Tag(name = "Readers", description = "Endpoints for managing readers")
@RestController
@RequestMapping("/api/readers")
class ReaderController {

    private final ReaderServiceImpl readerService;

    @Autowired
    public ReaderController(ReaderServiceImpl readerService) {
        this.readerService = readerService;
    }

    @Operation(summary = "Gets all readers")
    @ApiResponse(description = "Success", responseCode = "200", content = {@Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = Reader.class)))})
    @GetMapping
    public List<Reader> findAll() {
        return readerService.findAll();
    }

    @Operation(summary = "Gets a specific Reader by id")
    @GetMapping(value = "/id/{id1}/{id2}")
    public ResponseEntity<Reader> findById(
            @PathVariable("id1") @Parameter(description = "The id of the reader to find") final String id1,
            @PathVariable("id2") final String id2) {
        String readerID = id1 + "/" + id2;
        return readerService.getReaderByID(readerID)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Gets a specific Reader by email")
    @GetMapping(value = "/email/{email}")
    public ResponseEntity<Reader> findByEmail(
            @PathVariable("email") @Parameter(description = "The email of the reader to find") final String email) {
        return readerService.getReaderByEmail(email)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Gets a Reader by name")
    @GetMapping(value = "/name/{name}")
    public List<Reader> findByName(
            @PathVariable("name") @Parameter(description = "The name of the Reader to find") final String name) {
        return readerService.getReaderByName(name);
    }

    @Operation(summary = "Get top 5 Readers by number of lendings")
    @GetMapping("/top5Readers")
    public List<ReaderCountDTO> getTop5Readers() {
        return readerService.findTop5Readers();
    }


}
