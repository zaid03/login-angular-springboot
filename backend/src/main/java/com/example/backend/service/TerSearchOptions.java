package com.example.backend.service;

import com.example.backend.sqlserver2.model.Ter;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;

public class TerSearchOptions {
    
    // Field name constants
    private static final String ENT = "ENT";
    private static final String TERBLO = "TERBLO";
    private static final String TERNIF = "TERNIF";
    private static final String TERNOM = "TERNOM";
    private static final String TERALI = "TERALI";
    
    // Fixed values
    private static final Integer BLOQUEADO = 1;
    
    // Search predicate types
    private static final int SEARCH_ALL = 1;      // TERNIF, TERNOM, TERALI
    private static final int SEARCH_NOM_ALI = 2;  // TERNOM, TERALI

    private TerSearchOptions() {
        // Utility class - cannot be instantiated
    }

    public static Specification<Ter> searchFiltered(Integer ent, String term) {
        return (root, query, cb) -> buildPredicate(root, cb, ent, term, SEARCH_ALL, true);
    }

    //for the list filtered by TERNIF and TERNOM and TERALI no bloqueado
    public static Specification<Ter> searchByTerm(Integer ent, String term) {
        return (root, query, cb) -> buildPredicate(root, cb, ent, term, SEARCH_ALL, false);
    }

    //for the list filtered by TERNOM and TERALI bloqueado
    public static Specification<Ter> searchByNomOrAli(Integer ent, String term) {
        return (root, query, cb) -> buildPredicate(root, cb, ent, term, SEARCH_NOM_ALI, true);
    }

    //for the list filtered by TERNOM and TERALI no bloqueado
    public static Specification<Ter> findMatchingNomOrAli(Integer ent, String term) {
        return (root, query, cb) -> buildPredicate(root, cb, ent, term, SEARCH_NOM_ALI, false);
    }

    // filtering by ternif and ternom and terali
    public static Specification<Ter> searchTodos(Integer ent, String term) {
        return (root, query, cb) -> {
            Predicate entPredicate = cb.equal(root.get(ENT), ent);
            Predicate searchPredicate = buildSearchPredicate(root, cb, SEARCH_ALL, term);
            return cb.and(entPredicate, searchPredicate);
        };
    }
    
    private static Predicate buildPredicate(Root<Ter> root, CriteriaBuilder cb, Integer ent, String term, 
            int searchType, boolean bloqueado) {
        Predicate entPredicate = cb.equal(root.get(ENT), ent);
        Predicate terbloPredicate = bloqueado 
            ? cb.equal(root.get(TERBLO), BLOQUEADO)
            : cb.notEqual(root.get(TERBLO), BLOQUEADO);
        Predicate searchPredicate = buildSearchPredicate(root, cb, searchType, term);
        
        return cb.and(entPredicate, terbloPredicate, searchPredicate);
    }
    
    private static Predicate buildSearchPredicate(Root<Ter> root, CriteriaBuilder cb, int searchType, String term) {
        if (searchType == SEARCH_NOM_ALI) {
            return cb.or(
                cb.like(root.get(TERNOM), "%" + term + "%"),
                cb.like(root.get(TERALI), "%" + term + "%")
            );
        }
        
        // SEARCH_ALL
        return cb.or(
            cb.like(root.get(TERNIF), "%" + term + "%"),
            cb.like(root.get(TERNOM), "%" + term + "%"),
            cb.like(root.get(TERALI), "%" + term + "%")
        );
    }
}
