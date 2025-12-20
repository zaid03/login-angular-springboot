package com.example.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.backend.dto.TpeDto;
import com.example.backend.sqlserver2.model.Tpe;
import com.example.backend.sqlserver2.repository.TpeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;
import java.util.List;
@RestController
@RequestMapping("/api/more")
public class TpeController {
    
    @Autowired
    private TpeRepository tpeRepository;
    private static final Logger logger = LoggerFactory.getLogger(TpeController.class);

    // Custom query to find Tpe by ENT and TERCOD
    @GetMapping("/by-tpe/{ent}/{tercod}")
    public ResponseEntity<?> getByEntAndTercod(
        @PathVariable int ent,
        @PathVariable int tercod) {

        List<TpeDto> result = tpeRepository.findDtoByEntAndTercod(ent, tercod);

        if (result == null || result.isEmpty()) {
            logger.info("No Tpe found for ent={}, tercod={}", ent, tercod);
            Map<String, String> body = Collections.singletonMap(
                "message",
                String.format("No se encontró ninguna persona de contacto para este proveedor", ent, tercod)
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        } else {
            logger.debug("Found {} Tpe entries for ent={}, tercod={}", result.size(), ent, tercod);
            return ResponseEntity.ok(result);
        }
    }

    // modifying personas de contacto
    public record personaContacto(String tpenom, String tpetel, String tpetmo, String tpecoe, String tpeobs) {}

    @PutMapping("/modify/{ent}/{tercod}/{tpecod}")
    public ResponseEntity<?> modifyTpe(
        @PathVariable Integer ent,
        @PathVariable Integer tercod,
        @PathVariable Integer tpecod,
        @RequestBody personaContacto payload
    ) {
        try {
            if (payload == null || payload.tpenom() == null) {
                return ResponseEntity.badRequest().body("nombre requerido.");
            }

            int updated = tpeRepository.updatePersona(
                payload.tpenom(),
                payload.tpetel(),
                payload.tpetmo(),
                payload.tpecoe(),
                payload.tpeobs(),
                ent,
                tercod,
                tpecod
            );

            if (updated == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró ninguna persona de contacto para los datos.");
            }

            return ResponseEntity.noContent().build();
        } catch(DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("La actualización falló: " + ex.getMostSpecificCause().getMessage());
        }
    }

    // Deleting data
    // @DeleteMapping("/delete/{ent}/{tercod}/{tpecod}")
    // @Transactional
    // public ResponseEntity<?> deleteTpe(
    //     @PathVariable Integer ent, 
    //     @PathVariable Integer tercod,
    //     @PathVariable Integer tpecod
    // ) {
    //     List<Tpe> list = tpeRepository.findByEntAndTercodAndTpecod(ent, tercod, tpecod);
    //     if (list == null || list.isEmpty()) {
    //         return ResponseEntity.status(HttpStatus.NOT_FOUND)
    //             .body(Collections.singletonMap("message", "No Tpe entries found to delete"));
    //     }
    //     tpeRepository.deleteByEntAndTercodAndTpecod(ent, tercod, tpecod);
    //     return ResponseEntity.ok(Collections.singletonMap("message", "Tpe entries deleted successfully"));
    // }

    //adding a persona de contacto
}
