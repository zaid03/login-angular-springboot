package com.example.backend.controller;

import com.example.backend.dto.DepCodDesDto;
import com.example.backend.sqlserver2.model.DpeId;
import com.example.backend.sqlserver2.repository.DpeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.dao.DataAccessException;

import java.util.List;

@RestController
@RequestMapping("/api/depe")
public class DpeController {
    @Autowired
    private DpeRepository dpeRepository;

    //selecting personas for servicios
    @GetMapping("/fetch-service-personas/{ent}/{eje}/{depcod}")
    public ResponseEntity<?> fetchAllservices(
        @PathVariable Integer ent,
        @PathVariable String eje,
        @PathVariable String depcod
    ) {
        try {
            List<Object[]> personas = dpeRepository.fetchPersonas(ent, eje, depcod);
            if (personas.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se puede encontrar a las personas de este departamento.");
            }
            return ResponseEntity.ok(personas);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al consultar personas: " + ex.getMostSpecificCause().getMessage());
        }
    } 

    //deleting a persona from a service
    @DeleteMapping("/delete-persona-service/{ent}/{eje}/{depcod}/{percod}")
    public ResponseEntity<?> fetchAllservices(
        @PathVariable Integer ent,
        @PathVariable String eje,
        @PathVariable String depcod,
        @PathVariable String percod
    ) {
        try {
            DpeId id = new DpeId(ent, eje, depcod, percod);
            if(!dpeRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No se encontró la personna para eliminar.");
            }

            dpeRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error durante la eliminación: " + ex.getMostSpecificCause().getMessage());
        }
    }

    //selecting a persona's services
    @GetMapping("/fetch-persona-service/{ent}/{eje}/{percod}")
    public ResponseEntity<?> fetchPersonaService(
         @PathVariable Integer ent,
        @PathVariable String eje,
        @PathVariable String percod
    ) {
        try{
            List<DepCodDesDto> services = dpeRepository.personaServices(ent, eje, percod);
            if (services.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se pueden encontrar servicios para esta persona.");
            }
            return ResponseEntity.ok(services);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error: " + ex.getMostSpecificCause().getMessage());
        }
    }

    //deleting a persona's services
    @DeleteMapping("/delete-service-persona/{ent}/{eje}/{depcod}/{percod}")
    public ResponseEntity<?> deleteService(
        @PathVariable Integer ent,
        @PathVariable String eje,
        @PathVariable String depcod,
        @PathVariable String percod
    ) {
        try {
            DpeId id = new DpeId(ent, eje, depcod, percod);
            if(!dpeRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No se encontró la servicio para eliminar.");
            }

            dpeRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error durante la eliminación: " + ex.getMostSpecificCause().getMessage());
        }
    }
}
