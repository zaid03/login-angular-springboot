package com.example.backend.controller;

import com.example.backend.dto.MagShortDto;
import com.example.backend.sqlserver2.repository.MagRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.dao.DataAccessException;

import java.util.Optional;

@RestController
@RequestMapping("/api/mag")
public class MagController {
    @Autowired
    private MagRepository magRepository;

    //selecting name for almacen
    @GetMapping("/fetch-almacen-nombre/{ent}/{depcod}")
    public ResponseEntity<?> fetchAlmacenName(
        @PathVariable Integer ent,
        @PathVariable String depcod
    ) {
        try {
            Optional<MagShortDto> name = magRepository.findShortByEntAndDepcod(ent, depcod);
            if (name.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sin resultado");
            }
            return ResponseEntity.ok(name);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + ex.getMostSpecificCause().getMessage());
        }
    }
}
