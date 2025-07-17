package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import com.example.backend.sqlserver2.repository.CfgRepository;

import java.util.List;

@RestController
@RequestMapping("/api/cfg")
public class CfgController {
    
    @Autowired
    private CfgRepository cfgRepository;

    //method to ejercicio in Cfg table by entidad and CFGEST
    @GetMapping("/by-ent/{ent}")
    public ResponseEntity<?> getEJE(@PathVariable int ent) {
        try {
            if (ent <= 0) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "ENT debe ser un número positivo"));
            }

            List<String> results = cfgRepository.findEjeByEntAndCfgest(ent);
            
            if (results.isEmpty()) {
                return ResponseEntity.ok()
                    .body(Map.of("message", "No se encontraron ejercicios para esto entidad: " + ent, "data", results));
            }
            
            return ResponseEntity.ok(results);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Database error: " + e.getMessage()));
        }
    }
}
