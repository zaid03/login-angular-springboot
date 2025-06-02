package com.example.backend.controller;

import com.example.backend.dto.PuaEntDTO;
import com.example.backend.service.PuaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PuaController {

    @Autowired
    private PuaService puaService;

    @GetMapping("/filter")
    public ResponseEntity<List<PuaEntDTO>> getFilteredData(@RequestParam String usucod) {
        System.out.println("Filter endpoint called with usucod=" + usucod);
        List<PuaEntDTO> result = puaService.getFilteredData(usucod);
        return ResponseEntity.ok(result);
    }
}
