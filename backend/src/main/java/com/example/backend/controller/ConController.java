package com.example.backend.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.backend.dto.ContratoDto;
import com.example.backend.service.CotContratoProjection;
import com.example.backend.sqlserver2.repository.CotRepository;

@RestController
@RequestMapping("/api/con")
public class ConController {
    @Autowired
    private CotRepository cotRepository;

    @GetMapping("/fetch-contratos")
    public ResponseEntity<?> fetchContratos() {
        try {
            List<CotContratoProjection> rows = cotRepository.findAllProjectedBy();
            if (rows == null || rows.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sin resultado");
            }

            List<ContratoDto> dto = rows.stream().map(p -> {
                CotContratoProjection.ConInfo c = p.getCon();
                CotContratoProjection.TerInfo t = p.getTer();
                return new ContratoDto(
                    c.getCONCOD(),
                    c.getCONLOT(),
                    c.getCONDES(),
                    c.getCONFIN(),
                    c.getCONFFI(),
                    c.getCONBLO(),
                    t.getTERCOD(),
                    t.getTERNOM()
                );
            }).collect(Collectors.toList());

            return ResponseEntity.ok(dto);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + ex.getMostSpecificCause().getMessage());
        }
    }
}