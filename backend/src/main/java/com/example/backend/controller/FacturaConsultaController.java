package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.FacturaConsultaRequestDto;
import com.example.backend.service.FacturaConsultaService;

@RestController
@RequestMapping("/api/facturas")
public class FacturaConsultaController {
    @Autowired
    private FacturaConsultaService facturaConsultaService;

    @PostMapping("/consulta")
    public ResponseEntity<?> consultaFacturas(@RequestBody FacturaConsultaRequestDto request) {
        try {
            String smlInput = facturaConsultaService.buildSmlInput(request);
            String smlResponse = facturaConsultaService.sendSmlRequest(smlInput, request.getWebserviceUrl());

            if(smlResponse.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sin resultado");
            }
            return ResponseEntity.ok(smlResponse);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + ex.getMessage());
        }
    }
}
