package com.example.backend.controller;

import com.example.backend.dto.MenuDto;
import com.example.backend.repository.RpmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mnucods")
@CrossOrigin(origins = "*")
public class RpmController {

    @Autowired
    private RpmRepository rpmRepository;

    @GetMapping
    public List<MenuDto> getMnucodsByPERCOD(@RequestParam String PERCOD) {
    return rpmRepository.findMNUCODsByPERCOD(PERCOD)
            .stream()
            .map(MenuDto::new)
            .collect(java.util.stream.Collectors.toList());
    }
}
