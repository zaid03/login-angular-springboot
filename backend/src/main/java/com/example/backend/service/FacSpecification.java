package com.example.backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;
import com.example.backend.sqlserver2.model.Fac;
import com.example.backend.sqlserver2.model.Ter;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.*;

public class FacSpecification {
    
    // Field constants
    private static final String ENT = "ENT";
    private static final String EJE = "EJE";
    private static final String CGECOD = "CGECOD";
    private static final String FACADO = "FACADO";
    private static final String FACIMP = "FACIMP";
    private static final String FACIEC = "FACIEC";
    private static final String FACIDI = "FACIDI";
    private static final String FACDAT = "FACDAT";
    private static final String FACFCO = "FACFCO";
    private static final String FACFRE = "FACFRE";
    private static final String FACDOC = "FACDOC";
    private static final String FACANN = "FACANN";
    private static final String TERCOD = "TERCOD";
    private static final String TERNIF = "TERNIF";
    private static final String TERNOM = "TERNOM";
    
    // SQL function constants
    private static final String ROUND = "ROUND";
    private static final Integer ROUND_SCALE = 2;
    
    public static Specification<Fac> searchFacturas(
        Integer ent, String eje, String cgecod, String estado, 
        String dateType, LocalDate fromDate, LocalDate toDate,
        String facannMode, String facann, String search, String searchType
    ) {
        return (root, query, cb) -> {
            Join<Fac, Ter> terJoin = root.join("ter", JoinType.INNER);
            
            Predicate predicate = cb.conjunction();
            predicate = applyBasicFilters(predicate, root, cb, ent, eje, cgecod);
            predicate = applyDateFilters(predicate, root, cb, dateType, fromDate, toDate);
            predicate = applyEstadoFilter(predicate, root, cb, estado);
            predicate = applyFacannFilter(predicate, root, cb, facannMode, facann);
            
            if (search != null && !search.isEmpty()) {
                Predicate searchPredicate = buildSearchPredicate(terJoin, root, cb, search, searchType);
                predicate = cb.and(predicate, searchPredicate);
            }
            
            return predicate;
        };
    }
    
    private static Predicate applyBasicFilters(Predicate predicate, Root<Fac> root, CriteriaBuilder cb, 
            Integer ent, String eje, String cgecod) {
        predicate = cb.and(predicate, cb.equal(root.get(ENT), ent));
        predicate = cb.and(predicate, cb.equal(root.get(EJE), eje));
        predicate = cb.and(predicate, cb.equal(root.get(CGECOD), cgecod));
        return predicate;
    }
    
    private static Predicate applyDateFilters(Predicate predicate, Root<Fac> root, CriteriaBuilder cb,
            String dateType, LocalDate fromDate, LocalDate toDate) {
        if (fromDate == null && toDate == null) {
            return predicate;
        }
        
        Expression<LocalDateTime> dateField = switch (dateType) {
            case "FACTURA" -> root.get(FACDAT);
            case "CONTABLE" -> root.get(FACFCO);
            default -> root.get(FACFRE);
        };
        
        if (fromDate != null) {
            predicate = cb.and(predicate, cb.greaterThanOrEqualTo(dateField, fromDate.atStartOfDay()));
        }
        if (toDate != null) {
            predicate = cb.and(predicate, cb.lessThanOrEqualTo(dateField, toDate.atTime(23, 59, 59)));
        }
        
        return predicate;
    }
    
    private static Predicate applyEstadoFilter(Predicate predicate, Root<Fac> root, CriteriaBuilder cb, String estado) {
        if ("TODAS".equals(estado)) {
            return predicate;
        }
        
        Predicate estadoPredicate = switch (estado) {
            case "CONT" -> cb.isNotNull(root.get(FACADO));
            case "NO_CONT" -> cb.isNull(root.get(FACADO));
            case "PTE_APL" -> buildPteAplPredicate(root, cb);
            case "PTE_SIN" -> buildPteSinPredicate(root, cb);
            default -> cb.conjunction();
        };
        
        return cb.and(predicate, estadoPredicate);
    }
    
    private static Predicate buildPteAplPredicate(Root<Fac> root, CriteriaBuilder cb) {
        Expression<Double> facimpRounded = cb.function(ROUND, Double.class, root.get(FACIMP), cb.literal(ROUND_SCALE));
        Expression<Double> sumRounded = cb.function(ROUND, Double.class,
            cb.sum(cb.coalesce(root.get(FACIEC), 0.0), cb.coalesce(root.get(FACIDI), 0.0)),
            cb.literal(ROUND_SCALE));
        
        return cb.and(
            cb.isNull(root.get(FACADO)),
            cb.equal(facimpRounded, sumRounded)
        );
    }
    
    private static Predicate buildPteSinPredicate(Root<Fac> root, CriteriaBuilder cb) {
        Expression<Double> facimpRounded = cb.function(ROUND, Double.class, root.get(FACIMP), cb.literal(ROUND_SCALE));
        Expression<Double> sumRounded = cb.function(ROUND, Double.class,
            cb.sum(cb.coalesce(root.get(FACIEC), 0.0), cb.coalesce(root.get(FACIDI), 0.0)),
            cb.literal(ROUND_SCALE));
        
        return cb.and(
            cb.isNull(root.get(FACADO)),
            cb.notEqual(facimpRounded, sumRounded)
        );
    }
    
    private static Predicate applyFacannFilter(Predicate predicate, Root<Fac> root, CriteriaBuilder cb,
            String facannMode, String facann) {
        if ("ANY".equals(facannMode)) {
            return predicate;
        }
        
        Predicate facannPredicate = switch (facannMode) {
            case "NULL" -> cb.isNull(root.get(FACANN));
            case "NOT_NULL" -> cb.isNotNull(root.get(FACANN));
            case "VALUE" -> cb.equal(root.get(FACANN), Integer.parseInt(facann));
            default -> cb.conjunction();
        };
        
        return cb.and(predicate, facannPredicate);
    }
    
    private static Predicate buildSearchPredicate(Join<Fac, Ter> terJoin, Root<Fac> root, CriteriaBuilder cb,
            String search, String searchType) {
        String searchUpper = search.toUpperCase();
        
        return switch (searchType) {
            case "TERCOD" -> searchByTercod(root, cb, search);
            case "NIF" -> cb.like(
                cb.upper(cb.coalesce(terJoin.get(TERNIF), "")),
                "%" + searchUpper + "%"
            );
            case "NIF_LETTERS" -> cb.or(
                cb.like(cb.upper(cb.coalesce(terJoin.get(TERNIF), "")), "%" + searchUpper + "%"),
                cb.like(cb.upper(cb.coalesce(terJoin.get(TERNOM), "")), "%" + searchUpper + "%"),
                cb.like(cb.upper(cb.coalesce(root.get(FACDOC), "")), "%" + searchUpper + "%")
            );
            case "OTROS" -> cb.or(
                cb.like(cb.upper(cb.coalesce(terJoin.get(TERNOM), "")), "%" + searchUpper + "%"),
                cb.like(cb.upper(cb.coalesce(root.get(FACDOC), "")), "%" + searchUpper + "%")
            );
            default -> cb.conjunction();
        };
    }
    
    private static Predicate searchByTercod(Root<Fac> root, CriteriaBuilder cb, String search) {
        try {
            int tercod = Integer.parseInt(search);
            return cb.equal(root.get(TERCOD), tercod);
        } catch (NumberFormatException e) {
            return cb.disjunction();
        }
    }
}