package com.example.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.dto.FacWithTerProjection;
import com.example.backend.sqlserver2.repository.FacRepository;

import java.util.List;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Service
public class FacturaSearch {
    @Autowired
    private FacRepository facRepository;

    public List<FacWithTerProjection> searchFactura(
        Integer ent, 
        String eje, 
        String cgecod,
        String main_filter,
        Integer ej_factura,
        String estado,
        String fecha,
        LocalDate fromDate,
        LocalDate toDate
    ) {
        LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
        LocalDateTime toDateTime = toDate != null ? toDate.atTime(23, 59, 59) : null;


        List<FacWithTerProjection> facturas = facRepository.findByENTAndEJEAndCGECODOrderByFACFREAsc(ent, eje, cgecod);
        
        if (facturas != null && !facturas.isEmpty()) {
            if (main_filter != null && !main_filter.isEmpty()) {
                if (isNumbersOnly(main_filter)) {
                    if (main_filter.length() <= 5) {
                        facturas = filterByTercodOrFacado(facturas, main_filter);
                    } else {
                        facturas = filterByTernif(facturas, main_filter);
                    }
                }
                else if (isMixed(main_filter)) {
                    facturas = filterByTernifOrTernomOrFacdoc(facturas, main_filter);
                } else {
                    facturas = filterByTernomOrFacdoc(facturas, main_filter);
                }
            }

            if (ej_factura != null) {
                facturas = filterByFacann(facturas, ej_factura);
            }

            if (estado != null && !estado.isEmpty()) {
                if (estado.equals("contabilizadas")) {
                    facturas = filterByFacadoNNull(facturas);
                } else if (estado.equals("noContabilizadas")) {
                    facturas = filterByFacadoNull(facturas);
                } else if (estado.equals("ptApplidas")) {
                    facturas = filterByFacadoNullAndEMath(facturas);
                } else if (estado.equals("sinPtApplicar")) {
                    facturas = filterByFacadoNullAndNotEMath(facturas);
                }
            }

            if (fecha != null && !fecha.isEmpty()) {
                if (fecha.equals("registro")) {
                    if (fromDateTime != null && toDateTime != null) {
                        facturas = filterByFacfreBetween(facturas, fromDateTime, toDateTime);
                    } else if (fromDateTime != null && toDateTime == null) {
                        facturas = filterByFacfreFrom(facturas, fromDateTime);
                    } else if (toDateTime != null && fromDateTime == null) {
                        facturas = filterByFacfreTo(facturas, toDateTime);
                    }
                } else if (fecha.equals("factura")) {
                    if (fromDateTime != null && toDateTime != null) {
                        facturas = filterByFacdatBetween(facturas, fromDateTime, toDateTime);
                    } else if (fromDateTime != null && toDateTime == null) {
                        facturas = filterByFacdatFrom(facturas, fromDateTime);
                    } else if (toDateTime != null && fromDateTime == null) {
                        facturas = filterByFacdatTo(facturas, toDateTime);
                    }
                } else if (fecha.equals("contable")) {
                    if (fromDateTime != null && toDateTime != null) {
                        facturas = filterByFacfcoBetween(facturas, fromDateTime, toDateTime);
                    } else if (fromDateTime != null && toDateTime == null) {
                        facturas = filterByFacfcoFrom(facturas, fromDateTime);
                    } else if (toDateTime != null && fromDateTime == null) {
                        facturas = filterByFacfcoTo(facturas, toDateTime);
                    }
                }
            }
        }
        
        return facturas;
    }

    private boolean isNumbersOnly(String text) {return text.matches("^[0-9]+$");}
    private boolean isMixed(String text) {return !isNumbersOnly(text);}

    private List<FacWithTerProjection> filterByTercodOrFacado (
        List<FacWithTerProjection> facturas, 
        String main_filter
    ) {
        return facturas.stream().filter(f -> 
            (f.getTERCOD() != null && f.getTERCOD().toString().equals(main_filter)) || 
            (f.getFACADO() != null && f.getFACADO().contains(main_filter))
        ).toList();
    }
    private List<FacWithTerProjection> filterByTernif (
        List<FacWithTerProjection> facturas, 
        String main_filter
    ) {
        return  facturas.stream().filter(f -> (f.getTer_TERNIF() != null && f.getTer_TERNIF().equals(main_filter))).toList();
    }
    private List<FacWithTerProjection> filterByTernifOrTernomOrFacdoc (
        List<FacWithTerProjection> facturas, 
        String main_filter
    ) {
        return facturas.stream().filter(f -> (f.getTer_TERNIF() != null && f.getTer_TERNIF().toLowerCase().contains(main_filter.toLowerCase())) || (f.getTer_TERNOM() != null && f.getTer_TERNOM().toLowerCase().contains(main_filter.toLowerCase())) || (f.getFACDOC() != null && f.getFACDOC().toLowerCase().contains(main_filter.toLowerCase()))).toList();
    }
    private List<FacWithTerProjection> filterByTernomOrFacdoc (
        List<FacWithTerProjection> facturas, 
        String main_filter
    ) {
        return facturas.stream().filter(f -> (f.getTer_TERNOM() != null && f.getTer_TERNOM().toLowerCase().contains(main_filter.toLowerCase())) || (f.getFACDOC() != null && f.getFACDOC().toLowerCase().contains(main_filter.toLowerCase()))).toList();
    }
    private List<FacWithTerProjection> filterByFacann (
        List<FacWithTerProjection> facturas, 
        Integer ej_factura
    ) {
        return facturas.stream().filter(f -> (f.getFACANN() != null && f.getFACANN().equals(ej_factura))).toList();
    }
    private List<FacWithTerProjection> filterByFacadoNNull (
        List<FacWithTerProjection> facturas
    ) {
        return facturas.stream().filter(f -> f.getFACADO() != null).toList();
    }
    private List<FacWithTerProjection> filterByFacadoNull (
        List<FacWithTerProjection> facturas
    ) {
        return facturas.stream().filter(f -> f.getFACADO() == null).toList();
    }
    private List<FacWithTerProjection> filterByFacadoNullAndEMath (
        List<FacWithTerProjection> facturas
    ) {
        return facturas.stream().filter(f -> (f.getFACADO() == null && 
            Math.round(f.getFACIMP() * 100.0) / 100.0 == Math.round((f.getFACIEC() + f.getFACIDI()) * 100.0) / 100.0
        )).toList();
    }
    private List<FacWithTerProjection> filterByFacadoNullAndNotEMath (
        List<FacWithTerProjection> facturas
    ) {
        return facturas.stream().filter(f -> (f.getFACADO() == null && 
            Math.round(f.getFACIMP() * 100.0) / 100.0 != Math.round((f.getFACIEC() + f.getFACIDI()) * 100.0) / 100.0
        )).toList();
    }
    private List<FacWithTerProjection> filterByFacfreFrom (
        List<FacWithTerProjection> facturas,
        LocalDateTime fromDateTime
    ) {
        return facturas.stream().filter(f -> (f.getFACFRE() != null && f.getFACFRE().isAfter(fromDateTime)))
        .toList();
    }
    private List<FacWithTerProjection> filterByFacfreTo (
        List<FacWithTerProjection> facturas,
        LocalDateTime fromDateTime
    ) {
        return facturas.stream().filter(f -> (f.getFACFRE() != null && f.getFACFRE().isBefore(fromDateTime)))
        .toList();
    }
    private List<FacWithTerProjection> filterByFacfreBetween (
        List<FacWithTerProjection> facturas,
        LocalDateTime fromDateTime,
        LocalDateTime toDateTime
    ) {
        return facturas.stream().filter(f -> (f.getFACFRE() != null && f.getFACFRE().isAfter(fromDateTime) && f.getFACFRE().isBefore(toDateTime))).toList();
    }
    private List<FacWithTerProjection> filterByFacdatFrom (
        List<FacWithTerProjection> facturas,
        LocalDateTime fromDateTime
    ) {
        return facturas.stream().filter(f -> (f.getFACDAT() != null && f.getFACDAT().isAfter(fromDateTime)))
        .toList();
    }
    private List<FacWithTerProjection> filterByFacdatTo (
        List<FacWithTerProjection> facturas,
        LocalDateTime fromDateTime
    ) {
        return facturas.stream().filter(f -> (f.getFACDAT() != null && f.getFACDAT().isBefore(fromDateTime)))
        .toList();
    }
    private List<FacWithTerProjection> filterByFacdatBetween (
        List<FacWithTerProjection> facturas,
        LocalDateTime fromDateTime,
        LocalDateTime toDateTime
    ) {
        return facturas.stream().filter(f -> (f.getFACDAT() != null && f.getFACDAT().isAfter(fromDateTime) && f.getFACDAT().isBefore(toDateTime))).toList();
    }
    private List<FacWithTerProjection> filterByFacfcoFrom (
        List<FacWithTerProjection> facturas,
        LocalDateTime fromDateTime
    ) {
        return facturas.stream().filter(f -> (f.getFACFCO() != null && f.getFACFCO().isAfter(fromDateTime)))
        .toList();
    }
    private List<FacWithTerProjection> filterByFacfcoTo (
        List<FacWithTerProjection> facturas,
        LocalDateTime fromDateTime
    ) {
        return facturas.stream().filter(f -> (f.getFACFCO() != null && f.getFACFCO().isBefore(fromDateTime)))
        .toList();
    }
    private List<FacWithTerProjection> filterByFacfcoBetween (
        List<FacWithTerProjection> facturas,
        LocalDateTime fromDateTime,
        LocalDateTime toDateTime
    ) {
        return facturas.stream().filter(f -> (f.getFACFCO() != null && f.getFACFCO().isAfter(fromDateTime) && f.getFACFCO().isBefore(toDateTime))).toList();
    }
}