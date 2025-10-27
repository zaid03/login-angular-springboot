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

    //for the main list
    @GetMapping("/{ent}/{eje}")
    public ResponseEntity<List<Map<String, Object>>> getFacturas(
        @PathVariable Integer ent,
        @PathVariable String eje) {
            
        List<Object[]> rows = facRepository.findByENTAndEJE(ent, eje);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] r : rows) {
            result.add(rowToMap(r));
        }
        return ResponseEntity.ok(result);
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

    // desde contabilizadas
    @GetMapping("/facfre-desde-facado-notnull/{ent}/{eje}/{fromDate}")
    public ResponseEntity<List<Map<String,Object>>> facfreDesdeFacadoNotNull(
            @PathVariable int ent, @PathVariable String eje, @PathVariable String fromDate) {
        List<Object[]> rows = facRepository.filterFacfreDesdeFacadoNotNull(ent, eje, fromDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    // desde no contabilizadas
    @GetMapping("/facfre-desde-facado-null/{ent}/{eje}/{fromDate}")
    public ResponseEntity<List<Map<String,Object>>> facfreDesdeFacadoNull(
            @PathVariable int ent, @PathVariable String eje, @PathVariable String fromDate) {
        List<Object[]> rows = facRepository.filterFacfreDesdeFacadoNull(ent, eje, fromDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    // desde aplicadas (FACIMP == FACIEC+FACIDI)
    @GetMapping("/facfre-desde-facado-aplicadas/{ent}/{eje}/{fromDate}")
    public ResponseEntity<List<Map<String,Object>>> facfreDesdeFacadoAplicadas(
            @PathVariable int ent, @PathVariable String eje, @PathVariable String fromDate) {
        List<Object[]> rows = facRepository.filterFacfreDesdeFacadoAndAplicadas(ent, eje, fromDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    // desde sin aplicadas (FACIMP != FACIEC+FACIDI)
    @GetMapping("/facfre-desde-facado-sinaplicadas/{ent}/{eje}/{fromDate}")
    public ResponseEntity<List<Map<String,Object>>> facfreDesdeFacadoSinAplicadas(
            @PathVariable int ent, @PathVariable String eje, @PathVariable String fromDate) {
        List<Object[]> rows = facRepository.filterFacfreDesdeFacadoSinAplicadas(ent, eje, fromDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    // hasta contabilizadas
    @GetMapping("/facfre-hasta-facado-notnull/{ent}/{eje}/{toDate}")
    public ResponseEntity<List<Map<String,Object>>> facfreHastaFacadoNotNull(
            @PathVariable int ent, @PathVariable String eje, @PathVariable String toDate) {
        List<Object[]> rows = facRepository.filterFacfreHastaFacadoNotNull(ent, eje, toDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    // hasta no contabilizadas
    @GetMapping("/facfre-hasta-facado-null/{ent}/{eje}/{toDate}")
    public ResponseEntity<List<Map<String,Object>>> facfreHastaFacadoNull(
            @PathVariable int ent, @PathVariable String eje, @PathVariable String toDate) {
        List<Object[]> rows = facRepository.filterFacfreHastaFacadoNull(ent, eje, toDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    // hasta aplicadas
    @GetMapping("/facfre-hasta-facado-aplicadas/{ent}/{eje}/{toDate}")
    public ResponseEntity<List<Map<String,Object>>> facfreHastaFacadoAplicadas(
            @PathVariable int ent, @PathVariable String eje, @PathVariable String toDate) {
        List<Object[]> rows = facRepository.filterFacfreHastaFacadoAndAplicadas(ent, eje, toDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    // hasta sin aplicadas
    @GetMapping("/facfre-hasta-facado-sinaplicadas/{ent}/{eje}/{toDate}")
    public ResponseEntity<List<Map<String,Object>>> facfreHastaFacadoSinAplicadas(
            @PathVariable int ent, @PathVariable String eje, @PathVariable String toDate) {
        List<Object[]> rows = facRepository.filterFacfreHastaFacadoSinAplicadas(ent, eje, toDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    // range (between) variants
    @GetMapping("/facfre-range/{ent}/{eje}/{fromDate}/{toDate}")
    public ResponseEntity<List<Map<String,Object>>> facfreRange(
            @PathVariable int ent, @PathVariable String eje,
            @PathVariable String fromDate, @PathVariable String toDate) {
        List<Object[]> rows = facRepository.filterFacfreHastaDesde(ent, eje, fromDate, toDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/facfre-range-facado-notnull/{ent}/{eje}/{fromDate}/{toDate}")
    public ResponseEntity<List<Map<String,Object>>> facfreRangeFacadoNotNull(
            @PathVariable int ent, @PathVariable String eje,
            @PathVariable String fromDate, @PathVariable String toDate) {
        List<Object[]> rows = facRepository.filterFacfreDesdeHastaFacadoNotNull(ent, eje, fromDate, toDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/facfre-range-facado-null/{ent}/{eje}/{fromDate}/{toDate}")
    public ResponseEntity<List<Map<String,Object>>> facfreRangeFacadoNull(
            @PathVariable int ent, @PathVariable String eje,
            @PathVariable String fromDate, @PathVariable String toDate) {
        List<Object[]> rows = facRepository.filterFacfreDesdeHastaFacadoNull(ent, eje, fromDate, toDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/facfre-range-facado-aplicadas/{ent}/{eje}/{fromDate}/{toDate}")
    public ResponseEntity<List<Map<String,Object>>> facfreRangeFacadoAplicadas(
            @PathVariable int ent, @PathVariable String eje,
            @PathVariable String fromDate, @PathVariable String toDate) {
        List<Object[]> rows = facRepository.filterFacfreDesdeHastaFacadoAndAplicadas(ent, eje, fromDate, toDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/facfre-range-facado-sinaplicadas/{ent}/{eje}/{fromDate}/{toDate}")
    public ResponseEntity<List<Map<String,Object>>> facfreRangeFacadoSinAplicadas(
            @PathVariable int ent, @PathVariable String eje,
            @PathVariable String fromDate, @PathVariable String toDate) {
        List<Object[]> rows = facRepository.filterFacfreDesdeHastaFacadoSinAplicadas(ent, eje, fromDate, toDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    // --- FACDAT controllers (same patterns) ---

    @GetMapping("/facdat-desde/{ent}/{eje}/{fromDate}")
    public ResponseEntity<List<Map<String,Object>>> facdatDesde(
            @PathVariable int ent, @PathVariable String eje, @PathVariable String fromDate) {
        List<Object[]> rows = facRepository.filterFacdatDesde(ent, eje, fromDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/facdat-desde-facado-notnull/{ent}/{eje}/{fromDate}")
    public ResponseEntity<List<Map<String,Object>>> facdatDesdeFacadoNotNull(
            @PathVariable int ent, @PathVariable String eje, @PathVariable String fromDate) {
        List<Object[]> rows = facRepository.filterFacdatDesdeFacadoNotNull(ent, eje, fromDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/facdat-desde-facado-null/{ent}/{eje}/{fromDate}")
    public ResponseEntity<List<Map<String,Object>>> facdatDesdeFacadoNull(
            @PathVariable int ent, @PathVariable String eje, @PathVariable String fromDate) {
        List<Object[]> rows = facRepository.filterFacdatDesdeFacadoNull(ent, eje, fromDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/facdat-desde-facado-aplicadas/{ent}/{eje}/{fromDate}")
    public ResponseEntity<List<Map<String,Object>>> facdatDesdeFacadoAplicadas(
            @PathVariable int ent, @PathVariable String eje, @PathVariable String fromDate) {
        List<Object[]> rows = facRepository.filterFacdatDesdeFacadoAndAplicadas(ent, eje, fromDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/facdat-desde-facado-sinaplicadas/{ent}/{eje}/{fromDate}")
    public ResponseEntity<List<Map<String,Object>>> facdatDesdeFacadoSinAplicadas(
            @PathVariable int ent, @PathVariable String eje, @PathVariable String fromDate) {
        List<Object[]> rows = facRepository.filterFacdatDesdeFacadoSinAplicadas(ent, eje, fromDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/facdat-hasta/{ent}/{eje}/{toDate}")
    public ResponseEntity<List<Map<String,Object>>> facdatHasta(
            @PathVariable int ent, @PathVariable String eje, @PathVariable String toDate) {
        List<Object[]> rows = facRepository.filterFacdatHasta(ent, eje, toDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/facdat-hasta-facado-notnull/{ent}/{eje}/{toDate}")
    public ResponseEntity<List<Map<String,Object>>> facdatHastaFacadoNotNull(
            @PathVariable int ent, @PathVariable String eje, @PathVariable String toDate) {
        List<Object[]> rows = facRepository.filterFacdatHastaFacadoNotNull(ent, eje, toDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/facdat-hasta-facado-null/{ent}/{eje}/{toDate}")
    public ResponseEntity<List<Map<String,Object>>> facdatHastaFacadoNull(
            @PathVariable int ent, @PathVariable String eje, @PathVariable String toDate) {
        List<Object[]> rows = facRepository.filterFacdatHastaFacadoNull(ent, eje, toDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/facdat-hasta-facado-aplicadas/{ent}/{eje}/{toDate}")
    public ResponseEntity<List<Map<String,Object>>> facdatHastaFacadoAplicadas(
            @PathVariable int ent, @PathVariable String eje, @PathVariable String toDate) {
        List<Object[]> rows = facRepository.filterFacdatHastaFacadoAndAplicadas(ent, eje, toDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/facdat-hasta-facado-sinaplicadas/{ent}/{eje}/{toDate}")
    public ResponseEntity<List<Map<String,Object>>> facdatHastaFacadoSinAplicadas(
            @PathVariable int ent, @PathVariable String eje, @PathVariable String toDate) {
        List<Object[]> rows = facRepository.filterFacdatHastaFacadoSinAplicadas(ent, eje, toDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/facdat-range/{ent}/{eje}/{fromDate}/{toDate}")
    public ResponseEntity<List<Map<String,Object>>> facdatRange(
            @PathVariable int ent, @PathVariable String eje,
            @PathVariable String fromDate, @PathVariable String toDate) {
        List<Object[]> rows = facRepository.filterFacdatHastaDesde(ent, eje, fromDate, toDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/facdat-range-facado-notnull/{ent}/{eje}/{fromDate}/{toDate}")
    public ResponseEntity<List<Map<String,Object>>> facdatRangeFacadoNotNull(
            @PathVariable int ent, @PathVariable String eje,
            @PathVariable String fromDate, @PathVariable String toDate) {
        List<Object[]> rows = facRepository.filterFacdatDesdeHastaFacadoNotNull(ent, eje, fromDate, toDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/facdat-range-facado-null/{ent}/{eje}/{fromDate}/{toDate}")
    public ResponseEntity<List<Map<String,Object>>> facdatRangeFacadoNull(
            @PathVariable int ent, @PathVariable String eje,
            @PathVariable String fromDate, @PathVariable String toDate) {
        List<Object[]> rows = facRepository.filterFacdatDesdeHastaFacadoNull(ent, eje, fromDate, toDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/facdat-range-facado-aplicadas/{ent}/{eje}/{fromDate}/{toDate}")
    public ResponseEntity<List<Map<String,Object>>> facdatRangeFacadoAplicadas(
            @PathVariable int ent, @PathVariable String eje,
            @PathVariable String fromDate, @PathVariable String toDate) {
        List<Object[]> rows = facRepository.filterFacdatDesdeHastaFacadoAndAplicadas(ent, eje, fromDate, toDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/facdat-range-facado-sinaplicadas/{ent}/{eje}/{fromDate}/{toDate}")
    public ResponseEntity<List<Map<String,Object>>> facdatRangeFacadoSinAplicadas(
            @PathVariable int ent, @PathVariable String eje,
            @PathVariable String fromDate, @PathVariable String toDate) {
        List<Object[]> rows = facRepository.filterFacdatDesdeHastaFacadoSinAplicadas(ent, eje, fromDate, toDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    // --- FACFCO controllers (same patterns) ---

    @GetMapping("/facfco-desde/{ent}/{eje}/{fromDate}")
    public ResponseEntity<List<Map<String,Object>>> facfcoDesde(
            @PathVariable int ent, @PathVariable String eje, @PathVariable String fromDate) {
        List<Object[]> rows = facRepository.filterFacfcoDesde(ent, eje, fromDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/facfco-desde-facado-notnull/{ent}/{eje}/{fromDate}")
    public ResponseEntity<List<Map<String,Object>>> facfcoDesdeFacadoNotNull(
            @PathVariable int ent, @PathVariable String eje, @PathVariable String fromDate) {
        List<Object[]> rows = facRepository.filterFacfcoDesdeFacadoNotNull(ent, eje, fromDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/facfco-desde-facado-null/{ent}/{eje}/{fromDate}")
    public ResponseEntity<List<Map<String,Object>>> facfcoDesdeFacadoNull(
            @PathVariable int ent, @PathVariable String eje, @PathVariable String fromDate) {
        List<Object[]> rows = facRepository.filterFacfcoDesdeFacadoNull(ent, eje, fromDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/facfco-desde-facado-aplicadas/{ent}/{eje}/{fromDate}")
    public ResponseEntity<List<Map<String,Object>>> facfcoDesdeFacadoAplicadas(
            @PathVariable int ent, @PathVariable String eje, @PathVariable String fromDate) {
        List<Object[]> rows = facRepository.filterFacfcoDesdeFacadoAndAplicadas(ent, eje, fromDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/facfco-desde-facado-sinaplicadas/{ent}/{eje}/{fromDate}")
    public ResponseEntity<List<Map<String,Object>>> facfcoDesdeFacadoSinAplicadas(
            @PathVariable int ent, @PathVariable String eje, @PathVariable String fromDate) {
        List<Object[]> rows = facRepository.filterFacfcoDesdeFacadoSinAplicadas(ent, eje, fromDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/facfco-hasta/{ent}/{eje}/{toDate}")
    public ResponseEntity<List<Map<String,Object>>> facfcoHasta(
            @PathVariable int ent, @PathVariable String eje, @PathVariable String toDate) {
        List<Object[]> rows = facRepository.filterFacfcoHasta(ent, eje, toDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/facfco-hasta-facado-notnull/{ent}/{eje}/{toDate}")
    public ResponseEntity<List<Map<String,Object>>> facfcoHastaFacadoNotNull(
            @PathVariable int ent, @PathVariable String eje, @PathVariable String toDate) {
        List<Object[]> rows = facRepository.filterFacfcoHastaFacadoNotNull(ent, eje, toDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/facfco-hasta-facado-null/{ent}/{eje}/{toDate}")
    public ResponseEntity<List<Map<String,Object>>> facfcoHastaFacadoNull(
            @PathVariable int ent, @PathVariable String eje, @PathVariable String toDate) {
        List<Object[]> rows = facRepository.filterFacfcoHastaFacadoNull(ent, eje, toDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/facfco-hasta-facado-aplicadas/{ent}/{eje}/{toDate}")
    public ResponseEntity<List<Map<String,Object>>> facfcoHastaFacadoAplicadas(
            @PathVariable int ent, @PathVariable String eje, @PathVariable String toDate) {
        List<Object[]> rows = facRepository.filterFacfcoHastaFacadoAndAplicadas(ent, eje, toDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/facfco-hasta-facado-sinaplicadas/{ent}/{eje}/{toDate}")
    public ResponseEntity<List<Map<String,Object>>> facfcoHastaFacadoSinAplicadas(
            @PathVariable int ent, @PathVariable String eje, @PathVariable String toDate) {
        List<Object[]> rows = facRepository.filterFacfcoHastaFacadoSinAplicadas(ent, eje, toDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/facfco-range/{ent}/{eje}/{fromDate}/{toDate}")
    public ResponseEntity<List<Map<String,Object>>> facfcoRange(
            @PathVariable int ent, @PathVariable String eje,
            @PathVariable String fromDate, @PathVariable String toDate) {
        List<Object[]> rows = facRepository.filterFacfcoDesdeHasta(ent, eje, fromDate, toDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/facfco-range-facado-notnull/{ent}/{eje}/{fromDate}/{toDate}")
    public ResponseEntity<List<Map<String,Object>>> facfcoRangeFacadoNotNull(
            @PathVariable int ent, @PathVariable String eje,
            @PathVariable String fromDate, @PathVariable String toDate) {
        List<Object[]> rows = facRepository.filterFacfcoDesdeHastaFacadoNotNull(ent, eje, fromDate, toDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/facfco-range-facado-null/{ent}/{eje}/{fromDate}/{toDate}")
    public ResponseEntity<List<Map<String,Object>>> facfcoRangeFacadoNull(
            @PathVariable int ent, @PathVariable String eje,
            @PathVariable String fromDate, @PathVariable String toDate) {
        List<Object[]> rows = facRepository.filterFacfcoDesdeHastaFacadoNull(ent, eje, fromDate, toDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/facfco-range-facado-aplicadas/{ent}/{eje}/{fromDate}/{toDate}")
    public ResponseEntity<List<Map<String,Object>>> facfcoRangeFacadoAplicadas(
            @PathVariable int ent, @PathVariable String eje,
            @PathVariable String fromDate, @PathVariable String toDate) {
        List<Object[]> rows = facRepository.filterFacfcoDesdeHastaFacadoAndAplicadas(ent, eje, fromDate, toDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/facfco-range-facado-sinaplicadas/{ent}/{eje}/{fromDate}/{toDate}")
    public ResponseEntity<List<Map<String,Object>>> facfcoRangeFacadoSinAplicadas(
            @PathVariable int ent, @PathVariable String eje,
            @PathVariable String fromDate, @PathVariable String toDate) {
        List<Object[]> rows = facRepository.filterFacfcoDesdeHastaFacadoSinAplicadas(ent, eje, fromDate, toDate);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Object[] r: rows) res.add(rowToMap(r));
        return ResponseEntity.ok(res);
    }
}