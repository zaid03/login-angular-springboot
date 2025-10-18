package com.example.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
}
