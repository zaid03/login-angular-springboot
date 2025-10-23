package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import com.example.backend.sqlserver2.repository.FacRepository;

import java.util.List;

@RestController
@RequestMapping("/api/fac")
public class FacController {
    @Autowired
    private FacRepository facRepository;

    //for the main list
    @GetMapping("/{ent}/{eje}")
    public ResponseEntity<?> getFacturas(
        @PathVariable Integer ent,
        @PathVariable Integer eje) 
    {
        return ResponseEntity.ok().body(facRepository.findByENTAndEJE(ent, eje));
    }
}
