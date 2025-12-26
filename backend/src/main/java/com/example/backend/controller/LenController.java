package com.example.backend.controller;

import com.example.backend.sqlserver2.model.Len;
import com.example.backend.sqlserver2.repository.LenRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.dao.DataAccessException;

import java.util.List;

@RestController
@RequestMapping("/api/Len")
public class LenController {
    @Autowired
    private LenRepository lenRepository;

    //selecting all lugares de entrega
    @GetMapping("/fetch-all")
    public ResponseEntity<?> fetchLugares(
    ) {
        try {
            List<?> lugares = lenRepository.findAll();
            if (lugares.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontraron lugares de entregas");
            }
            return ResponseEntity.ok(lugares);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + ex.getMostSpecificCause().getMessage());
        }
    }

    //adding a lugar de entrega
    @PostMapping("/add-lugar")
    public ResponseEntity<?> addLugar(
        @RequestBody Len newLugar
    ) {
        try {
            Integer maxLencod = lenRepository.findMaxLencod();
            int nextLencod = (maxLencod == null ? 1 : maxLencod + 1);

            newLugar.setLENCOD(nextLencod);
            lenRepository.save(newLugar);

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + ex.getMostSpecificCause().getMessage());
        }
    }

    //update a lugar de entrega
    public record lugarUpdate(String LENDES, String LENTXT) {}

    @PatchMapping("/update-lugar/{LENCOD}")
    public ResponseEntity<?> updateLugar(
        @PathVariable Integer LENCOD,
        @RequestBody lugarUpdate payload
    ) {
        try {
            if (payload == null || payload.LENDES() == null || payload.LENTXT() == null) {
                return ResponseEntity.badRequest().body("Faltan datos obligatorios.");
            }

            int updated = lenRepository.updateLugar(
                payload.LENDES(), 
                payload.LENTXT(), 
                LENCOD
            );

            if(updated == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró ninguna lugares de entrega para los datos.");
            }
            return ResponseEntity.noContent().build();
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Update failed: " + ex.getMostSpecificCause().getMessage());
        }
    }

    //delete a lugare de entrega
    @DeleteMapping("/delete-lugar/{LENCOD}")
    public ResponseEntity<?> deleteLugar(
        @PathVariable Integer LENCOD
    ) {
        try {
            if (!lenRepository.existsById(LENCOD)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No se encontró el lugar de entrega para eliminar.");
            }
            lenRepository.deleteById(LENCOD);
            return ResponseEntity.noContent().build();
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("delete failed: " + ex.getMostSpecificCause().getMessage());
        }
    }

    //search with lencod
    @GetMapping("/filter-lencod/{LENCOD}")
    public ResponseEntity<?> filterLugaresLen(
        @PathVariable Integer LENCOD
    ) {
        try {
            List<?> lugares = lenRepository.filterByLencod(LENCOD);
            if (lugares.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontraron lugares de entregas");
            }
            return ResponseEntity.ok(lugares);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("filter failed: " + ex.getMostSpecificCause().getMessage());
        }
    }

    //search with lendes
    @GetMapping("/filter-lendes/{LENDES}")
    public ResponseEntity<?> filterLugaresDes(
        @PathVariable String LENDES
    ) {
        try {
            List<?> lugares = lenRepository.filterByLendes(LENDES);
            if (lugares.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontraron lugares de entregas");
            }
            return ResponseEntity.ok(lugares);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("filter failed: " + ex.getMostSpecificCause().getMessage());
        }
    }
}