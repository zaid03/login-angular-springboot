package com.example.backend.controller;

import com.example.backend.sqlserver1.repository.AytRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ayt")
public class AytController {
    @Autowired
    private AytRepository aytRepository;

    //to fetch ws parameters
    @GetMapping("/fetch-all/{ENT}")
    public ResponseEntity<?> fetchAytWs(
        @PathVariable Integer ENT
    ) {
        try {
            List<?> services = aytRepository.findByENTCOD(ENT);
            return ResponseEntity.ok(services);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Error : " + ex.getMostSpecificCause().getMessage());
        }
    }
}
