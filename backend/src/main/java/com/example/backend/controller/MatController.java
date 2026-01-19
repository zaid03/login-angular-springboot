package com.example.backend.controller;

import com.example.backend.dto.MatShortDto;
import com.example.backend.sqlserver2.repository.MatRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.dao.DataAccessException;

import java.util.List;

@RestController
@RequestMapping("/api/mat")
public class MatController {
    @Autowired
    private MatRepository matRepository;

    //selecting name for almacen
    @GetMapping("/fetch-almacen/{ent}/{depcod}")
    public ResponseEntity<?> fetchAlmacen(
        @PathVariable Integer ent,
        @PathVariable String depcod
    ) {
        try {
            List<MatShortDto> almacenes = matRepository.findDistinctMtaByEntAndDepcod(ent, depcod);
            if (almacenes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sin resultado");
            }
            return ResponseEntity.ok(almacenes);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + ex.getMostSpecificCause().getMessage());
        }
    }
}
