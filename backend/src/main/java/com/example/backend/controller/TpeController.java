package com.example.backend.controller;


import com.example.backend.dto.TpeDto;
import com.example.backend.sqlserver.repository.TpeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
@RestController
@RequestMapping("/api/more")
public class TpeController {
    
    @Autowired
    private TpeRepository tpeRepository;

    // Custom query to find Tpe by ENT and TERCOD
    @GetMapping("/by-tpe/ent/{ent}/tercod/{tercod}")
    public ResponseEntity<TpeDto> getByEntAndTercod(@PathVariable Integer ent, @PathVariable Integer tercod) {
        return tpeRepository.findByENTAndTERCOD(ent, tercod)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // modifying the data
    @PutMapping("/modify/{TERCOD}")
    public ResponseEntity<?> modifyTpe(
        @PathVariable Integer TERCOD,
        @RequestBody TpeDto update
    ) {
        return tpeRepository.findByTERCOD(TERCOD)
        .map(tpe -> {
            tpe.setTPENOM(update.getTPENOM());
            tpe.setTPETEL(update.getTPETEL());
            tpe.setTPETMO(update.getTPETMO());
            tpe.setTPECOE(update.getTPECOE());
            tpe.setTPEOBS(update.getTPEOBS());
            tpeRepository.save(tpe);
            return ResponseEntity.ok("Tpe modified successfully");
        })
            .orElse(ResponseEntity.notFound().build());
    }

    // Deleting data
    @DeleteMapping("/delete/{TERCOD}")
    @Transactional
    public ResponseEntity<String> deleteTpe(@PathVariable Integer TERCOD) {
        return tpeRepository.findByTERCOD(TERCOD)
            .map(tpe -> {
                tpeRepository.delete(tpe);
                return ResponseEntity.ok("Tpe deleted successfully");
            })
            .orElse(ResponseEntity.status(404).body("Tpe not found"));
    }
}
