package com.example.backend.service;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

import com.example.backend.sqlserver2.model.Fac;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class FacContabilizacionSpecification {
    public static Specification<Fac> searchContabilizacion(
        Integer ent,
        String eje,
        String cgecod,
        String fechaType,           // "registro" or "factura"
        LocalDateTime desde,
        LocalDateTime hasta,
        Integer facann              // ej.factura value
    ) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            // Required filters: ENT, EJE, CGECOD
            predicate = cb.and(predicate, cb.equal(root.get("ENT"), ent));
            predicate = cb.and(predicate, cb.equal(root.get("EJE"), eje));
            predicate = cb.and(predicate, cb.equal(root.get("CGECOD"), cgecod));

            // Required: FACADO IS NULL
            predicate = cb.and(predicate, cb.isNull(root.get("FACADO")));

            // Required: ROUND(FACIMP,2) = ROUND((FACIEC+FACIDI),2)
            Expression<Double> facimpRounded = cb.function("ROUND", Double.class, root.get("FACIMP"), cb.literal(2));
            Expression<Double> sumRounded = cb.function("ROUND", Double.class, 
                cb.sum(root.get("FACIEC"), root.get("FACIDI")), cb.literal(2));
            predicate = cb.and(predicate, cb.equal(facimpRounded, sumRounded));

            // Date field based on fechaType
            String dateField = "factura".equalsIgnoreCase(fechaType) ? "FACDAT" : "FACFRE";

            // Desde (>=)
            if (desde != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get(dateField), desde));
            }

            // Hasta (<=)
            if (hasta != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get(dateField), hasta));
            }

            // FACANN filter
            if (facann != null) {
                predicate = cb.and(predicate, cb.equal(root.get("FACANN"), facann));
            }

            return predicate;
        };
    }
}
