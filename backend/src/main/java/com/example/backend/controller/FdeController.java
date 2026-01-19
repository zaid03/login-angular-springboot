package com.example.backend.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.backend.sqlserver2.model.Fde;
import com.example.backend.sqlserver2.repository.FdeRepository;
import com.example.backend.dto.FdeResumeDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/fde")
public class FdeController {
    @Autowired
    private FdeRepository fdeRepository;

    @GetMapping("/{ent}/{eje}/{facnum}")
    public ResponseEntity<?> getFde(
            @PathVariable Integer ent,
            @PathVariable String eje,
            @PathVariable Integer facnum
    ) {
        try {
            List<Fde> detalles = fdeRepository.findByENTAndEJEAndFACNUM(ent, eje, facnum);
        
            if(detalles.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró ningún applicacione");
            }

            List<FdeResumeDto> result = detalles.stream()
                .map(fd -> new FdeResumeDto(
                    fd.getFDEREF(),
                    fd.getFDEECO(),
                    fd.getFDEIMP(),
                    fd.getFDEDIF()
                ))
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(result);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Error: " + ex.getMostSpecificCause().getMessage());
        }
    }
}
