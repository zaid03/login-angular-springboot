package com.example.backend.controller;


import com.example.backend.dto.TpeDto;
import com.example.backend.sqlserver.repository.TpeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/more")
public class TpeController {
    
    @Autowired
    private TpeRepository tpeRepository;

    @GetMapping("/by-tpe/ent/{ent}/tercod/{tercod}")
    public ResponseEntity<TpeDto> getByEntAndTercod(@PathVariable Integer ent, @PathVariable Integer tercod) {
        return tpeRepository.findByENTAndTERCOD(ent, tercod)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
