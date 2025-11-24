package com.example.backend.controller;

import com.example.backend.dto.AlbResumeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.example.backend.sqlserver2.repository.AlbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alb")
public class AlbController {

    private static final Logger log = LoggerFactory.getLogger(AlbController.class);

    @Autowired
    private AlbRepository albRepository;

    @GetMapping("/{ent}/{eje}/{facnum}")
    public ResponseEntity<?> getAlbResumen(
            @PathVariable Integer ent,
            @PathVariable String eje,
            @PathVariable String facnum) {
        try {
            List<AlbResumeDto> data = albRepository.findResumenByFactura(ent, eje, facnum);
            if (data.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontraron albaranes");
            }
            return ResponseEntity.ok(data);
        } catch (DataAccessException ex) {
            log.error("Error al obtener el resumen ALB para ent={}, eje={}, facnum={}", ent, eje, facnum, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("No se pudo recuperar el resumen de ALB: " + ex.getMostSpecificCause().getMessage());
        }
    }
}
