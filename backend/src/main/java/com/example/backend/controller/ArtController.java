package com.example.backend.controller;

import com.example.backend.sqlserver.model.Art;
import com.example.backend.sqlserver.repository.ArtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/art")
public class ArtController {

    @Autowired
    private ArtRepository artRepository;

    @GetMapping("/by-ent-afacod-asucod")
    public List<Art> getByEntAfacodAsucod(
            @RequestParam int ent,
            @RequestParam String afacod,
            @RequestParam String asucod) {
        return artRepository.findByENTAndAFACODAndASUCOD(ent, afacod, asucod);
    }
}