package com.example.backend.controller;

import com.example.backend.sqlserver2.model.Mag;
import com.example.backend.sqlserver2.model.Mat;
import com.example.backend.sqlserver2.model.Mta;
import com.example.backend.sqlserver2.repository.MagRepository;
import com.example.backend.sqlserver2.repository.MatRepository;
import com.example.backend.sqlserver2.repository.MtaRepository;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.dao.DataAccessException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/mat")
public class MatController {
    @Autowired
    private MatRepository matRepository;
    @Autowired
    private MagRepository magRepository;
    @Autowired
    private MtaRepository mtaRepository;

    //selecting almacen for services
    public record AlmacenCompleteDto(
        MagDto mag,
        List<MatDto> mats,
        List<MtaDto> mtas
    ) {}

    public record MagDto(Integer MAGCOD) {}
    public record MatDto(Integer mtacod) {}
    public record MtaDto(Integer mtacod, String mtades) {}
    @Transactional
    @GetMapping("/fetch-almacenajes/{ent}/{depcod}")
    public ResponseEntity<?> fetchAlmacenajes(
        @PathVariable Integer ent,
        @PathVariable String depcod
    ) {
        try {
            Optional<Mag> service = magRepository.findByENTAndDEPCOD(ent, depcod);
            if (service.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sin resultado");
            }

            List<Mat> record = matRepository.findByENTAndMAGCOD(ent, service.get().getMAGCOD());
            if (record.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sin resultado");
            }

            MagDto magDto = new MagDto(service.get().getMAGCOD());

            List<MatDto> matDtos = record.stream()
                .map(mat -> new MatDto(mat.getMta().getMTACOD()))
                .toList();

            List<Mta> almacenes = new ArrayList<>();
            for (Mat mat: record) {
                Integer mtacod = mat.getMta().getMTACOD();
                Optional<Mta> almacen = mtaRepository.findFirstByENTAndMTACOD(ent, mtacod);
                almacen.ifPresent(almacenes::add);
            }

            // Deduplicate by MTACOD to return only distinct almacenes
            Set<Integer> seenMtaCods = new HashSet<>();
            List<MtaDto> mtaDtos = almacenes.stream()
                .filter(mta -> seenMtaCods.add(mta.getMTACOD()))
                .map(mta -> new MtaDto(mta.getMTACOD(), mta.getMTADES())) 
                .toList();

            return ResponseEntity.ok(mtaDtos);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + ex.getMostSpecificCause().getMessage());
        }
    }
}
