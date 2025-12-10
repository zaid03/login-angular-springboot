package com.example.backend.controller;

import com.example.backend.sqlserver2.model.Cge;
import com.example.backend.sqlserver2.repository.CgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.dao.DataAccessException;

import java.util.List;

@RestController
@RequestMapping("/api/cge")
public class CgeController {
    @Autowired
    private CgeRepository cgeRepository;

    @GetMapping("/fetch-all/{ent}/{eje}")
    public ResponseEntity<?> fetchAllCentroGestores(
        @PathVariable Integer ent,
        @PathVariable String eje
    ) {
        try {
            List<Cge> centros = cgeRepository.findByENTAndEJE(ent, eje);
            if (centros.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontraron centros para ese ENT/EJE.");
            }
            return ResponseEntity.ok(centros);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al consultar centros: " + ex.getMostSpecificCause().getMessage());
        }
    }
}