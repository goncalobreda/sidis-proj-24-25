package com.example.acquisitionserviceCommand.api;

import com.example.acquisitionserviceCommand.dto.CreateAcquisitionDTO;
import com.example.acquisitionserviceCommand.model.Acquisition;
import com.example.acquisitionserviceCommand.service.AcquisitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/acquisitions")
@Tag(name = "Acquisitions", description = "Endpoints para gestão de aquisições de livros")
class AcquisitionController {


    private final AcquisitionService acquisitionService;

    @Autowired
    public AcquisitionController(AcquisitionService acquisitionService) {
        this.acquisitionService = acquisitionService;
    }

    @Operation(summary = "Cria uma nova sugestão de aquisição de livro")
    @PostMapping
    public ResponseEntity<Acquisition> createAcquisition(@RequestBody @Valid CreateAcquisitionDTO dto) {
        Acquisition createdAcquisition = acquisitionService.createAcquisition(dto);
        return ResponseEntity.ok(createdAcquisition);
    }

    @Operation(summary = "Aprova uma aquisição de livro pelo ID")
    @PatchMapping("/{id1}/{id2}/approve")
    public ResponseEntity<Acquisition> approveAcquisition(
            @PathVariable("id1") String id1,
            @PathVariable("id2") String id2) {
        String acquisitionID = id1 + "/" + id2;

        Acquisition approvedAcquisition = acquisitionService.approveAcquisition(acquisitionID);
        return ResponseEntity.ok(approvedAcquisition);
    }

    @Operation(summary = "Rejeita uma aquisição de livro pelo ID")
    @PatchMapping("/{id1}/{id2}/reject")
    public ResponseEntity<Acquisition> rejectAcquisition(
            @PathVariable("id1") String id1,
            @PathVariable("id2") String id2,
            @RequestParam String reason) {
        String acquisitionID = id1 + "/" + id2;

        Acquisition rejectedAcquisition = acquisitionService.rejectAcquisition(acquisitionID, reason);
        return ResponseEntity.ok(rejectedAcquisition);
    }

}
