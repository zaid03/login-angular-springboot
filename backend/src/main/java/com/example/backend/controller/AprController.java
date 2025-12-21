package com.example.backend.controller;

import com.example.backend.controller.CgeController.centroAdd;
import com.example.backend.dto.AprDto;
import com.example.backend.sqlserver2.model.Apr;
import com.example.backend.sqlserver2.repository.AprRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
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

    // Modifying an articulo
    public record articulo(String aprref, double aprpre, double apruem, String aprobs, Integer apracu) {}

    @PatchMapping("/update-apr/{ent}/{tercod}/{afacod}/{asucod}/{artcod}")
    public ResponseEntity<?> updateArticulo(
        @PathVariable Integer ent,
        @PathVariable Integer tercod,
        @PathVariable String afacod,
        @PathVariable String asucod,
        @PathVariable String artcod,
        @RequestBody articulo payload
    ) {
        try {
            int articulo = aprRepository.updateArticulo(
                payload.aprref(),
                payload.aprpre(),
                payload.apruem(),
                payload.aprobs(),
                payload.apracu(),
                ent,
                tercod,
                afacod,
                asucod,
                artcod
            );

            if (articulo == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró ninguno articulo para los datos.");
            }

            return ResponseEntity.noContent().build();
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("La actualización falló.: " + ex.getMostSpecificCause().getMessage());
        }
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
