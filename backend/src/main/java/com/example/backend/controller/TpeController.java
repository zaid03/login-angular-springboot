package com.example.backend.controller;


import com.example.backend.dto.TpeDto;
import com.example.backend.sqlserver2.repository.TpeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@RestController
@RequestMapping("/api/more")
public class TpeController {
    
    @Autowired
    private TpeRepository tpeRepository;

    // Custom query to find Tpe by ENT and TERCOD
    @GetMapping("/by-tpe/{ent}/{tercod}/{tpecod}")
    public ResponseEntity<List<TpeDto>> getByEntAndTercodAndTpecod(
        @PathVariable int ent,
        @PathVariable int tercod,
        @PathVariable int tpecod) {
        List<TpeDto> result = tpeRepository.findDtoByEntAndTercodAndTpecod(ent, tercod, tpecod);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
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
        return tpeRepository.findByEntAndTercodAndTpecod(ent, tercod, tpecod)
        .map(tpe -> {
            tpe.settpenom(update.gettpenom());
            tpe.settpetel(update.gettpetel());
            tpe.settpetmo(update.gettpetmo());
            tpe.settpecoe(update.gettpecoe());
            tpe.settpeobs(update.gettpeobs());
            tpeRepository.save(tpe);
            return ResponseEntity.ok("Tpe modified successfully");
        })
            .orElse(ResponseEntity.notFound().build());
    }

    // Deleting data
    @DeleteMapping("/delete/{ent}/{tercod}/{tpecod}")
    @Transactional
    public ResponseEntity<String> deleteTpe(@PathVariable Integer ent, @PathVariable Integer tercod, @PathVariable Integer tpecod) {
        return tpeRepository.findByEntAndTercodAndTpecod(ent, tercod, tpecod)
            .map(tpe -> {
                tpeRepository.delete(tpe);
                return ResponseEntity.ok("Tpe deleted successfully");
            })
            .orElse(ResponseEntity.notFound().build());
    }
}
