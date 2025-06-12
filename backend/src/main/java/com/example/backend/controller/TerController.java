package com.example.backend.controller;

import com.example.backend.sqlserver.model.Ter;
import com.example.backend.sqlserver.repository.TerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public List<Ter> getByENTAndTERCODAndTERBLONot(@PathVariable int ent, @PathVariable Integer tercod) {
        return terRepository.findByENTAndTERCODAndTERBLONot(ent, tercod);
    }

    //for the list filtered by TERNIF and option bloqueado
    @GetMapping("/by-ent/{ent}/ternif/{ternif}/terblo/{terblo}")
    public List<Ter> getByENTAndTERNIFAndTERBLO(@PathVariable int ent, @PathVariable String ternif) {
        return terRepository.findByENTAndTERNIFAndTERBLO(ent, ternif);
    }

    //for the list filtered by TERNIF and option no bloqueado 
    @GetMapping("/by-ent/{ent}/ternif/{ternif}/terblo-not/{terblo}")
    public List<Ter> getByENTAndTERNIFAndTERBLONot(@PathVariable int ent, @PathVariable String ternif) {
        return terRepository.findByENTAndTERNIFContainingAndTERBLONot(ent, ternif);
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
            @RequestParam String term
    ) {
        return terRepository.searchByTerm(ent, term);
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
            @RequestParam String term
    ) {
        return terRepository.findMatchingNomOrAli(ent, term);
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
}