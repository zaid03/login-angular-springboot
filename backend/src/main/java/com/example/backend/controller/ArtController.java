package com.example.backend.controller;

import com.example.backend.sqlserver2.model.Art;
import com.example.backend.sqlserver2.repository.ArtRepository;
import com.example.backend.sqlserver2.repository.AsuRepository;
import com.example.backend.sqlserver2.repository.AfaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/art")
public class ArtController {

    @Autowired
    private ArtRepository artRepository;
    @Autowired
    private AfaRepository afaRepository;
    @Autowired
    private AsuRepository asuRepository;

    // Method to find Art records by ENT and AFACOD and artcod
    @GetMapping("/by-ent/{ent}/{afacod}/{asucod}/{artcod}")
    public List<Art> getByEntAfacodAsucodArtcod(
            @PathVariable int ent,
            @PathVariable String afacod,
            @PathVariable String asucod,
            @PathVariable String artcod) {
        return artRepository.findByENTAndAFACODAndASUCODAndARTCOD(ent, afacod, asucod, artcod);
    }

    // Method to find art records by ent and artdes like
    @GetMapping("/by-ent-like/{ent}/{artdes}")
    public List<Art> getByEntAndArtdesLike(
            @PathVariable int ent,
            @PathVariable String artdes) {
        return artRepository.findByENTAndARTDESContaining(ent, artdes);
    }

    //find an art name
    @GetMapping("/art-name/{ent}/{afacod}/{asucod}/{artcod}")
    public List<Art> getArtName(
        @PathVariable int ent,
        @PathVariable String afacod,
        @PathVariable String asucod,
        @PathVariable String artcod) 
        {
        return artRepository.findArtName(ent, afacod, asucod, artcod);
        }

    //deleting a familia
    @DeleteMapping("/delete-familia/{ent}/{afacod}")
    public ResponseEntity<?> deleteFamilia(
        @PathVariable Integer ent,
        @PathVariable String afacod
    ) {

        long articulos = artRepository.countByEntAndAfacod(ent, afacod);
        if (articulos > 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("No se puede borrar una familia con artículos asociados");
        }

        asuRepository.deleteByEntAndAfacod(ent, afacod);
        int removed = afaRepository.deleteByEntAndAfacod(ent, afacod);
        return removed == 0
            ? ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Familia no encontrada para el código indicado.")
            : ResponseEntity.noContent().build();
    }

    //to delete a subfamilia
    @DeleteMapping("/delete-sub-familia/{ent}/{afacod}/{asucod}")
    public ResponseEntity<?> deleteSubFamilia(
        @PathVariable Integer ent,
        @PathVariable String afacod,
        @PathVariable String asucod
    ) {
        long articulos = artRepository.countByEntAndAsucod(ent, asucod);
        if (articulos > 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("No se puede borrar una subfamilia con artículos asociados");
        }

        int removed = asuRepository.deleteByEntAndAfacodAndAsucod(ent, afacod, asucod);
        return removed == 0
            ? ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Subfamilia no encontrada para el código indicado.")
            : ResponseEntity.noContent().build();
    }
}
