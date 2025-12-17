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

    //modifying a service
    public record serviceUpdate(String depdes, Integer depalm, Integer depcom, Integer depint, String ccocod) {}
    @PatchMapping("/update-service/{ent}/{eje}/{depcod}")
    public ResponseEntity<?> updateCentro(
        @PathVariable Integer ent,
        @PathVariable String eje,
        @PathVariable String depcod,
        @RequestBody serviceUpdate payload
    ) {
        try {
            if (payload == null || payload.depdes() == null || payload.depalm() == null || payload.depcom() == null || payload.depint() == null || payload.ccocod() == null) {
                return ResponseEntity.badRequest().body("Faltan datos obligatorios.");
            }

            int updated = depRepository.updateService(
                payload.depdes(),
                payload.depalm(),
                payload.depcom(),
                payload.depint(),
                payload.ccocod(),
                ent,
                eje,
                depcod
            );

            if (updated == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró ninguna centro gestor para los datos.");
            }
            return ResponseEntity.noContent().build();
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Update failed: " + ex.getMostSpecificCause().getMessage());
        }
    }

    //adding a service
    public record serviceAdd(Integer ent, String eje, String depcod, String depdes, Integer depalm, Integer depcom, Integer depint, String ccocod, String cgecod) {}
    @PostMapping("/Insert-service")
    public ResponseEntity<?> addCentroGestor(
        @RequestBody serviceAdd payload
    ) {
        try {
            if (payload == null || payload.ent() == null || payload.eje() == null || payload.depcod() == null || payload.depdes() == null || payload.depalm() == null || payload.depcom() == null || payload.depint() == null || payload.ccocod() == null || payload.cgecod() == null) {
                return ResponseEntity.badRequest().body("Faltan datos obligatorios.");
            }
            if(!depRepository.findByENTAndEJEAndDEPCOD(payload.ent(), payload.eje(), payload.depcod()).isEmpty()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Este servicio ya existe   .");
            }

            Dep nueva = new Dep();
            nueva.setENT(payload.ent());
            nueva.setEJE(payload.eje());
            nueva.setDEPCOD(payload.depcod());
            nueva.setDEPDES(payload.depdes());
            nueva.setDEPALM(payload.depalm());
            nueva.setDEPCOM(payload.depcom());
            nueva.setCCOCOD(payload.ccocod());
            nueva.setCGECOD(payload.cgecod());

            depRepository.save(nueva);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Update failed: " + ex.getMostSpecificCause().getMessage());
        }
    }
}
