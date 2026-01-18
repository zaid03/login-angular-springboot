package com.example.backend.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.backend.service.FacSpecification;
import com.example.backend.sqlserver2.model.Fac;
import com.example.backend.sqlserver2.model.Ter;
import com.example.backend.sqlserver2.repository.FacRepository;
import com.example.backend.sqlserver2.repository.TerRepository;
import com.example.backend.dto.FacWithTerDto;

@RestController
@RequestMapping("/api/fac")
public class FacController {
    @Autowired
    private FacRepository facRepository;
    @Autowired
    private TerRepository terRepository;

    //for the main list
    @GetMapping("/{ent}/{eje}/{cgecod}")
    public ResponseEntity<?> getFacturas(
        @PathVariable Integer ent,
        @PathVariable String eje,
        @PathVariable String cgecod
    ) {
        try {
            List<Fac> facturas = facRepository.findByENTAndEJEAndCGECODOrderByFACFREAsc(ent, eje, cgecod);
            
            List<FacWithTerDto> result = facturas.stream().map(f -> {
                Optional<Ter> terOpt = terRepository.findByENTAndTERCOD(f.getENT(), f.getTERCOD());
                Ter ter = terOpt.orElse(null);
                
                return new FacWithTerDto(
                    f.getENT(), 
                    f.getEJE(), 
                    f.getFACNUM(), 
                    f.getTERCOD(), 
                    f.getCGECOD(), 
                    f.getFACOBS(), 
                    f.getFACIMP(), 
                    f.getFACIEC(), 
                    f.getFACIDI(), 
                    f.getFACTDC(), 
                    f.getFACANN() != null ? String.valueOf(f.getFACANN()) : null, 
                    f.getFACFAC() != null ? String.valueOf(f.getFACFAC()) : null, 
                    f.getFACDOC(), 
                    f.getFACDAT(), 
                    f.getFACFCO(), 
                    f.getFACADO(), 
                    f.getFACTXT(), 
                    f.getFACFRE(), 
                    f.getCONCTP(), 
                    f.getCONCPR(), 
                    f.getCONCCR(), 
                    f.getFACOCT(), 
                    f.getFACFPG(), 
                    f.getFACOPG(),
                    f.getFACTPG(), 
                    f.getFACDTO(), 
                    ter != null ? ter.getTERNOM() : null, 
                    ter != null ? ter.getTERNIF() : null
                );
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(result);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Error: " + ex.getMostSpecificCause().getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchFacturas(
        @RequestParam Integer ent,
        @RequestParam String eje,
        @RequestParam String cgecod,
        @RequestParam(defaultValue = "TODAS") String estado,
        @RequestParam(defaultValue = "REGISTRO") String dateType,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
        @RequestParam(defaultValue = "ANY") String facannMode,
        @RequestParam(required = false) String facann,
        @RequestParam(required = false) String search,
        @RequestParam(defaultValue = "OTROS") String searchType
    ) {
        try {
            Specification<Fac> spec = FacSpecification.searchFacturas(
                ent, eje, cgecod, estado, dateType, fromDate, toDate,
                facannMode, facann, search, searchType
            );
            
            List<Fac> facturas = facRepository.findAll(spec);
            
            List<FacWithTerDto> result = facturas.stream().map(f -> {
                Ter ter = f.getTer();
                return new FacWithTerDto(
                    f.getENT(), f.getEJE(), f.getFACNUM(), f.getTERCOD(), f.getCGECOD(),
                    f.getFACOBS(), f.getFACIMP(), f.getFACIEC(), f.getFACIDI(), f.getFACTDC(),
                    f.getFACANN() != null ? String.valueOf(f.getFACANN()) : null,
                    f.getFACFAC() != null ? String.valueOf(f.getFACFAC()) : null,
                    f.getFACDOC(), f.getFACDAT(), f.getFACFCO(), f.getFACADO(),
                    f.getFACTXT(), f.getFACFRE(), f.getCONCTP(), f.getCONCPR(),
                    f.getCONCCR(), f.getFACOCT(), f.getFACFPG(), f.getFACOPG(),
                    f.getFACTPG(), f.getFACDTO(),
                    ter != null ? ter.getTERNOM() : null,
                    ter != null ? ter.getTERNIF() : null
                );
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(result);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Error: " + ex.getMessage());
        }
    }
}