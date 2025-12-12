package com.example.backend.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.util.StringUtils;


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

    private static final Logger log = LoggerFactory.getLogger(FacController.class);

    //for the main list
    @GetMapping("/{ent}/{eje}/{cgecod}")
    public ResponseEntity<List<Map<String, Object>>> getFacturas(
        @PathVariable Integer ent,
        @PathVariable String eje,
        @PathVariable String cgecod
    ) {    
        try {
            List<Object[]> rows = facRepository.findByENTAndEJE(ent, eje, cgecod);
            List<Map<String, Object>> result = new ArrayList<>();
            for (Object[] r : rows) {
                result.add(rowToMap(r));
            }
            return ResponseEntity.ok(result);
        } catch (DataAccessException ex) {
            log.error("Failed to fetch facturas for ent={}, eje={}, cgecod={}", ent, eje, cgecod, ex);
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "No se pudieron obtener las facturas. Intente más tarde.",
                ex
            );
        }
    }

    //to seach in facturas
    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchFacturas(
        @RequestParam Integer ent,
        @RequestParam String eje,
        @RequestParam String cgecod,
        @RequestParam(defaultValue = "CONT") String estado,
        @RequestParam(defaultValue = "REGISTRO") String dateType,
        @RequestParam(defaultValue = "ANY") String facannMode,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
        @RequestParam(required = false) String facann,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) String searchType
    ) {
        String normalizedEstado = Optional.ofNullable(estado)
            .map(s -> s.toUpperCase(Locale.ROOT))
            .filter(Set.of("TODAS", "CONT", "NO_CONT", "PTE_APL", "PTE_SIN")::contains)
            .orElse("CONT");

        String normalizedDateType = Optional.ofNullable(dateType)
            .map(s -> s.toUpperCase(Locale.ROOT))
            .filter(Set.of("FACTURA", "CONTABLE", "REGISTRO")::contains)
            .orElse("REGISTRO");

        String normalizedFacannMode = Optional.ofNullable(facannMode)
            .map(s -> s.toUpperCase(Locale.ROOT))
            .filter(Set.of("ANY", "NULL", "NOT_NULL", "VALUE")::contains)
            .orElse("ANY");

        String sanitizedSearch = StringUtils.hasText(search) ? search.trim() : null;

        String normalizedSearchType = Optional.ofNullable(searchType)
            .map(s -> s.toUpperCase(Locale.ROOT))
            .filter(Set.of("TERCOD", "TERADO", "NIF", "NIF_LETTERS", "OTROS")::contains)
            .orElse("OTROS");

        String searchUpper = sanitizedSearch != null ? sanitizedSearch.toUpperCase(Locale.ROOT) : null;

        List<Object[]> rows = facRepository.searchFacturas(
            ent,
            eje,
            cgecod,
            normalizedEstado,
            normalizedDateType,
            Optional.ofNullable(desde).map(LocalDate::toString).orElse(null),
            Optional.ofNullable(hasta).map(LocalDate::toString).orElse(null),
            normalizedFacannMode,
            facann,
            sanitizedSearch,
            searchUpper,
            sanitizedSearch != null ? normalizedSearchType : "OTROS"
        );

        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rows) {
            result.add(rowToMap(row));
        }
        return result.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(result);
    }
}