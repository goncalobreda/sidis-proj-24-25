package com.example.acquisitionserviceQuery.controller;

import com.example.acquisitionserviceQuery.model.Acquisition;
import com.example.acquisitionserviceQuery.service.AcquisitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/acquisitions")
@Tag(name = "Acquisitions", description = "Endpoints para consulta de aquisições")
public class AcquisitionController {

    private final AcquisitionService acquisitionService;

    public AcquisitionController(AcquisitionService acquisitionService) {
        this.acquisitionService = acquisitionService;
    }

    @Operation(summary = "Obter todas as aquisições")
    @GetMapping
    public ResponseEntity<List<Acquisition>> getAllAcquisitions() {
        List<Acquisition> acquisitions = acquisitionService.findAllAcquisitions();
        return ResponseEntity.ok(acquisitions);
    }

    @Operation(summary = "Obter aquisições pendentes")
    @GetMapping("/pending")
    public ResponseEntity<List<Acquisition>> getPendingAcquisitions() {
        List<Acquisition> pendingAcquisitions = acquisitionService.findPendingAcquisitions();
        return ResponseEntity.ok(pendingAcquisitions);
    }
}
