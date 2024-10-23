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

    @Operation(summary = "Gets the average number of lendings per genre for a certain month")
    @GetMapping("/average-lending-per-genre")
    public ResponseEntity<Map<String, Double>> getAverageLendingPerGenreForMonth(
            @RequestParam int month, @RequestParam int year) {
        Map<String, Double> statistics = service.getAverageLendingPerGenreForMonth(month, year);
        return ResponseEntity.ok(statistics);
    }

    @Operation(summary = "Gets the average lending duration")
    @GetMapping("/average-lending-duration")
    public ResponseEntity<Double> getAverageLendingDuration() {
        double avgDuration = service.getAverageLendingDuration();
        return ResponseEntity.ok(avgDuration);
    }

    @Operation(summary = "Gets the number of lendings per month for the last 12 months, broken down by genre")
    @GetMapping("/lendings-per-month-by-genre")
    public ResponseEntity<Map<String, Map<String, Long>>> getLendingsPerMonthByGenreForLastYear() {
        Map<String, Map<String, Long>> statistics = service.getLendingsPerMonthByGenreForLastYear();
        return ResponseEntity.ok(statistics);
    }

    @Operation(summary = "Gets the number of lendings for a specific reader for a certain month")
    @GetMapping("/lendings-per-reader")
    public ResponseEntity<Long> getLendingCountByReaderForMonth(
            @RequestParam String readerID, @RequestParam int month, @RequestParam int year) {
        long lendingCount = service.getLendingCountByReaderForMonth(readerID, month, year);
        return ResponseEntity.ok(lendingCount);
    }

    @Operation(summary = "Gets the average lending duration per genre for a specific month and year")
    @GetMapping("/average-lending-duration-per-genre")
    public ResponseEntity<Map<String, Double>> getAverageLendingDurationPerGenreAndMonth(
            @RequestParam int month, @RequestParam int year) {
        Map<String, Double> result = service.getAverageLendingDurationPerGenre(month, year);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Gets the average lending duration per book")
    @GetMapping("/average-lending-duration-per-book")
    public ResponseEntity<Map<String, Double>> getAverageLendingDurationPerBook() {
        Map<String, Double> result = service.getAverageLendingDurationPerBook();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/sync")
    public ResponseEntity<Lending> createLendingSync(@RequestBody Lending lending) {
        lending.updateOverdueStatus();
        Lending savedLending = lendingRepository.save(lending); // Guardar o empréstimo sincronizado
        return ResponseEntity.ok(savedLending);
    }



    @DeleteMapping("/id/{id1}/{id2}")
    public ResponseEntity<Void> deleteLending(
            @PathVariable("id1") String id1,
            @PathVariable("id2") String id2) {

        String lendingID = id1 + "/" + id2;  // Combina o id1 e id2 para formar o lendingID completo
        lendingService.deleteLendingById(lendingID);  // Chama o serviço para eliminar o lending

        return ResponseEntity.noContent().build();  // Retorna uma resposta 204 No Content
    }



}
