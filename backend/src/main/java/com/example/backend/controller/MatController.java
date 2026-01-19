package com.example.backend.controller;

import com.example.backend.dto.MatShortDto;
import com.example.backend.sqlserver2.model.Mat;
import com.example.backend.sqlserver2.model.Mta;
import com.example.backend.sqlserver2.repository.MatRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mat")
public class MatController {
    @Autowired
    private MatRepository matRepository;

    //selecting name for almacen
    @GetMapping("/fetch-almacen/{ent}/{depcod}")
    public ResponseEntity<?> fetchAlmacen(
        @PathVariable Integer ent,
        @PathVariable String depcod
    ) {
        try {
            List<Mat> matRecords = matRepository.findByENT(ent);
            
            Set<MatShortDto> distinctMta = matRecords.stream()
                .filter(mat -> mat.getMag() != null && depcod.equals(mat.getMag().getDEPCOD()))
                .map(mat -> {
                    Mta mta = mat.getMta();
                    return mta != null 
                        ? new MatShortDto(mta.getMTACOD(), mta.getMTADES()) 
                        : null;
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toCollection(LinkedHashSet::new));

            if (distinctMta.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No resultado");
            }

            return ResponseEntity.ok(new ArrayList<>(distinctMta));
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + ex.getMostSpecificCause().getMessage());
        }
    }
}
