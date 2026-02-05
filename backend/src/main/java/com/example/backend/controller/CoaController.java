package com.example.backend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.backend.dto.CoaArtProjection;
import com.example.backend.sqlserver2.repository.CoaRepository;
import com.example.backend.sqlserver2.model.Coa;
import com.example.backend.sqlserver2.model.CoaId;

@RestController
@RequestMapping("/api/coa")
public class CoaController {
    @Autowired
    private CoaRepository coaRepository;

    //selecting articulos for a contrato
    @GetMapping("/fetch-articulos/{ent}/{eje}/{concod}")
    public ResponseEntity<?> fetchArticulos(
        @PathVariable Integer ent,
        @PathVariable String eje,
        @PathVariable Integer concod
    ) {
        try {
            List<CoaArtProjection> articulos = coaRepository.findAllByENTAndEJEAndConnCONCOD(ent, eje, concod);
            if (articulos.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sin resultado");
            }

            return ResponseEntity.ok(articulos);

        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + ex.getMostSpecificCause().getMessage());
        }
    }

    //update articulo
    public record artUpdate(Double COAPRE) {};

    @PatchMapping("/update-articulo/{ent}/{eje}/{concod}/{afacod}/{asucod}/{artcod}")
    public ResponseEntity<?> updateArticulo(
        @PathVariable Integer ent,
        @PathVariable String eje,
        @PathVariable Integer concod,
        @PathVariable String afacod,
        @PathVariable String asucod,
        @PathVariable String artcod,
        @RequestBody artUpdate payload
    ) {
        try {
            if(payload == null || payload.COAPRE() == null) {
                return ResponseEntity.badRequest().body("Faltan datos obligatorios.");
            }

            CoaId id = new CoaId(ent, eje, concod, afacod, asucod, artcod);
            Optional<Coa> articulo = coaRepository.findById(id);

            Coa coaUpdate = articulo.get();
            coaUpdate.setCOAPRE(payload.COAPRE());
            coaRepository.save(coaUpdate);

            return ResponseEntity.noContent().build();
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + ex.getMostSpecificCause().getMessage());
        }
    }
}