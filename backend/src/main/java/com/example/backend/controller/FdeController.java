package com.example.backend.controller;

import com.example.backend.dto.FdeResumeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.backend.sqlserver2.repository.FdeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fde")
public class FdeController {

    private static final Logger log = LoggerFactory.getLogger(FdeController.class);

    @Autowired
    private FdeRepository fdeRepository;

    @GetMapping("/{ent}/{eje}/{facnum}")
    public ResponseEntity<?> getFde(
            @PathVariable Integer ent,
            @PathVariable String eje,
            @PathVariable String facnum) {
        try {
            List<FdeResumeDto> data = fdeRepository.findByFactura(ent, eje, facnum);
            if (data.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontraron aplicaciones");
            }
            return ResponseEntity.ok(data);
        } catch (DataAccessException ex) {
            log.error("Error al obtener el resumen FDE para ent={}, eje={}, facnum={}", ent, eje, facnum, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("No se pudo recuperar el resumen de FDE: " + ex.getMostSpecificCause().getMessage());
        }
    }
}
