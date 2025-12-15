package com.example.backend.controller;

import com.example.backend.sqlserver2.model.Dep;
import com.example.backend.sqlserver2.repository.DepRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.dao.DataAccessException;

import java.util.List;

@RestController
@RequestMapping("/api/dep")
public class DepController {
    @Autowired
    private DepRepository depRepository;

    //fetching all services
    @GetMapping("/fetch-services/{ent}/{eje}")
        public ResponseEntity<?> fetchAllservices(
        @PathVariable Integer ent,
        @PathVariable String eje
    ) {
        try {
            List<Dep> services = depRepository.findByENTAndEJE(ent, eje);
            if (services.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontraron servicios para los siguientes entidad y ejercicio.");
            }
            return ResponseEntity.ok(services);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al consultar servicios: " + ex.getMostSpecificCause().getMessage());
        }
    }
}
