package com.example.backend.controller;

import com.example.backend.sqlserver2.model.Per;
import com.example.backend.sqlserver2.repository.PerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/Per")
public class PerController {
    @Autowired
    private PerRepository perRepository;

    //for selecting all personas
    @GetMapping("/fetch-all")
    public ResponseEntity<?> fetchPersona(
    ) {
        try {
            List<Per> personas = perRepository.findAll();
            if(personas.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontraron personas");
            }
            return ResponseEntity.ok(personas);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + ex.getMostSpecificCause().getMessage());
        }
    }

    //for search in personas first case
    @GetMapping("/search-cod-nom/{search}")
    public ResponseEntity<?> searchPersonaFirst(
        @PathVariable String search
    ) {
        try {
            List<Per> personas = perRepository.findByPERCODOrPERNOMContaining(search, search);
            if(personas.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontraron personas");
            }
            return ResponseEntity.ok(personas);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + ex.getMostSpecificCause().getMessage());
        }
    }

    //for search in personas second case
    @GetMapping("/search-nom/{search}")
    public ResponseEntity<?> searchPersonaSecond(
        @PathVariable String search
    ) {
        try {
            List<Per> personas = perRepository.findByPERNOMContaining(search);
            if(personas.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontraron personas");
            }
            return ResponseEntity.ok(personas);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + ex.getMostSpecificCause().getMessage());
        }
    }
}