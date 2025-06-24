package com.example.backend.controller;

import com.example.backend.sqlserver.model.Asu;
import com.example.backend.sqlserver.repository.AsuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asu")
public class AsuController {

    @Autowired
    private AsuRepository asuRepository;

    @GetMapping("/by-ent-afacod")
    public List<Asu> getByEntAndAfacod(
            @RequestParam int ent,
            @RequestParam String afacod) {
        return asuRepository.findByENTAndAFACOD(ent, afacod);
    }
}