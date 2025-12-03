package com.example.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.sqlserver2.model.Asu;
import com.example.backend.sqlserver2.repository.AsuRepository;

@RestController
@RequestMapping("/api/asu")
public class AsuController {

    @Autowired
    private AsuRepository asuRepository;

    // Method to find Asu records by ENT and AFACOD
    @GetMapping("/by-ent/{ent}/{afacod}/{asucod}")
    public List<Asu> getByEntAndAfacodOrAsucod(
            @PathVariable int ent,
            @PathVariable String afacod,
            @PathVariable String asucod) {
        return asuRepository.findByEntAndAfacodOrAsucod(ent, afacod, asucod);
    }

    // Method to find Asu records by ENT and ASUCOD like
    @GetMapping("/by-ent-like/{ent}/{asudes}")
    public List<Asu> getByEntAndAsudesLike(
            @PathVariable int ent,
            @PathVariable String asudes) {
        return asuRepository.findByENTAndASUDESContaining(ent, asudes);
    }

    //find an art name
    @GetMapping("/art-name/{ent}/{afacod}/{asucod}")
    public List<Asu> getArtName(
        @PathVariable int ent,
        @PathVariable String afacod,
        @PathVariable String asucod) 
        {
            return asuRepository.getArtName(ent, afacod, asucod);
        }

    //filtering subs by ent and afacod
    @GetMapping("/by-ent-afacod/{ent}/{afacod}")
    public List<Asu> getSubfamilias(
        @PathVariable int ent,
        @PathVariable String afacod
    )
    {
        return asuRepository.findByENTAndAFACOD(ent, afacod);
    }

    //for updating subs
    public record newSubFamilia(String ASUDES, String ASUECO, Integer MTACOD) {}

    @PatchMapping("/update-subfamilia/{ent}/{afacod}/{asucod}")
    public ResponseEntity<?> updateSubFamilia(
        @PathVariable Integer ent,
        @PathVariable String afacod,
        @PathVariable("asucod") String asucod,
        @RequestBody newSubFamilia payload
        ) {
        if (payload == null || payload.ASUDES() == null || payload.ASUECO() == null || payload.MTACOD() == null) {
            return ResponseEntity.badRequest().body("Faltan datos obligatorios.");
        }

        int updated = asuRepository.updateSubFamilia(
                payload.ASUDES(),
                payload.ASUECO(),
                payload.MTACOD(),
                ent,
                afacod,
                asucod);

        if (updated == 0) {
            return ResponseEntity.notFound()
            .build();
        }

        return ResponseEntity.noContent().build();
    }

    //subfamilia Add
    public record newSub(Integer ent, String afacod, String asucod, String asudes, String asueco, Integer mtacod) {}

    @PostMapping("/Insert-Subfamilia")
    public ResponseEntity<?> insertSub(
        @RequestBody newSub payload
    ) {
        if (payload == null || payload.ent() == null || payload.afacod() == null || payload.asucod() == null || payload.asudes() == null || payload.asueco() == null || payload.mtacod() == null) {
            return ResponseEntity.badRequest().body("Faltan datos obligatorios.");
        }

        if (!asuRepository.findByENTAndAFACODAndASUCOD(payload.ent(), payload.afacod(), payload.asucod()).isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("La subfamilia ya existe para ese código.");
        }

        Asu nueva = new Asu();
        nueva.setENT(payload.ent());
        nueva.setAFACOD(payload.afacod());
        nueva.setASUCOD(payload.asucod());
        nueva.setASUDES(payload.asudes());
        nueva.setASUECO(payload.asueco());
        nueva.setMTACOD(payload.mtacod());

        asuRepository.save(nueva);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
