package com.example.backend.controller;

import com.example.backend.dto.AlbResumeDto;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.backend.sqlserver2.model.Alb;
import com.example.backend.sqlserver2.repository.AlbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
}
