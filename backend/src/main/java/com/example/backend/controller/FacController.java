package com.example.backend.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.backend.sqlserver2.repository.FacRepository;


@RestController
@RequestMapping("/api/fac")
public class FacController {
    @Autowired
    private FacRepository facRepository;

    //for the main list
    @GetMapping("/{ent}/{eje}")
    public ResponseEntity<?> getFacturas(
        @PathVariable Integer ent,
        @PathVariable String eje) 
    {
        return ResponseEntity.ok().body(facRepository.findByENTAndEJE(ent, eje));
    }

    //Filter by facfre desde
    @GetMapping("/facfre-desde/{ent}/{eje}/{fromDate}")
    public ResponseEntity<List<Map<String, Object>>> testFilterFacfreDesdePath(
            @PathVariable int ent,
            @PathVariable String eje,
            @PathVariable String fromDate) {

        List<Object[]> rows = facRepository.filterFacfreDesde(ent, eje, fromDate);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] r : rows) {
            Map<String, Object> m = new HashMap<>();
            m.put("facnum",  r.length > 0  ? r[0] : null);
            m.put("facdat",  r.length > 1  ? r[1] : null);
            m.put("facfco",  r.length > 2  ? r[2] : null);
            m.put("facdoc",  r.length > 3  ? r[3] : null);
            m.put("facfac",  r.length > 4  ? r[4] : null);
            m.put("tercod",  r.length > 5  ? r[5] : null);
            m.put("ternom",  r.length > 6  ? r[6] : null);
            m.put("ternif",  r.length > 7  ? r[7] : null);
            m.put("cgecod",  r.length > 8  ? r[8] : null);
            m.put("facimp",  r.length > 9  ? r[9] : null);
            m.put("faciec",  r.length > 10 ? r[10] : null);
            m.put("facidi",  r.length > 11 ? r[11] : null);
            m.put("facfre",  r.length > 12 ? r[12] : null);
            result.add(m);
        }
        return ResponseEntity.ok(result);
    }

    //Filter by facfre hasta
    @GetMapping("/facfre-hasta/{ent}/{eje}/{fromDate}")
    public ResponseEntity<List<Map<String, Object>>> FilterFacfreHastaPath(
            @PathVariable int ent,
            @PathVariable String eje,
            @PathVariable String fromDate) {

        List<Object[]> rows = facRepository.filterFacfreHasta(ent, eje, fromDate);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] r : rows) {
            Map<String, Object> m = new HashMap<>();
            m.put("facnum",  r.length > 0  ? r[0] : null);
            m.put("facdat",  r.length > 1  ? r[1] : null);
            m.put("facfco",  r.length > 2  ? r[2] : null);
            m.put("facdoc",  r.length > 3  ? r[3] : null);
            m.put("facfac",  r.length > 4  ? r[4] : null);
            m.put("tercod",  r.length > 5  ? r[5] : null);
            m.put("ternom",  r.length > 6  ? r[6] : null);
            m.put("ternif",  r.length > 7  ? r[7] : null);
            m.put("cgecod",  r.length > 8  ? r[8] : null);
            m.put("facimp",  r.length > 9  ? r[9] : null);
            m.put("faciec",  r.length > 10 ? r[10] : null);
            m.put("facidi",  r.length > 11 ? r[11] : null);
            m.put("facfre",  r.length > 12 ? r[12] : null);
            result.add(m);
        }
        return ResponseEntity.ok(result);
    }

    //Filter by facfre desde hasta
    @GetMapping("/facfre-hasta-desde/{ent}/{eje}/{fromDate}/{toDate}")
    public ResponseEntity<List<Map<String, Object>>> FilterFacfreHastaDesdePath(
            @PathVariable int ent,
            @PathVariable String eje,
            @PathVariable String fromDate,
            @PathVariable String toDate) {

        List<Object[]> rows = facRepository.filterFacfreHastaDesde(ent, eje, fromDate, toDate);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] r : rows) {
            Map<String, Object> m = new HashMap<>();
            m.put("facnum",  r.length > 0  ? r[0] : null);
            m.put("facdat",  r.length > 1  ? r[1] : null);
            m.put("facfco",  r.length > 2  ? r[2] : null);
            m.put("facdoc",  r.length > 3  ? r[3] : null);
            m.put("facfac",  r.length > 4  ? r[4] : null);
            m.put("tercod",  r.length > 5  ? r[5] : null);
            m.put("ternom",  r.length > 6  ? r[6] : null);
            m.put("ternif",  r.length > 7  ? r[7] : null);
            m.put("cgecod",  r.length > 8  ? r[8] : null);
            m.put("facimp",  r.length > 9  ? r[9] : null);
            m.put("faciec",  r.length > 10 ? r[10] : null);
            m.put("facidi",  r.length > 11 ? r[11] : null);
            m.put("facfre",  r.length > 12 ? r[12] : null);
            result.add(m);
        }
        return ResponseEntity.ok(result);
    }
}