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
    List<Fac> findByENTAndEJE(Integer ENT, String EJE);

    //Filter by facfre desde
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
          AND CAST(f.FACFRE AS DATE) >= CAST(:fromDate AS DATE)
        ORDER BY f.FACFRE ASC
        """, nativeQuery = true)
    List<Object[]> filterFacfreDesde(@Param("ent") int ent, @Param("eje") String eje, @Param("fromDate") String fromDate);

    //Filter by facfre desde and contabilizadas

    //filter by facfre hasta
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
          AND CAST(f.FACFRE AS DATE) <= CAST(:toDate AS DATE)
        ORDER BY f.FACFRE ASC
        """, nativeQuery = true)
    List<Object[]> filterFacfreHasta(@Param("ent") int ent, @Param("eje") String eje, @Param("toDate") String fromDate);

    //filter by facfre desde hasta
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
          AND CAST(f.FACFRE AS DATE) BETWEEN CAST(:fromDate AS DATE) AND CAST(:toDate AS DATE)
        ORDER BY f.FACFRE ASC
        """, nativeQuery = true)
    List<Object[]> filterFacfreHastaDesde(@Param("ent") int ent, @Param("eje") String eje, @Param("fromDate") String fromDate, @Param("toDate") String toDate);
}