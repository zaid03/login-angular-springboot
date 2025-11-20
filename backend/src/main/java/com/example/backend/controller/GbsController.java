package com.example.backend.controller;

import com.example.backend.sqlserver2.repository.GbsProjection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.backend.sqlserver2.repository.GbsRepository;
import com.example.backend.dto.UpdateBolsaRequest;



@RestController
@RequestMapping("/api/gbs")
public class GbsController {
    @Autowired
    private GbsRepository gbsRepository;

    //for the main list
    @GetMapping("/{ent}/{eje}/{cgecod}")
    public ResponseEntity<List<GbsProjection>> getBolsas(
        @PathVariable Integer ent,
        @PathVariable String eje,
        @PathVariable String cgecod) {
            List<GbsProjection> result = gbsRepository.getBolsas(ent, eje, cgecod);
            return ResponseEntity.ok(result);
        }

    //modifying a bolsa
    @PatchMapping("/{ent}/{eje}/{cgecod}/{gbsref}")
    public ResponseEntity<?> updateBolsa(
            @PathVariable Integer ent,
            @PathVariable String eje,
            @PathVariable String cgecod,
            @PathVariable String gbsref,
            @RequestBody UpdateBolsaRequest payload) {
        try {
            int updated = gbsRepository.updateABolsa(
                    payload.gbsimp(),
                    payload.gbsius(),
                    payload.gbseco(),
                    payload.gbsfop() != null ? payload.gbsfop().atStartOfDay() : null,
                    ent,
                    eje,
                    cgecod,
                    gbsref);

            if (updated == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No bolsa found for the provided identifiers.");
            }
            return ResponseEntity.noContent().build();
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Update failed: " + ex.getMostSpecificCause().getMessage());
        }
    }
}
