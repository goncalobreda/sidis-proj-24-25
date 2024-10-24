package com.example.lendingservice.api;

import com.example.lendingservice.model.Lending;
import com.example.lendingservice.repositories.LendingRepository;
import com.example.lendingservice.service.LendingService;
import com.example.lendingservice.service.CreateLendingRequest;
import com.example.lendingservice.service.EditLendingRequest;
import com.example.lendingservice.exceptions.NotFoundException;
import com.example.lendingservice.service.LendingServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Tag(name = "Lendings", description = "Endpoints for managing Lendings")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lendings")
public class LendingController {

    private final LendingService service;
    private final LendingViewMapper lendingViewMapper;
    private final LendingRepository lendingRepository;

    @Autowired
    private LendingServiceImpl lendingService;

    @Operation(summary = "Gets all lendings")
    @ApiResponse(description = "Success", responseCode = "200", content = {
            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = LendingView.class)))
    })
    @GetMapping
    public Iterable<LendingView> findAll() {
        return lendingViewMapper.toLendingView(service.findAll());
    }

    @Operation(summary = "Gets a specific lending")
    @GetMapping(value = "/{id1}/{id2}")
    public ResponseEntity<LendingView> findById(@PathVariable("id1") final int id1, @PathVariable("id2") final int id2) {
        final var lending = service.findById(id1, id2)
                .orElseThrow(() -> new NotFoundException(Lending.class, id1 + "/" + id2));
        return ResponseEntity.ok().eTag(Long.toString(lending.getVersion())).body(lendingViewMapper.toLendingView(lending));
    }

    @Operation(summary = "Creates a new lending")
    @PostMapping
    public ResponseEntity<LendingView> create(@RequestBody @Valid CreateLendingRequest request) {
        final var lending = service.create(request);
        return ResponseEntity.ok(lendingViewMapper.toLendingView(lending));
    }

    @Operation(summary = "Partially updates an existing lending")
    @PatchMapping(value = "/{id1}/{id2}")
    public ResponseEntity<LendingView> partialUpdate(@PathVariable("id1") final int id1, @PathVariable("id2") final int id2,
                                                     @Valid @RequestBody final EditLendingRequest resource) {
        final var lending = service.partialUpdate(id1, id2, resource, 1L);
        return ResponseEntity.ok().body(lendingViewMapper.toLendingView(lending));
    }

    @Operation(summary = "Lists overdue lendings sorted by their tardiness")
    @GetMapping("/overdue")
    public ResponseEntity<List<LendingView>> listOverdueLendingsSortedByTardiness() {
        List<Lending> overdueLendings = service.getOverdueLendingsSortedByTardiness();
        List<LendingView> lendingViews = lendingViewMapper.toLendingView(overdueLendings);
        return ResponseEntity.ok(lendingViews);
    }



    @Operation(summary = "Gets the average lending duration")
    @GetMapping("/average-lending-duration")
    public ResponseEntity<Double> getAverageLendingDuration() {
        double avgDuration = service.getAverageLendingDuration();
        return ResponseEntity.ok(avgDuration);
    }


    @Operation(summary = "Gets the average number of lending per genre of a certain month")
    @GetMapping("/average-lending-per-genre")
    public ResponseEntity<Map<String, Double>> getAverageLendingDurationPerGenreAndMonth(
            @RequestParam int month, @RequestParam int year) {
        Map<String, Double> result = service.getAverageLendingsPerGenre(month, year);
        return ResponseEntity.ok(result);
    }



    @PostMapping("/sync")
    public ResponseEntity<Lending> createLendingSync(@RequestBody Lending lending) {
        Optional<Lending> existingLending = lendingRepository.findByLendingID(lending.getLendingID());

        if (existingLending.isPresent()) {
            Lending existing = existingLending.get();
            // Atualiza os campos modificados, sem validar a versão
            existing.setBookID(lending.getBookID());
            existing.setReaderID(lending.getReaderID());
            existing.setStartDate(lending.getStartDate());
            existing.setExpectedReturnDate(lending.getExpectedReturnDate());
            existing.setReturnDate(lending.getReturnDate());
            existing.setOverdue(lending.isOverdue());
            existing.setFine(lending.getFine());
            existing.setNotes(lending.getNotes());
            lending.updateOverdueStatus();

            existing.setVersion(lending.getVersion());

            Lending updatedLending = lendingRepository.save(existing); // Atualiza a entidade
            return ResponseEntity.ok(updatedLending);
        } else {
            // Se o empréstimo não existir, cria um novo
            Lending savedLending = lendingRepository.save(lending);
            return ResponseEntity.ok(savedLending);
        }
    }




}
