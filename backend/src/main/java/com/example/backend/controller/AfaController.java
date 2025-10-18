package com.example.backend.controller;

import com.example.backend.sqlserver2.model.Afa;
import com.example.backend.sqlserver2.repository.AfaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
}
