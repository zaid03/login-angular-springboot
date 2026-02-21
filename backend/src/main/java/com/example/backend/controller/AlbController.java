package com.example.backend.controller;

import com.example.backend.dto.AlbResumeDto;
import com.example.backend.dto.albFacturaDto;

import org.springframework.dao.DataAccessException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.backend.sqlserver2.model.Alb;
import com.example.backend.sqlserver2.repository.AlbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/alb")
public class AlbController {
    @Autowired
    private AlbRepository albRepository;

    //fetch albaranes for facturas
    @GetMapping("/albaranes/{ent}/{eje}/{facnum}")
    public ResponseEntity<?> getAlbaranesByFactura(
        @PathVariable Integer ent,
        @PathVariable String eje,
        @PathVariable Integer facnum
    ) {
        try {
            List<Alb> albaranes = albRepository.findByENTAndEJEAndFACNUM(ent, eje, facnum);
            
            if(albaranes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Sin resultado");
            }

            List<AlbResumeDto> result = albaranes.stream()
                .map(a -> new AlbResumeDto(
                    a.getALBNUM(),
                    a.getALBREF(),
                    a.getALBDAT(),
                    a.getALBBIM(),
                    a.getSOLNUM(),
                    a.getSOLSUB(),
                    a.getALBOBS()
                ))
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(result);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Error: " + ex.getMostSpecificCause().getMessage());
        }
    }

    //fetching albaranes for adding to a factura
    @GetMapping("/albaranes-factura/{ent}/{tercod}/{eje}/{cgecod}")
    public ResponseEntity<?> fetchAlbaranesByServices(
        @PathVariable Integer ent,
        @PathVariable Integer tercod,
        @PathVariable String eje,
        @PathVariable String cgecod
    ) {
        try {
            List<albFacturaDto> albaranes = albRepository.findByENTAndTERCODAndDep_EJEAndDep_CGECOD(ent, tercod, eje, cgecod);
            if (albaranes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sin resultado");
            }

            return ResponseEntity.ok(albaranes);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + ex.getMostSpecificCause().getMessage());
        }
    }

    //searching in albaranes for adding to a factura
    @GetMapping("/search-albaranes-Desde/{ent}/{tercod}/{albdat}/{eje}/{cgecod}")
    public ResponseEntity<?> searchAlbaranesByDesde(
        @PathVariable Integer ent,
        @PathVariable Integer tercod,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @PathVariable LocalDateTime albdat,
        @PathVariable String eje,
        @PathVariable String cgecod
    ) {
        try {
            List<albFacturaDto> albaranes = albRepository.findByENTAndTERCODAndALBDATGreaterThanEqualAndDep_EJEAndDep_CGECOD(ent, tercod, albdat, eje, cgecod);
            if (albaranes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sin resultado");
            }

            return ResponseEntity.ok(albaranes);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + ex.getMostSpecificCause().getMessage());
        }
    }

    @GetMapping("/search-albaranes-Hasta/{ent}/{tercod}/{albdat}/{eje}/{cgecod}")
    public ResponseEntity<?> searchAlbaranesByHasta(
        @PathVariable Integer ent,
        @PathVariable Integer tercod,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @PathVariable LocalDateTime albdat,
        @PathVariable String eje,
        @PathVariable String cgecod
    ) {
        try {
            List<albFacturaDto> albaranes = albRepository.findByENTAndTERCODAndALBDATLessThanEqualAndDep_EJEAndDep_CGECOD(ent, tercod, albdat, eje, cgecod);
            if (albaranes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sin resultado");
            }

            return ResponseEntity.ok(albaranes);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + ex.getMostSpecificCause().getMessage());
        }
    }
}
