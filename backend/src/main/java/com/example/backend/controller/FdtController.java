package com.example.backend.controller;

import com.example.backend.dto.FdtResumeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.backend.sqlserver2.repository.FdtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fdt")
public class FdtController {

    private static final Logger log = LoggerFactory.getLogger(FdtController.class);

    @Autowired
    private FdtRepository fdtController;

    @GetMapping("/{ent}/{eje}/{facnum}")
    public ResponseEntity<?> getFde(
            @PathVariable Integer ent,
            @PathVariable String eje,
            @PathVariable String facnum) {
        try {
            List<FdtResumeDto> data = fdtController.findByFdt(ent, eje, facnum);
            if (data.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontraron descuentos");
            }
            return ResponseEntity.ok(data);
        } catch (DataAccessException ex) {
            log.error("Error al obtener el resumen Fdt para ent={}, eje={}, facnum={}", ent, eje, facnum, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("No se pudo recuperar el resumen de descuentos: " + ex.getMostSpecificCause().getMessage());
        }
    }
}