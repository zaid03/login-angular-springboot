package com.example.backend.controller;

import com.example.backend.sqlserver.model.Afa;
import com.example.backend.sqlserver.repository.AfaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/afa")
public class AfaController {

    @Autowired
    private AfaRepository afaRepository;

    @GetMapping("/by-ent/{ent}")
    public List<Afa> getByEnt(@PathVariable int ent) {
        return afaRepository.findByENT(ent);
    }
}