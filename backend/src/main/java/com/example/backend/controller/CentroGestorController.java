package com.example.backend.controller;

import com.example.backend.sqlserver2.repository.CentroGestor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/centrogestor")
public class CentroGestorController {
    
    @Autowired
    private CentroGestor centrogestor;

    @GetMapping("/percod/{percod}/ent/{ent}/eje/{eje}")
    public List<Object[]> getcentrogestor(
        @PathVariable String percod,
        @PathVariable Integer ent,
        @PathVariable String eje) {
            return centrogestor.findDepartmentsByUserAndEntity(percod, ent, eje);
        }
}
