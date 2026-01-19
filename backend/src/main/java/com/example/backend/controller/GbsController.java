package com.example.backend.controller;

import com.example.backend.sqlserver2.model.Cge;
import com.example.backend.sqlserver2.model.CgeId;
import com.example.backend.sqlserver2.model.Gbs;
import com.example.backend.sqlserver2.model.GbsId;
import com.example.backend.sqlserver2.repository.CgeRepository;
import com.example.backend.dto.GbsWithCgeDto;
import com.example.backend.sqlserver2.repository.GbsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gbs")
public class GbsController {
    @Autowired
    private GbsRepository gbsRepository;
    @Autowired
    private CgeRepository cgeRepository;

    //for the main list
    @GetMapping("fetch-all/{ent}/{eje}/{cgecod}")
    public ResponseEntity<?> getBolsas(
        @PathVariable Integer ent,
        @PathVariable String eje,
        @PathVariable String cgecod
    ) {
        try {
            List<Gbs> gbsList = gbsRepository.findByENTAndEJEAndCGECOD(ent, eje, cgecod);
            
            Optional<Cge> cgeOpt = cgeRepository.findById(new CgeId(ent, eje, cgecod));
            if (cgeOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Sin resultado");
            }
            Cge cge = cgeOpt.get();

            List<GbsWithCgeDto> result = gbsList.stream().map(g -> new GbsWithCgeDto(
                cge.getCGECOD(), cge.getCGEDES(), cge.getCGECIC() != null ? String.valueOf(cge.getCGECIC()) : null,
                g.getGBSREF(), g.getGBSOPE(), g.getGBSORG(), g.getGBSFUN(), g.getGBSECO(),
                g.getGBSFOP(), g.getGBSIMP(), g.getGBSIBG(), g.getGBSIUS(), g.getGBSICO(),
                g.getGBSIUT(), g.getGBSICT(), g.getGBS413()
            )).collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Error: " + ex.getMostSpecificCause().getMessage());
        }
    }

    //modifying a bolsa
    public record updateBolsa(Double GBSIMP, Double GBSIUS, String GBSECO, LocalDateTime GBSFOP) {}
    @PatchMapping("/{ent}/{eje}/{cgecod}/{gbsref}")
    public ResponseEntity<?> updateBolsa(
            @PathVariable Integer ent,
            @PathVariable String eje,
            @PathVariable String cgecod,
            @PathVariable String gbsref,
            @RequestBody updateBolsa payload
    ) {
        try {
            if (payload == null || payload.GBSIMP() == null || payload.GBSIUS() == null || payload.GBSECO() == null || payload.GBSFOP() == null) {
                return ResponseEntity.badRequest().body("Faltan datos obligatorios.");
            }

            GbsId id = new GbsId(ent, eje, cgecod, gbsref);
            Optional<Gbs> bolsa = gbsRepository.findById(id);
            if (bolsa.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Sin resultado");
            }

            Gbs bolsaUpdate = bolsa.get();
            bolsaUpdate.setGBSIMP(payload.GBSIMP());
            bolsaUpdate.setGBSIUS(payload.GBSIUS());
            bolsaUpdate.setGBSECO(payload.GBSECO());
            bolsaUpdate.setGBSFOP(payload.GBSFOP());

            gbsRepository.save(bolsaUpdate);
            return ResponseEntity.noContent().build();
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + ex.getMostSpecificCause().getMessage());
        }
    }
}
