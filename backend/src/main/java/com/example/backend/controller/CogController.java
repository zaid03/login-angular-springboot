package com.example.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.backend.dto.CogCgeProjection;
import com.example.backend.sqlserver2.repository.CogRepository;
// import com.example.backend.sqlserver2.model.Cog;
// import com.example.backend.sqlserver2.model.CogId;

@RestController
@RequestMapping("/api/cog")
public class CogController {
    @Autowired
    private CogRepository cogRepository;

    //selecting centro gestores for contrato
    @GetMapping("/fetch-centros/{ent}/{eje}/{concod}")
    public ResponseEntity<?> fetchCentroGestores(
        @PathVariable Integer ent,
        @PathVariable String eje,
        @PathVariable Integer concod
    ) {
        try {
            List<CogCgeProjection> centros = cogRepository.findAllByENTAndEJEAndCONCOD(ent, eje, concod);
            if (centros.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sin resultado");
            }

            return ResponseEntity.ok(centros);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + ex.getMostSpecificCause().getMessage());
        }
    }
}
