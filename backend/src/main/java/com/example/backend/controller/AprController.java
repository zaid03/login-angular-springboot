package com.example.backend.controller;

import com.example.backend.dto.AprDto;
import com.example.backend.sqlserver.repository.AprRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/more")
public class AprController {

    @Autowired
    private AprRepository aprRepository;

    @GetMapping("/by-apr/{ent}/{tercod}")
    public ResponseEntity<AprDto> getApr(@PathVariable Integer ent, @PathVariable Integer tercod) {
        return aprRepository.findByEnt(ent, tercod)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
