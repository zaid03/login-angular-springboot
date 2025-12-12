package com.example.backend.sqlserver2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.backend.sqlserver2.model.Fac;

@Repository
public interface FacRepository extends JpaRepository<Fac, Integer>{
    //for the main list
    @Query(value = """
        SELECT
            f.ENT    AS ENT,
            f.EJE    AS EJE,
            f.FACNUM AS FACNUM,
            f.TERCOD AS TERCOD,
            f.CGECOD AS CGECOD,
            f.FACOBS AS FACOBS,
            f.FACIMP AS FACIMP,
            f.FACIEC AS FACIEC,
            f.FACIDI AS FACIDI,
            f.FACTDC AS FACTDC,
            f.FACANN AS FACANN,
            f.FACFAC AS FACFAC,
            f.FACDOC AS FACDOC,
            f.FACDAT AS FACDAT,
            f.FACFCO AS FACFCO,
            f.FACADO AS FACADO,
            f.FACTXT AS FACTXT,
            f.FACFRE AS FACFRE,
            f.CONCTP AS CONCTP,
            f.CONCPR AS CONCPR,
            f.CONCCR AS CONCCR,
            f.FACOCT AS FACOCT,
            f.FACFPG AS FACFPG,
            f.FACOPG AS FACOPG,
            f.FACTPG AS FACTPG,
            f.FACDTO AS FACDTO,
            t.TERNOM AS TERNOM,
            t.TERNIF AS TERNIF
        FROM FAC f
        INNER JOIN TER t
          ON f.ENT = t.ENT
         AND f.TERCOD = t.TERCOD
        WHERE f.ENT = :ent
          AND f.EJE = :eje
          AND f.cgecod = :cgecod
        ORDER BY f.FACFRE ASC
        """, nativeQuery = true)
    List<Object[]> findByENTAndEJE(@Param("ent") Integer ent, @Param("eje") String eje, @Param("cgecod") String cgecod);

  @Query(value = """
      SELECT
          f.ENT,
          f.EJE,
          f.FACNUM,
          f.TERCOD,
          f.CGECOD,
          f.FACOBS,
          f.FACIMP,
          f.FACIEC,
          f.FACIDI,
          f.FACTDC,
          f.FACANN,
          f.FACFAC,
          f.FACDOC,
          f.FACDAT,
          f.FACFCO,
          f.FACADO,
          f.FACTXT,
          f.FACFRE,
          f.CONCTP,
          f.CONCPR,
          f.CONCCR,
          f.FACOCT,
          f.FACFPG,
          f.FACOPG,
          f.FACTPG,
          f.FACDTO,
          t.TERNOM,
          t.TERNIF
      FROM FAC f
      JOIN TER t
        ON f.ENT = t.ENT
       AND f.TERCOD = t.TERCOD
      WHERE f.ENT = :ent
        AND f.EJE = :eje
        AND f.CGECOD = :cgecod
        AND (
              :fromDate IS NULL
           OR CAST(
                CASE :dateType
                  WHEN 'FACTURA'  THEN f.FACDAT
                  WHEN 'CONTABLE' THEN f.FACFCO
                  ELSE f.FACFRE
                END AS DATE
             ) >= CAST(:fromDate AS DATE)
        )
        AND (
              :toDate IS NULL
           OR CAST(
                CASE :dateType
                  WHEN 'FACTURA'  THEN f.FACDAT
                  WHEN 'CONTABLE' THEN f.FACFCO
                  ELSE f.FACFRE
                END AS DATE
             ) <= CAST(:toDate AS DATE)
        )
        AND (
              :estado = 'TODAS'
           OR (:estado = 'CONT'     AND f.FACADO IS NOT NULL)
           OR (:estado = 'NO_CONT'  AND f.FACADO IS NULL)
           OR (:estado = 'PTE_APL'  AND f.FACADO IS NULL
                                    AND ROUND(f.FACIMP, 2) = ROUND(COALESCE(f.FACIEC, 0) + COALESCE(f.FACIDI, 0), 2))
           OR (:estado = 'PTE_SIN'  AND f.FACADO IS NULL
                                    AND ROUND(f.FACIMP, 2) <> ROUND(COALESCE(f.FACIEC, 0) + COALESCE(f.FACIDI, 0), 2))
        )
        AND (
              :facannMode = 'ANY'
           OR (:facannMode = 'NULL'      AND f.FACANN IS NULL)
           OR (:facannMode = 'NOT_NULL'  AND f.FACANN IS NOT NULL)
           OR (:facannMode = 'VALUE'     AND f.FACANN = :facann)
        )
        AND (
            :search IS NULL
         OR (
                :searchType = 'TERCOD'
            AND TRY_CAST(:search AS INT) IS NOT NULL
            AND f.TERCOD = TRY_CAST(:search AS INT)
            )
         OR (
                :searchType = 'NIF'
            AND UPPER(COALESCE(t.TERNIF, '')) LIKE CONCAT('%' ,:searchUpper, '%')
            )
         OR (
                :searchType = 'NIF_LETTERS'
            AND (
                    UPPER(COALESCE(t.TERNIF, '')) LIKE CONCAT('%' ,:searchUpper, '%')
                 OR UPPER(COALESCE(t.TERNOM, '')) LIKE CONCAT('%', :searchUpper, '%')
                 OR UPPER(COALESCE(f.FACDOC, '')) LIKE CONCAT('%', :searchUpper, '%')
                )
            )
         OR (
                :searchType = 'OTROS'
            AND (
                    UPPER(COALESCE(t.TERNOM, '')) LIKE CONCAT('%', :searchUpper, '%')
                 OR UPPER(COALESCE(f.FACDOC, '')) LIKE CONCAT('%', :searchUpper, '%')
                )
            )
        )
      """, nativeQuery = true)
  List<Object[]> searchFacturas(
      @Param("ent") Integer ent,
      @Param("eje") String eje,
      @Param("cgecod") String cgecod,
      @Param("estado") String estado,
      @Param("dateType") String dateType,
      @Param("fromDate") String fromDate,
      @Param("toDate") String toDate,
      @Param("facannMode") String facannMode,
      @Param("facann") String facann,
      @Param("search") String search,
      @Param("searchUpper") String searchUpper,
      @Param("searchType") String searchType);

}