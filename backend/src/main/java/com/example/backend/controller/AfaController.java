package com.example.backend.controller;

import com.example.backend.sqlserver2.model.Afa;
import com.example.backend.sqlserver2.repository.AfaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.dao.DataAccessException;


import java.util.List;

@RestController
@RequestMapping("/api/afa")
public class AfaController {

    @Autowired
    private AfaRepository afaRepository;

    @GetMapping("/by-ent/{ent}/{afacod}")
    public List<Afa> getByEntAndAfacod(@PathVariable int ent, @PathVariable String afacod) {
        return afaRepository.findByENTAndAFACOD(ent, afacod);
    }

    @GetMapping("/by-ent-like/{ent}/{afades}")
    public List<Afa> getByEntAndAfadesLike(@PathVariable int ent, @PathVariable String afades) {
        return afaRepository.findByENTAndAFADESContaining(ent, afades);
    }

    //find an art name 
    @GetMapping("/art-name/{ent}/{afacod}")
    public List<Afa> getArtName(
        @PathVariable int ent,
        @PathVariable String afacod) 
        {
            return afaRepository.getArtName(ent, afacod);
        }

    //find familias by ent
    @GetMapping("/by-ent/{ent}")
    public List<Afa> getAfaByEnt(
        @PathVariable int ent
    ) {
        return afaRepository.findByENT(ent);
    }

    //update description of familias
    @PatchMapping("/update-familia/{ent}/{afacod}")
    public ResponseEntity<?> updateFamilia(
        @PathVariable Integer ent,
        @PathVariable String afacod,
        @RequestBody String afades
    ) {
        try {
            String value = afades != null ? afades.trim() : "";
            if (value.startsWith("\"") && value.endsWith("\"")) {
                value = value.substring(1, value.length() - 1);
            }
            
            int updated = afaRepository.updateFamilia (
                value,
                ent,
                afacod
            );
            if (updated == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró ninguna familia para los afacod.");
            }
            return ResponseEntity.noContent().build();
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Update failed: " + ex.getMostSpecificCause().getMessage());
        }
    }

    //familia add
    public record newFamilia(Integer ent, String afacod, String afades) {}

    @PostMapping("/Insert-familia")
    public ResponseEntity<?> insertFamilia(
        @RequestBody newFamilia payload
    )
    {
        if (payload == null || payload.ent() == null || payload.afacod() == null || payload.afades() == null) {
            return ResponseEntity.badRequest().body("Faltan datos obligatorios.");
        }

        if (!afaRepository.findByENTAndAFACOD(payload.ent(), payload.afacod()).isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("La familia ya existe para ese código.");
        }

        Afa nueva = new Afa();
        nueva.setENT(payload.ent());
        nueva.setAFACOD(payload.afacod());
        nueva.setAFADES(payload.afades());

        afaRepository.save(nueva);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
