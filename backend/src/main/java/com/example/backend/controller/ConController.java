package com.example.backend.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.backend.dto.ContratoDto;
import com.example.backend.service.CotContratoProjection;
import com.example.backend.sqlserver2.repository.CotRepository;

@RestController
@RequestMapping("/api/con")
public class ConController {
    @Autowired
    private CotRepository cotRepository;

    //selecting all contratos
    @GetMapping("/fetch-contratos/{ent}/{eje}")
    public ResponseEntity<?> fetchContratos(
        @PathVariable Integer ent,
        @PathVariable String eje
    ) {
        try {
            List<CotContratoProjection> rows = cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJE(3, ent, eje);
            if (rows == null || rows.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sin resultado");
            }

            List<ContratoDto> dto = rows.stream().map(p -> {
                CotContratoProjection.ConnInfo c = p.getConn();
                CotContratoProjection.TerInfo t = p.getTer();
                return new ContratoDto(
                    c.getCONCOD(),
                    c.getCONLOT(),
                    c.getCONDES(),
                    c.getCONFIN(),
                    c.getCONFFI(),
                    c.getCONBLO(),
                    t.getTERCOD(),
                    t.getTERNOM()
                );
            }).collect(Collectors.toList());

            return ResponseEntity.ok(dto);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + ex.getMostSpecificCause().getMessage());
        }
    }

    //search by concod bloqueado
    @GetMapping("/searchByCodigoBloque/{ent}/{eje}/{concod}")
    public ResponseEntity<?> searchContratosCodigoBloqueado(
        @PathVariable Integer ent,
        @PathVariable String eje,
        @PathVariable Integer concod
    ) {
        try {
            List<CotContratoProjection> rows = cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONCODAndConnCONBLO(3, ent, eje, concod, 0);
            if (rows == null || rows.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sin resultado");
            }

            List<ContratoDto> dto = rows.stream().map(p -> {
                CotContratoProjection.ConnInfo c = p.getConn();
                CotContratoProjection.TerInfo t = p.getTer();
                return new ContratoDto(
                    c.getCONCOD(),
                    c.getCONLOT(),
                    c.getCONDES(),
                    c.getCONFIN(),
                    c.getCONFFI(),
                    c.getCONBLO(),
                    t.getTERCOD(),
                    t.getTERNOM()
                );
            }).collect(Collectors.toList());

            return ResponseEntity.ok(dto);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + ex.getMostSpecificCause().getMessage());
        }
    }

    //search by concod no bloqueado
    @GetMapping("/searchByCodigoNoBloque/{ent}/{eje}/{concod}")
    public ResponseEntity<?> searchContratosCodigoNoBloqueado(
        @PathVariable Integer ent,
        @PathVariable String eje,
        @PathVariable Integer concod
    ) {
        try {
            List<CotContratoProjection> rows = cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONCODAndConnCONBLONot(3, ent, eje, concod, 0);
            if (rows == null || rows.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sin resultado");
            }

            List<ContratoDto> dto = rows.stream().map(p -> {
                CotContratoProjection.ConnInfo c = p.getConn();
                CotContratoProjection.TerInfo t = p.getTer();
                return new ContratoDto(
                    c.getCONCOD(),
                    c.getCONLOT(),
                    c.getCONDES(),
                    c.getCONFIN(),
                    c.getCONFFI(),
                    c.getCONBLO(),
                    t.getTERCOD(),
                    t.getTERNOM()
                );
            }).collect(Collectors.toList());

            return ResponseEntity.ok(dto);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + ex.getMostSpecificCause().getMessage());
        }
    }

    //search by condes bloqueado
    @GetMapping("/searchByDescBloque/{ent}/{eje}/{condes}")
    public ResponseEntity<?> searchContratosDescBloqueado(
        @PathVariable Integer ent,
        @PathVariable String eje,
        @PathVariable String condes
    ) {
        try {
            List<CotContratoProjection> rows = cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONDESContainingAndConnCONBLO(3, ent, eje, condes, 0);
            if (rows == null || rows.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sin resultado");
            }

            List<ContratoDto> dto = rows.stream().map(p -> {
                CotContratoProjection.ConnInfo c = p.getConn();
                CotContratoProjection.TerInfo t = p.getTer();
                return new ContratoDto(
                    c.getCONCOD(),
                    c.getCONLOT(),
                    c.getCONDES(),
                    c.getCONFIN(),
                    c.getCONFFI(),
                    c.getCONBLO(),
                    t.getTERCOD(),
                    t.getTERNOM()
                );
            }).collect(Collectors.toList());

            return ResponseEntity.ok(dto);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + ex.getMostSpecificCause().getMessage());
        }
    }

    //search by condes no bloqueado
    @GetMapping("/searchByDescNoBloque/{ent}/{eje}/{condes}")
    public ResponseEntity<?> searchContratosDescNoBloqueado(
        @PathVariable Integer ent,
        @PathVariable String eje,
        @PathVariable String condes
    ) {
        try {
            List<CotContratoProjection> rows = cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONDESContainingAndConnCONBLONot(3, ent, eje, condes, 0);
            if (rows == null || rows.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sin resultado");
            }

            List<ContratoDto> dto = rows.stream().map(p -> {
                CotContratoProjection.ConnInfo c = p.getConn();
                CotContratoProjection.TerInfo t = p.getTer();
                return new ContratoDto(
                    c.getCONCOD(),
                    c.getCONLOT(),
                    c.getCONDES(),
                    c.getCONFIN(),
                    c.getCONFFI(),
                    c.getCONBLO(),
                    t.getTERCOD(),
                    t.getTERNOM()
                );
            }).collect(Collectors.toList());

            return ResponseEntity.ok(dto);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + ex.getMostSpecificCause().getMessage());
        }
    }

    //search by bloqueado all
    @GetMapping("/searchByBloqu/{ent}/{eje}")
    public ResponseEntity<?> searchContratosBloqu(
        @PathVariable Integer ent,
        @PathVariable String eje
    ) {
        try {
            List<CotContratoProjection> rows = cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONBLO(3, ent, eje, 0);
            if (rows == null || rows.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sin resultado");
            }

            List<ContratoDto> dto = rows.stream().map(p -> {
                CotContratoProjection.ConnInfo c = p.getConn();
                CotContratoProjection.TerInfo t = p.getTer();
                return new ContratoDto(
                    c.getCONCOD(),
                    c.getCONLOT(),
                    c.getCONDES(),
                    c.getCONFIN(),
                    c.getCONFFI(),
                    c.getCONBLO(),
                    t.getTERCOD(),
                    t.getTERNOM()
                );
            }).collect(Collectors.toList());

            return ResponseEntity.ok(dto);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + ex.getMostSpecificCause().getMessage());
        }
    }

    //search by bloqueado all
    @GetMapping("/searchByNobloq/{ent}/{eje}")
    public ResponseEntity<?> searchContratosNobloq(
        @PathVariable Integer ent,
        @PathVariable String eje
    ) {
        try {
            List<CotContratoProjection> rows = cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONBLONot(3, ent, eje, 0);
            if (rows == null || rows.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sin resultado");
            }

            List<ContratoDto> dto = rows.stream().map(p -> {
                CotContratoProjection.ConnInfo c = p.getConn();
                CotContratoProjection.TerInfo t = p.getTer();
                return new ContratoDto(
                    c.getCONCOD(),
                    c.getCONLOT(),
                    c.getCONDES(),
                    c.getCONFIN(),
                    c.getCONFFI(),
                    c.getCONBLO(),
                    t.getTERCOD(),
                    t.getTERNOM()
                );
            }).collect(Collectors.toList());

            return ResponseEntity.ok(dto);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + ex.getMostSpecificCause().getMessage());
        }
    }
}