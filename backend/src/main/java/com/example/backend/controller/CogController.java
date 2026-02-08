package com.example.backend.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.backend.dto.COGAIPOnlyDto;
import com.example.backend.dto.CogCgeProjection;
import com.example.backend.dto.CogSaveDto;
import com.example.backend.sqlserver2.repository.CogRepository;
import com.example.backend.sqlserver2.model.Cog;
import com.example.backend.sqlserver2.model.CogId;

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

    //deleting a centro gestor from contrato
    @DeleteMapping("/delete-centro/{ent}/{eje}/{concod}/{cgecod}")
    public ResponseEntity<?> deleteCentroGestore(
        @PathVariable Integer ent,
        @PathVariable String eje,
        @PathVariable Integer concod,
        @PathVariable String cgecod
    ) {
        try {
            Optional<COGAIPOnlyDto> centro =  cogRepository.findByENTAndEJEAndCONCODAndCGECOD(ent, eje, concod, cgecod);

            if (centro.isPresent()) {
                Double cogaip = centro.get().getCOGAIP();
                if (cogaip != null && cogaip > 0) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se puede quitar un centro gestor donde ya hay pedidos");
                } else if (cogaip != null && cogaip == 0) {
                    CogId id = new CogId(ent, eje, concod, cgecod);
                    cogRepository.deleteById(id);

                    return ResponseEntity.noContent().build();
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sin resultado");
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sin resultado");
            }
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + ex.getMostSpecificCause().getMessage());
        }
    }

    //saving centro gestores to a contrato
    @PostMapping("/save-centroGestores")
    public ResponseEntity<?> saveCentros(
        @RequestBody List<CogSaveDto> items
    ) {
        try {
            List<Cog> toSave = new ArrayList<>();
            for (CogSaveDto dto: items) {
                boolean exists = cogRepository.existsByENTAndEJEAndCONCODAndCGECOD(dto.ent, dto.eje, dto.concod, dto.cgecod);
                if (!exists) {
                    Cog c = new Cog();
                    c.setENT(dto.ent);
                    c.setEJE(dto.eje);
                    c.setCONCOD(dto.concod);
                    c.setCGECOD(dto.cgecod);
                    c.setCOGIMP(dto.cogimp);
                    c.setCOGAIP(dto.cogaip);
                    toSave.add(c);
                }
            }
            cogRepository.saveAll(toSave);
            return ResponseEntity.noContent().build();
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + ex.getMostSpecificCause().getMessage());
        }
    }
}
