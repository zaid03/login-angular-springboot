package com.example.backend.controller;

import com.example.backend.dto.AprDto;
import com.example.backend.sqlserver2.model.Apr;
import com.example.backend.sqlserver2.repository.AprRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/more")
public class AprController {

    @Autowired
    private AprRepository aprRepository;

    // Custom query to find Apr by ENT and TERCOD
    @GetMapping("/by-apr/{ent}/{tercod}")
    public ResponseEntity<List<AprDto>> getApr(@PathVariable Integer ent, @PathVariable Integer tercod) {
        List<AprDto> result = aprRepository.findByEnt(ent, tercod);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    // Modifying the data
    @PutMapping("/update-apr")
    public ResponseEntity<String> updateApr(@RequestBody AprDto aprDto) {
        int updated = aprRepository.updateOneApr(
            aprDto.getAPRREF(),
            aprDto.getAPRPRE(),
            aprDto.getAPRUEM(),
            aprDto.getAPROBS(),
            aprDto.getAPRACU(),
            aprDto.getENT(),
            aprDto.getTERCOD(),
            aprDto.getAFACOD(),
            aprDto.getASUCOD(),
            aprDto.getARTCOD()
        );
        return ResponseEntity.ok(updated + " row(s) updated");
    }

    // Deleting data
    @DeleteMapping("/delete-apr")
    public ResponseEntity<String> deleteApr(
        @RequestParam Integer ent,
        @RequestParam Integer tercod,
        @RequestParam String afacod,
        @RequestParam String asucod,
        @RequestParam String artcod
    ) {
        int deleted = aprRepository.deleteByENTAndTERCODAndAFACODAndASUCODAndARTCOD(ent, tercod, afacod, asucod, artcod);
        if (deleted > 0) {
            return ResponseEntity.ok("Apr deleted successfully");
        } else {
            return ResponseEntity.status(404).body("Apr not found");
        }
    }

    //adding data
    @PostMapping("/add-apr")
    public ResponseEntity<String> addApr(@RequestBody AprDto aprDto) {
        Apr apr = new Apr();
        apr.setENT(aprDto.getENT());
        apr.setTERCOD(aprDto.getTERCOD());
        apr.setAFACOD(aprDto.getAFACOD());
        apr.setASUCOD(aprDto.getASUCOD());
        apr.setARTCOD(aprDto.getARTCOD());
        apr.setAPRREF(aprDto.getAPRREF());
        apr.setAPRPRE(aprDto.getAPRPRE());
        apr.setAPRUEM(aprDto.getAPRUEM());
        apr.setAPROBS(aprDto.getAPROBS());
        apr.setAPRACU(aprDto.getAPRACU());

        aprRepository.save(apr);
        return ResponseEntity.ok(apr.getAPRREF() + " added successfully");
    }
}
