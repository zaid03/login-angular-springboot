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

    // modifying the data
    @PutMapping("/modify/{ent}/{tercod}/{tpecod}")
    public ResponseEntity<?> modifyTpe(
        @PathVariable Integer ent,
        @PathVariable Integer tercod,
        @PathVariable Integer tpecod,
        @RequestBody TpeDto update
    ) {
        try {
            List<Tpe> list = tpeRepository.findByEntAndTercodAndTpecod(ent, tercod, tpecod);
            if (list == null || list.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", "No se encontró a nadie"));
            }

            Tpe tpe = list.get(0);
            if (update.gettpenom() != null) tpe.settpenom(update.gettpenom());
            if (update.gettpetel() != null) tpe.settpetel(update.gettpetel());
            if (update.gettpetmo() != null) tpe.settpetmo(update.gettpetmo());
            if (update.gettpecoe() != null) tpe.settpecoe(update.gettpecoe());
            if (update.gettpeobs() != null) tpe.settpeobs(update.gettpeobs());
            tpeRepository.saveAll(list);

            return ResponseEntity.ok(Collections.singletonMap("message", "Campos actualizados exitosamente"));
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error de actualización de esta persona: " + ex.getMostSpecificCause().getMessage());
        }
    }

    // Deleting data
    @DeleteMapping("/delete/{ent}/{tercod}/{tpecod}")
    @Transactional
    public ResponseEntity<?> deleteTpe(
        @PathVariable Integer ent, 
        @PathVariable Integer tercod,
        @PathVariable Integer tpecod
    ) {
        List<Tpe> list = tpeRepository.findByEntAndTercodAndTpecod(ent, tercod, tpecod);
        if (list == null || list.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap("message", "No Tpe entries found to delete"));
        }
        tpeRepository.deleteByEntAndTercodAndTpecod(ent, tercod, tpecod);
        return ResponseEntity.ok(Collections.singletonMap("message", "Tpe entries deleted successfully"));
    }

    //adding a persona de contacto
}
