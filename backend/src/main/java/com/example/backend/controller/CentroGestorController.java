package com.example.backend.controller;

import com.example.backend.dto.DepProjection;
import com.example.backend.sqlserver2.repository.CentroGestor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/centrogestor")
public class CentroGestorController {
    
    @Autowired
    private CentroGestor centrogestor;

    @GetMapping("/percod/{percod}/ent/{ent}/eje/{eje}")
    public ResponseEntity<?> getcentrogestor(
        @PathVariable String percod,
        @PathVariable Integer ent,
        @PathVariable String eje
    ) {
        try {
            List<DepProjection> centros = centrogestor.findDepartmentsByUserAndEntity(percod, ent, eje);
            if (centros.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontraron centro Gestor para ese datos.");
            }

            return ResponseEntity.ok(centros);
        } catch(DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Error: " + ex.getMostSpecificCause().getMessage());
        }
    }
}
