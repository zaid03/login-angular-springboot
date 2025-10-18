package com.example.backend.controller;

import com.example.backend.sqlserver2.model.Art;
import com.example.backend.sqlserver2.repository.ArtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/art")
public class ArtController {

    @Autowired
    private ArtRepository artRepository;

    // Method to find Art records by ENT and AFACOD and artcod
    @GetMapping("/by-ent/{ent}/{afacod}/{asucod}/{artcod}")
    public List<Art> getByEntAfacodAsucodArtcod(
            @PathVariable int ent,
            @PathVariable String afacod,
            @PathVariable String asucod,
            @PathVariable String artcod) {
        return artRepository.findByENTAndAFACODAndASUCODAndARTCOD(ent, afacod, asucod, artcod);
    }

    // Method to find art records by ent and artdes like
    @GetMapping("/by-ent-like/{ent}/{artdes}")
    public List<Art> getByEntAndArtdesLike(
            @PathVariable int ent,
            @PathVariable String artdes) {
        return artRepository.findByENTAndARTDESContaining(ent, artdes);
    }

    //find an art name
    @GetMapping("/art-name/{ent}/{afacod}/{asucod}/{artcod}")
    public List<Art> getArtName(
        @PathVariable int ent,
        @PathVariable String afacod,
        @PathVariable String asucod,
        @PathVariable String artcod) 
        {
        return artRepository.findArtName(ent, afacod, asucod, artcod);
        }
}
