package com.example.backend.controller;

import com.example.backend.sqlserver2.model.Cge;
import com.example.backend.sqlserver2.repository.CgeRepository;
import com.example.backend.sqlserver2.repository.GbsRepository;
import com.example.backend.sqlserver2.repository.DepRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.dao.DataAccessException;

import java.util.List;

@RestController
@RequestMapping("/api/cge")
public class CgeController {
    @Autowired
    private CgeRepository cgeRepository;
    @Autowired
    private GbsRepository gbsRepository;
    @Autowired
    private DepRepository depRepository;

    @GetMapping("/fetch-all/{ent}/{eje}")
    public ResponseEntity<?> fetchAllCentroGestores(
        @PathVariable Integer ent,
        @PathVariable String eje
    ) {
        try {
            List<Cge> centros = cgeRepository.findByENTAndEJE(ent, eje);
            if (centros.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontraron centros para ese ENT/EJE.");
            }
            return ResponseEntity.ok(centros);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al consultar centros: " + ex.getMostSpecificCause().getMessage());
        }
    }

    //update centro gestor
    public record centroUpdate(String cgedes, String cgeorg, String cgefun, String cgedat, Integer cgecic) {}

    @PatchMapping("/update-familia/{ent}/{eje}/{cge}")
    public ResponseEntity<?> updateCentro(
        @PathVariable Integer ent,
        @PathVariable String eje,
        @PathVariable String cge,
        @RequestBody centroUpdate payload
    ) {
        try {
            if (payload == null || payload.cgedes() == null || payload.cgeorg() == null || payload.cgefun() == null || payload.cgedat() == null || payload.cgecic() == null) {
                return ResponseEntity.badRequest().body("Faltan datos obligatorios.");
            }

            int updated = cgeRepository.updateCentroGestor(
                payload.cgedes(),
                payload.cgeorg(),
                payload.cgefun(),
                payload.cgedat(),
                payload.cgecic(),
                ent,
                eje,
                cge
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
    //add centro gestor
    public record centroAdd(Integer ent, String eje, String cgecod, String cgedes, String cgeorg, String cgefun, String cgedat, Integer cgecic) {}

    @PostMapping("/Insert-familia")
    public ResponseEntity<?> addCentroGestor(
        @RequestBody centroAdd payload
    ) {
        try {
            if (payload == null || payload.ent() == null || payload.eje() == null || payload.cgecod() == null || payload.cgedes() == null || payload.cgeorg() == null || payload.cgefun() == null || payload.cgedat() == null || payload.cgecic() == null) {
                return ResponseEntity.badRequest().body("Faltan datos obligatorios.");
            }

            if(!cgeRepository.findByENTAndEJEAndCGECOD(payload.ent(), payload.eje(), payload.cgecod()).isEmpty()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Este Centro gestor ya existe   .");
            }
            
            Cge nueva = new Cge();
            nueva.setENT(payload.ent());
            nueva.setEJE(payload.eje());
            nueva.setCGECOD(payload.cgecod());
            nueva.setCGEDES(payload.cgedes());
            nueva.setCGEORG(payload.cgeorg());
            nueva.setCGEFUN(payload.cgefun());
            nueva.setCGEDAT(payload.cgedat());
            nueva.setCGECIC(payload.cgecic());

            cgeRepository.save(nueva);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch(DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("insert failed: " + ex.getMostSpecificCause().getMessage());
        }
    }

    //to delete a centro gestor
    @DeleteMapping("/delete-centro-gestor/{ent}/{eje}/{cgecod}")
    public ResponseEntity<?> deleteSubFamilia(
        @PathVariable Integer ent,
        @PathVariable String eje,
        @PathVariable String cgecod
    ) {
        try {
            Long bolsas = gbsRepository.CountBolsas(ent, eje, cgecod);
            if (bolsas > 0) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("No puede borrar un centro gestor que tiene bolsas de crédito");
            }

            Long services = depRepository.countServices(ent, eje, cgecod);
            if (services > 0) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("No puede borrar un centro gestor que tiene servicios");
            }

            int removed = cgeRepository.deleteCentroGestor(ent, eje, cgecod);
            return removed == 0
            ? ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Centro gestor no encontrada para los datos.")
            : ResponseEntity.noContent().build();

        } catch(DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("delete failed: " + ex.getMostSpecificCause().getMessage());
        }
    }
}