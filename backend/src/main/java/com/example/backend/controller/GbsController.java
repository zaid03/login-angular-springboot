package com.example.backend.controller;

import com.example.backend.sqlserver2.repository.GbsProjection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.backend.sqlserver2.repository.GbsRepository;


@RestController
@RequestMapping("/api/gbs")
public class GbsController {
    @Autowired
    private GbsRepository gbsRepository;

    //for the main list
    @GetMapping("/{ent}/{eje}/{cgecod}")
    public ResponseEntity<List<GbsProjection>> getBolsas(
        @PathVariable Integer ent,
        @PathVariable String eje,
        @PathVariable String cgecod) {
            List<GbsProjection> result = gbsRepository.getBolsas(ent, eje, cgecod);
            return ResponseEntity.ok(result);
        }
}
