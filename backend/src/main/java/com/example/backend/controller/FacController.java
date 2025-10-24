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

    //helper for mapping
    private Map<String,Object> rowToMap(Object[] r) {
        Map<String,Object> m = new HashMap<>();
        m.put("ent",     r.length > 0  ? r[0]  : null);
        m.put("eje",     r.length > 1  ? r[1]  : null);
        m.put("facnum",  r.length > 2  ? r[2]  : null);
        m.put("tercod",  r.length > 3  ? r[3]  : null);
        m.put("cgecod",  r.length > 4  ? r[4]  : null);
        m.put("facobs",  r.length > 5  ? r[5]  : null);
        m.put("facimp",  r.length > 6  ? r[6]  : null);
        m.put("faciec",  r.length > 7  ? r[7]  : null);
        m.put("facidi",  r.length > 8  ? r[8]  : null);
        m.put("factdc",  r.length > 9  ? r[9]  : null);
        m.put("facann",  r.length > 10 ? r[10] : null);
        m.put("facfac",  r.length > 11 ? r[11] : null);
        m.put("facdoc",  r.length > 12 ? r[12] : null);
        m.put("facdat",  r.length > 13 ? r[13] : null);
        m.put("facfco",  r.length > 14 ? r[14] : null);
        m.put("facado",  r.length > 15 ? r[15] : null);
        m.put("factxt",  r.length > 16 ? r[16] : null);
        m.put("facfre",  r.length > 17 ? r[17] : null);
        m.put("conctp",  r.length > 18 ? r[18] : null);
        m.put("concpr",  r.length > 19 ? r[19] : null);
        m.put("conccr",  r.length > 20 ? r[20] : null);
        m.put("facoct",  r.length > 21 ? r[21] : null);
        m.put("facfpg",  r.length > 22 ? r[22] : null);
        m.put("facopg",  r.length > 23 ? r[23] : null);
        m.put("factpg",  r.length > 24 ? r[24] : null);
        m.put("facdto",  r.length > 25 ? r[25] : null);
        m.put("ternom",  r.length > 26 ? r[26] : null);
        m.put("ternif",  r.length > 27 ? r[27] : null);
        return m;
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
            result.add(rowToMap(r));
        }
        return ResponseEntity.ok(result);
    }

    //Filter by facfre hasta
    @GetMapping("/facfre-hasta/{ent}/{eje}/{toDate}")
    public ResponseEntity<List<Map<String, Object>>> FilterFacfreHastaPath(
            @PathVariable int ent,
            @PathVariable String eje,
            @PathVariable String toDate) {

        List<Object[]> rows = facRepository.filterFacfreHasta(ent, eje, toDate);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] r : rows) {
            result.add(rowToMap(r));
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
            result.add(rowToMap(r));
        }
        return ResponseEntity.ok(result);
    }
}