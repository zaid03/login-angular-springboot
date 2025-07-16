package com.example.backend.controller;

import com.example.backend.sqlserver2.model.Ter;
import com.example.backend.dto.TerDto;
import com.example.backend.sqlserver2.repository.TerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ter")
public class TerController {

    @Autowired
    private TerRepository terRepository;

    // Get all Ter records for a specific ENT
    @GetMapping("/by-ent/{ent}")
    public List<Ter> getByEnt(@PathVariable int ent) {
        return terRepository.findByENT(ent);
    }

    //for the list filtered by TERCOD and option bloqueado
    @GetMapping("/by-ent/{ent}/tercod/{tercod}/terblo/{terblo}")
    public List<Ter> getByENTAndTERCODAndTERBLO(@PathVariable int ent, @PathVariable Integer tercod) {
        return terRepository.findByENTAndTERCODAndTERBLOZero(ent, tercod);
    }

    //for the list filtered by TERCOD and option no bloqueado
    @GetMapping("/by-ent/{ent}/tercod/{tercod}/terblo-not/{terblo}")
    public List<Ter> getByENTAndTERCODAndTERBLONot(
        @PathVariable int ent,
        @PathVariable Integer tercod,
        @PathVariable Integer terblo
    ) {
        return terRepository.findByENTAndTERCODAndTERBLONot(ent, tercod, terblo);
    }

    //for the list filtered by TERNIF and option bloqueado
    @GetMapping("/by-ent/{ent}/ternif/{ternif}/terblo/{terblo}")
    public List<Ter> getByENTAndTERNIFAndTERBLO(@PathVariable int ent, @PathVariable String ternif) {
        return terRepository.findByENTAndTERNIFAndTERBLO(ent, ternif);
    }

    //for the list filtered by TERNIF and option no bloqueado 
    @GetMapping("/by-ent/{ent}/ternif/{ternif}/terblo-not/{terblo}")
    public List<Ter> getByENTAndTERNIFAndTERBLONot(@PathVariable int ent, @PathVariable String ternif, @PathVariable Integer terblo) {
        return terRepository.findByENTAndTERNIFContainingAndTERBLONot(ent, ternif, terblo);
    }

    //for the list filtered by TerNIF and TERNOM and TERALI bloqueado
    @GetMapping("/by-ent/{ent}/search")
    public List<Ter> search(
            @PathVariable int ent,
            @RequestParam String term
    ) {
        return terRepository.searchFiltered(ent, term);
    }

    //for the list filtered by TERNIF and TERNOM and TERALI no bloqueado
    @GetMapping("/by-ent/{ent}/search-by-term")
    public List<Ter> searchByTerm(
            @PathVariable int ent,
            @RequestParam String term,
            @RequestParam(required = false, defaultValue = "0") Integer terblo
    ) {
        return terRepository.searchByTerm(ent, term, terblo);
    }

    //for the list filtered by TERNIF and TERNOM and TERALI bloqueado
    @GetMapping("/by-ent/{ent}/searchByNomOrAli")
    public List<Ter> searchByNomOrAli(
            @PathVariable int ent,
            @RequestParam String term
    ) {
        return terRepository.searchByNomOrAli(ent, term);
    }

    //for the list filtered by TERNIF and TERNOM and TERALI no bloqueado
    @GetMapping("/by-ent/{ent}/findMatchingNomOrAli")
    public List<Ter> findMatchingNomOrAli(
            @PathVariable int ent,
            @RequestParam String term,
            @RequestParam(required = false, defaultValue = "0") Integer terblo
    ) {
        return terRepository.findMatchingNomOrAli(ent, term, terblo);
    }

    // For TERCOD, no TERBLO filter
    @GetMapping("/by-ent/{ent}/tercod/{tercod}")
    public List<Ter> getByENTAndTERCOD(@PathVariable int ent, @PathVariable Integer tercod) {
        return terRepository.findByENTAndTERCOD(ent, tercod);
    }

    // For TERNIF, no TERBLO filter
    @GetMapping("/by-ent/{ent}/ternif/{ternif}")
    public List<Ter> getByENTAndTERNIF(@PathVariable int ent, @PathVariable String ternif) {
        return terRepository.findByENTAndTERNIF(ent, ternif);
    }

    // For search term, no TERBLO filter
    @GetMapping("/by-ent/{ent}/search-todos")
    public List<Ter> searchTodos(
            @PathVariable int ent,
            @RequestParam String term
    ) {
        return terRepository.searchTodos(ent, term);
    }

    //for modifying a Ter record
    @PutMapping("/updateFields/{tercod}")
    public ResponseEntity<?> updateTerFields(@PathVariable Integer tercod, @RequestBody TerDto update) {
        Optional<Ter> optionalTer = terRepository.findByTERCOD(tercod);

        if (optionalTer.isPresent()) {
            Ter ter = optionalTer.get();
            ter.setTERWEB(update.getTERWEB());
            ter.setTEROBS(update.getTEROBS());
            ter.setTERBLO(update.getTERBLO());
            ter.setTERACU(update.getTERACU());
            System.out.println("TERWEB: " + ter.getTERWEB());
            System.out.println("TEROBS: " + ter.getTEROBS());
            System.out.println("TERBLO: " + ter.getTERBLO());
            System.out.println("TERACU: " + ter.getTERACU());

            System.out.println("Before save: " + ter);
            terRepository.save(ter); 

            return ResponseEntity.ok("Fields updated successfully.");   
        } else {
            return ResponseEntity.status(404).body("Ter not found.");
        }
    }
}
