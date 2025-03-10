package com.example.lendingserviceQuery.api;

import com.example.lendingserviceQuery.model.Lending;
import com.example.lendingserviceQuery.repositories.LendingRepository;
import com.example.lendingserviceQuery.service.LendingService;
import com.example.lendingserviceQuery.exceptions.NotFoundException;
import com.example.lendingserviceQuery.service.LendingServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
        return ResponseEntity.ok(lendingViewMapper.toLendingView(lending));
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



}
