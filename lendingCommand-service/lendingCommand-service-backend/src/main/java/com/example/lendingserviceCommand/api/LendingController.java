package com.example.lendingserviceCommand.api;

import com.example.lendingserviceCommand.dto.CreateLendingDTO;
import com.example.lendingserviceCommand.model.Lending;
import com.example.lendingserviceCommand.repositories.LendingRepository;
import com.example.lendingserviceCommand.service.EditLendingRequest;
import com.example.lendingserviceCommand.service.LendingService;
import com.example.lendingserviceCommand.service.LendingServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "Creates a new lending using CreateLendingDTO")
    @PostMapping
    public ResponseEntity<LendingView> create(@RequestBody @Valid CreateLendingDTO createLendingDTO) {
        // 1) Chama o service passando o DTO
        final Lending lending = service.create(createLendingDTO);
        // 2) Converte para LendingView e retorna
        return ResponseEntity.ok(lendingViewMapper.toLendingView(lending));
    }

    @Operation(summary = "Partially updates an existing lending")
    @PatchMapping(value = "/{id1}/{id2}")
    public ResponseEntity<LendingView> partialUpdate(
            @PathVariable("id1") final int id1,
            @PathVariable("id2") final int id2,
            @RequestBody @Valid final EditLendingRequest resource
    ) {
        final var lending = service.partialUpdate(id1, id2, resource, 1L);
        return ResponseEntity.ok().body(lendingViewMapper.toLendingView(lending));
    }

    @PostMapping("/sync")
    public ResponseEntity<Lending> createLendingSync(@RequestBody Lending lending) {
        // Lógica existente que lida com sincronização manual/forçada
        Optional<Lending> existingLending = lendingRepository.findByLendingID(lending.getLendingID());
        if (existingLending.isPresent()) {
            Lending existing = existingLending.get();
            existing.setBookID(lending.getBookID());
            existing.setReaderID(lending.getReaderID());
            existing.setStartDate(lending.getStartDate());
            existing.setExpectedReturnDate(lending.getExpectedReturnDate());
            existing.setReturnDate(lending.getReturnDate());
            existing.setOverdue(lending.isOverdue());
            existing.setFine(lending.getFine());
            existing.setNotes(lending.getNotes());
            existing.setVersion(lending.getVersion());
            lendingRepository.save(existing);
            return ResponseEntity.ok(existing);
        } else {
            Lending savedLending = lendingRepository.save(lending);
            return ResponseEntity.ok(savedLending);
        }
    }
}
