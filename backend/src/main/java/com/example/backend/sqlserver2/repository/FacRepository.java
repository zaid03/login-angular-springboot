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
        ORDER BY f.FACFRE ASC
        """, nativeQuery = true)
    List<Object[]> findByENTAndEJE(@Param("ent") Integer ent, @Param("eje") String eje);

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
          AND FACADO is not null
        ORDER BY f.FACFRE ASC
        """, nativeQuery = true)
    List<Object[]> filterFacfreDesdeFacadoNotNull(@Param("ent") int ent, @Param("eje") String eje, @Param("fromDate") String fromDate);

    //Filter by facfre desde and no contabilizadas
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
          AND FACADO is null
        ORDER BY f.FACFRE ASC
        """, nativeQuery = true)
    List<Object[]> filterFacfreDesdeFacadoNull(@Param("ent") int ent, @Param("eje") String eje, @Param("fromDate") String fromDate);

    //Filter by facfre desde and facado and aplicadas
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
          AND f.FACADO IS NULL
          AND ROUND(f.FACIMP, 2) = ROUND(COALESCE(f.FACIEC, 0) + COALESCE(f.FACIDI, 0), 2)
        ORDER BY f.FACFRE ASC
        """, nativeQuery = true)
    List<Object[]> filterFacfreDesdeFacadoAndAplicadas(@Param("ent") int ent, @Param("eje") String eje, @Param("fromDate") String fromDate);    

    //Filter by facfre desde and facado and sin aplicadas
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
          AND f.FACADO IS NULL
          AND ROUND(f.FACIMP, 2) <> ROUND(COALESCE(f.FACIEC, 0) + COALESCE(f.FACIDI, 0), 2)
        ORDER BY f.FACFRE ASC
        """, nativeQuery = true)
    List<Object[]> filterFacfreDesdeFacadoSinAplicadas(@Param("ent") int ent, @Param("eje") String eje, @Param("fromDate") String fromDate);   

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
    List<Object[]> filterFacfreHasta(@Param("ent") int ent, @Param("eje") String eje, @Param("toDate") String toDate);

    //Filter by facfre hasta and contabilizadas
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
          AND f.FACADO IS NOT NULL
        ORDER BY f.FACFRE ASC
        """, nativeQuery = true)
    List<Object[]> filterFacfreHastaFacadoNotNull(@Param("ent") int ent, @Param("eje") String eje, @Param("toDate") String toDate);

    //Filter by facfre hasta and no contabilizadas
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
          AND f.FACADO IS NULL
        ORDER BY f.FACFRE ASC
        """, nativeQuery = true)
    List<Object[]> filterFacfreHastaFacadoNull(@Param("ent") int ent, @Param("eje") String eje, @Param("toDate") String toDate);

    //Filter by facfre hasta, no contabilizadas and aplicadas (FACIMP == FACIEC+FACIDI rounded)
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
          AND f.FACADO IS NULL
          AND ROUND(f.FACIMP, 2) = ROUND(COALESCE(f.FACIEC, 0) + COALESCE(f.FACIDI, 0), 2)
        ORDER BY f.FACFRE ASC
        """, nativeQuery = true)
    List<Object[]> filterFacfreHastaFacadoAndAplicadas(@Param("ent") int ent, @Param("eje") String eje, @Param("toDate") String toDate);

    //Filter by facfre hasta, no contabilizadas and sin aplicadas (FACIMP != FACIEC+FACIDI rounded)
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
          AND f.FACADO IS NULL
          AND ROUND(f.FACIMP, 2) <> ROUND(COALESCE(f.FACIEC, 0) + COALESCE(f.FACIDI, 0), 2)
        ORDER BY f.FACFRE ASC
        """, nativeQuery = true)
    List<Object[]> filterFacfreHastaFacadoSinAplicadas(@Param("ent") int ent, @Param("eje") String eje, @Param("toDate") String toDate);

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

    //Filter by facfre BETWEEN fromDate AND toDate (facado IS NOT NULL)
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
          AND f.FACADO IS NOT NULL
        ORDER BY f.FACFRE ASC
        """, nativeQuery = true)
    List<Object[]> filterFacfreDesdeHastaFacadoNotNull(@Param("ent") int ent,
    @Param("eje") String eje, @Param("fromDate") String fromDate, @Param("toDate") String toDate);

    //Filter by facfre BETWEEN fromDate AND toDate (facado IS NULL)
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
          AND f.FACADO IS NULL
        ORDER BY f.FACFRE ASC
        """, nativeQuery = true)
    List<Object[]> filterFacfreDesdeHastaFacadoNull(@Param("ent") int ent, @Param("eje") String eje, @Param("fromDate") String fromDate, @Param("toDate") String toDate);

    //Filter by facfre BETWEEN fromDate AND toDate (facado IS NULL) AND aplicadas (FACIMP == FACIEC+FACIDI rounded)
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
          AND f.FACADO IS NULL
          AND ROUND(f.FACIMP, 2) = ROUND(COALESCE(f.FACIEC, 0) + COALESCE(f.FACIDI, 0), 2)
        ORDER BY f.FACFRE ASC
        """, nativeQuery = true)
    List<Object[]> filterFacfreDesdeHastaFacadoAndAplicadas(@Param("ent") int ent, @Param("eje") String eje, @Param("fromDate") String fromDate, @Param("toDate") String toDate);

    //Filter by facfre BETWEEN fromDate AND toDate (facado IS NULL) AND sin aplicadas (FACIMP != FACIEC+FACIDI rounded)
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
          AND f.FACADO IS NULL
          AND ROUND(f.FACIMP, 2) <> ROUND(COALESCE(f.FACIEC, 0) + COALESCE(f.FACIDI, 0), 2)
        ORDER BY f.FACFRE ASC
        """, nativeQuery = true)
    List<Object[]> filterFacfreDesdeHastaFacadoSinAplicadas(@Param("ent") int ent, @Param("eje") String eje, @Param("fromDate") String fromDate, @Param("toDate") String toDate);

    // --- end facfre variants ---

    // Filter by facdat desde
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
          AND CAST(f.FACDAT AS DATE) >= CAST(:fromDate AS DATE)
        ORDER BY f.FACDAT ASC
        """, nativeQuery = true)
    List<Object[]> filterFacdatDesde(@Param("ent") int ent, @Param("eje") String eje, @Param("fromDate") String fromDate);

    // desde contabilizadas (FACADO NOT NULL)
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
          AND CAST(f.FACDAT AS DATE) >= CAST(:fromDate AS DATE)
          AND f.FACADO IS NOT NULL
        ORDER BY f.FACDAT ASC
        """, nativeQuery = true)
    List<Object[]> filterFacdatDesdeFacadoNotNull(@Param("ent") int ent, @Param("eje") String eje, @Param("fromDate") String fromDate);

    // desde no contabilizadas (FACADO IS NULL)
    @Query(value = """
        SELECT f.ENT    AS ENT,
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
          AND CAST(f.FACDAT AS DATE) >= CAST(:fromDate AS DATE)
          AND f.FACADO IS NULL
        ORDER BY f.FACDAT ASC
        """, nativeQuery = true)
    List<Object[]> filterFacdatDesdeFacadoNull(@Param("ent") int ent, @Param("eje") String eje, @Param("fromDate") String fromDate);

    // desde no contabilizadas + aplicadas (FACIMP == FACIEC+FACIDI rounded)
    @Query(value = """
        SELECT f.ENT    AS ENT,
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
          AND CAST(f.FACDAT AS DATE) >= CAST(:fromDate AS DATE)
          AND f.FACADO IS NULL
          AND ROUND(f.FACIMP, 2) = ROUND(COALESCE(f.FACIEC,0) + COALESCE(f.FACIDI,0), 2)
        ORDER BY f.FACDAT ASC
        """, nativeQuery = true)
    List<Object[]> filterFacdatDesdeFacadoAndAplicadas(@Param("ent") int ent, @Param("eje") String eje, @Param("fromDate") String fromDate);

    // desde no contabilizadas + sin aplicadas (FACIMP != FACIEC+FACIDI rounded)
    @Query(value = """
        SELECT f.ENT    AS ENT,
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
          AND CAST(f.FACDAT AS DATE) >= CAST(:fromDate AS DATE)
          AND f.FACADO IS NULL
          AND ROUND(f.FACIMP, 2) <> ROUND(COALESCE(f.FACIEC,0) + COALESCE(f.FACIDI,0), 2)
        ORDER BY f.FACDAT ASC
        """, nativeQuery = true)
    List<Object[]> filterFacdatDesdeFacadoSinAplicadas(@Param("ent") int ent, @Param("eje") String eje, @Param("fromDate") String fromDate);

    // hasta (<= toDate)
    @Query(value = """
        SELECT f.ENT    AS ENT,
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
          AND CAST(f.FACDAT AS DATE) <= CAST(:toDate AS DATE)
        ORDER BY f.FACDAT ASC
        """, nativeQuery = true)
    List<Object[]> filterFacdatHasta(@Param("ent") int ent, @Param("eje") String eje, @Param("toDate") String toDate);

    // hasta contabilizadas
    @Query(value = """
        SELECT f.ENT    AS ENT,
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
          AND CAST(f.FACDAT AS DATE) <= CAST(:toDate AS DATE)
          AND f.FACADO IS NOT NULL
        ORDER BY f.FACDAT ASC
        """, nativeQuery = true)
    List<Object[]> filterFacdatHastaFacadoNotNull(@Param("ent") int ent, @Param("eje") String eje, @Param("toDate") String toDate);

    // hasta no contabilizadas
    @Query(value = """
        SELECT f.ENT    AS ENT,
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
          AND CAST(f.FACDAT AS DATE) <= CAST(:toDate AS DATE)
          AND f.FACADO IS NULL
        ORDER BY f.FACDAT ASC
        """, nativeQuery = true)
    List<Object[]> filterFacdatHastaFacadoNull(@Param("ent") int ent, @Param("eje") String eje, @Param("toDate") String toDate);

    // hasta no contabilizadas + aplicadas
    @Query(value = """
        SELECT f.ENT    AS ENT,
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
          AND CAST(f.FACDAT AS DATE) <= CAST(:toDate AS DATE)
          AND f.FACADO IS NULL
          AND ROUND(f.FACIMP, 2) = ROUND(COALESCE(f.FACIEC,0) + COALESCE(f.FACIDI,0), 2)
        ORDER BY f.FACDAT ASC
        """, nativeQuery = true)
    List<Object[]> filterFacdatHastaFacadoAndAplicadas(@Param("ent") int ent, @Param("eje") String eje, @Param("toDate") String toDate);

    // hasta no contabilizadas + sin aplicadas
    @Query(value = """
        SELECT f.ENT    AS ENT,
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
          AND CAST(f.FACDAT AS DATE) <= CAST(:toDate AS DATE)
          AND f.FACADO IS NULL
          AND ROUND(f.FACIMP, 2) <> ROUND(COALESCE(f.FACIEC,0) + COALESCE(f.FACIDI,0), 2)
        ORDER BY f.FACDAT ASC
        """, nativeQuery = true)
    List<Object[]> filterFacdatHastaFacadoSinAplicadas(@Param("ent") int ent, @Param("eje") String eje, @Param("toDate") String toDate);

    // between (FACDAT BETWEEN fromDate AND toDate)
    @Query(value = """
        SELECT f.ENT    AS ENT,
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
          AND CAST(f.FACDAT AS DATE) BETWEEN CAST(:fromDate AS DATE) AND CAST(:toDate AS DATE)
        ORDER BY f.FACDAT ASC
        """, nativeQuery = true)
    List<Object[]> filterFacdatHastaDesde(@Param("ent") int ent, @Param("eje") String eje, @Param("fromDate") String fromDate, @Param("toDate") String toDate);

    // between + facado not null
    @Query(value = """
        SELECT f.ENT    AS ENT,
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
          AND CAST(f.FACDAT AS DATE) BETWEEN CAST(:fromDate AS DATE) AND CAST(:toDate AS DATE)
          AND f.FACADO IS NOT NULL
        ORDER BY f.FACDAT ASC
        """, nativeQuery = true)
    List<Object[]> filterFacdatDesdeHastaFacadoNotNull(@Param("ent") int ent, @Param("eje") String eje, @Param("fromDate") String fromDate, @Param("toDate") String toDate);

    // between + facado null
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
            AND CONVERT(date, f.FACDAT) BETWEEN CONVERT(date, :fromDate) AND CONVERT(date, :toDate)
            AND f.FACADO IS NULL
        ORDER BY f.FACDAT ASC
        """, nativeQuery = true)
    List<Object[]> filterFacdatDesdeHastaFacadoNull(@Param("ent") int ent, @Param("eje") String eje, @Param("fromDate") String fromDate, @Param("toDate") String toDate);

    // between + facado null + aplicadas
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
          AND CAST(f.FACDAT AS DATE) BETWEEN CAST(:fromDate AS DATE) AND CAST(:toDate AS DATE)
          AND f.FACADO IS NULL
          AND ROUND(f.FACIMP, 2) = ROUND(COALESCE(f.FACIEC,0) + COALESCE(f.FACIDI,0), 2)
        ORDER BY f.FACDAT ASC
        """, nativeQuery = true)
    List<Object[]> filterFacdatDesdeHastaFacadoAndAplicadas(@Param("ent") int ent, @Param("eje") String eje, @Param("fromDate") String fromDate, @Param("toDate") String toDate);

    // between + facado null + sin aplicadas
    @Query(value = """
        SELECT f.ENT    AS ENT,
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
          AND CAST(f.FACDAT AS DATE) BETWEEN CAST(:fromDate AS DATE) AND CAST(:toDate AS DATE)
          AND f.FACADO IS NULL
          AND ROUND(f.FACIMP, 2) <> ROUND(COALESCE(f.FACIEC,0) + COALESCE(f.FACIDI,0), 2)
        ORDER BY f.FACDAT ASC
        """, nativeQuery = true)
    List<Object[]> filterFacdatDesdeHastaFacadoSinAplicadas(@Param("ent") int ent, @Param("eje") String eje, @Param("fromDate") String fromDate, @Param("toDate") String toDate);

        // --- FACFCO variants ---

    // Filter by facfco desde (>= fromDate)
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
          AND CAST(f.FACFCO AS DATE) >= CAST(:fromDate AS DATE)
        ORDER BY f.FACFCO ASC
        """, nativeQuery = true)
    List<Object[]> filterFacfcoDesde(@Param("ent") int ent, @Param("eje") String eje, @Param("fromDate") String fromDate);

    // desde contabilizadas (FACADO NOT NULL)
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
          AND CAST(f.FACFCO AS DATE) >= CAST(:fromDate AS DATE)
          AND f.FACADO IS NOT NULL
        ORDER BY f.FACFCO ASC
        """, nativeQuery = true)
    List<Object[]> filterFacfcoDesdeFacadoNotNull(@Param("ent") int ent, @Param("eje") String eje, @Param("fromDate") String fromDate);

    // desde no contabilizadas (FACADO IS NULL)
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
          AND CAST(f.FACFCO AS DATE) >= CAST(:fromDate AS DATE)
          AND f.FACADO IS NULL
        ORDER BY f.FACFCO ASC
        """, nativeQuery = true)
    List<Object[]> filterFacfcoDesdeFacadoNull(@Param("ent") int ent, @Param("eje") String eje, @Param("fromDate") String fromDate);

    // desde no contabilizadas + aplicadas (FACIMP == FACIEC+FACIDI rounded)
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
          AND CAST(f.FACFCO AS DATE) >= CAST(:fromDate AS DATE)
          AND f.FACADO IS NULL
          AND ROUND(f.FACIMP, 2) = ROUND(COALESCE(f.FACIEC, 0) + COALESCE(f.FACIDI, 0), 2)
        ORDER BY f.FACFCO ASC
        """, nativeQuery = true)
    List<Object[]> filterFacfcoDesdeFacadoAndAplicadas(@Param("ent") int ent, @Param("eje") String eje, @Param("fromDate") String fromDate);

    // desde no contabilizadas + sin aplicadas (FACIMP != FACIEC+FACIDI rounded)
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
          AND CAST(f.FACFCO AS DATE) >= CAST(:fromDate AS DATE)
          AND f.FACADO IS NULL
          AND ROUND(f.FACIMP, 2) <> ROUND(COALESCE(f.FACIEC, 0) + COALESCE(f.FACIDI, 0), 2)
        ORDER BY f.FACFCO ASC
        """, nativeQuery = true)
    List<Object[]> filterFacfcoDesdeFacadoSinAplicadas(@Param("ent") int ent, @Param("eje") String eje, @Param("fromDate") String fromDate);

    // ---- hasta (<= toDate) ----
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
          AND CAST(f.FACFCO AS DATE) <= CAST(:toDate AS DATE)
        ORDER BY f.FACFCO ASC
        """, nativeQuery = true)
    List<Object[]> filterFacfcoHasta(@Param("ent") int ent, @Param("eje") String eje, @Param("toDate") String toDate);

    // hasta contabilizadas
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
          AND CAST(f.FACFCO AS DATE) <= CAST(:toDate AS DATE)
          AND f.FACADO IS NOT NULL
        ORDER BY f.FACFCO ASC
        """, nativeQuery = true)
    List<Object[]> filterFacfcoHastaFacadoNotNull(@Param("ent") int ent, @Param("eje") String eje, @Param("toDate") String toDate);

    // hasta no contabilizadas
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
          AND CAST(f.FACFCO AS DATE) <= CAST(:toDate AS DATE)
          AND f.FACADO IS NULL
        ORDER BY f.FACFCO ASC
        """, nativeQuery = true)
    List<Object[]> filterFacfcoHastaFacadoNull(@Param("ent") int ent, @Param("eje") String eje, @Param("toDate") String toDate);

    // hasta no contabilizadas + aplicadas
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
        AND CAST(f.FACFCO AS DATE) <= CAST(:toDate AS DATE)
        AND f.FACADO IS NULL
        AND ROUND(f.FACIMP, 2) = ROUND(COALESCE(f.FACIEC, 0) + COALESCE(f.FACIDI, 0), 2)
        ORDER BY f.FACFCO ASC
        """, nativeQuery = true)
    List<Object[]> filterFacfcoHastaFacadoAndAplicadas(@Param("ent") int ent, @Param("eje") String eje, @Param("toDate") String toDate);

    // hasta no contabilizadas + sin aplicadas
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
          AND CAST(f.FACFCO AS DATE) <= CAST(:toDate AS DATE)
          AND f.FACADO IS NULL
          AND ROUND(f.FACIMP, 2) <> ROUND(COALESCE(f.FACIEC, 0) + COALESCE(f.FACIDI, 0), 2)
        ORDER BY f.FACFCO ASC
        """, nativeQuery = true)
    List<Object[]> filterFacfcoHastaFacadoSinAplicadas(@Param("ent") int ent, @Param("eje") String eje, @Param("toDate") String toDate);

    // ---- between (fromDate AND toDate) ----
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
          AND CAST(f.FACFCO AS DATE) BETWEEN CAST(:fromDate AS DATE) AND CAST(:toDate AS DATE)
        ORDER BY f.FACFCO ASC
        """, nativeQuery = true)
    List<Object[]> filterFacfcoDesdeHasta(@Param("ent") int ent, @Param("eje") String eje, @Param("fromDate") String fromDate, @Param("toDate") String toDate);

    // between + facado not null
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
          AND CAST(f.FACFCO AS DATE) BETWEEN CAST(:fromDate AS DATE) AND CAST(:toDate AS DATE)
          AND f.FACADO IS NOT NULL
        ORDER BY f.FACFCO ASC
        """, nativeQuery = true)
    List<Object[]> filterFacfcoDesdeHastaFacadoNotNull(@Param("ent") int ent, @Param("eje") String eje, @Param("fromDate") String fromDate, @Param("toDate") String toDate);

    // between + facado null
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
          AND CAST(f.FACFCO AS DATE) BETWEEN CAST(:fromDate AS DATE) AND CAST(:toDate AS DATE)
          AND f.FACADO IS NULL
        ORDER BY f.FACFCO ASC
        """, nativeQuery = true)
    List<Object[]> filterFacfcoDesdeHastaFacadoNull(@Param("ent") int ent, @Param("eje") String eje, @Param("fromDate") String fromDate, @Param("toDate") String toDate);

    // between + facado null + aplicadas
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
          AND CAST(f.FACFCO AS DATE) BETWEEN CAST(:fromDate AS DATE) AND CAST(:toDate AS DATE)
          AND f.FACADO IS NULL
          AND ROUND(f.FACIMP, 2) = ROUND(COALESCE(f.FACIEC, 0) + COALESCE(f.FACIDI, 0), 2)
        ORDER BY f.FACFCO ASC
        """, nativeQuery = true)
    List<Object[]> filterFacfcoDesdeHastaFacadoAndAplicadas(@Param("ent") int ent, @Param("eje") String eje, @Param("fromDate") String fromDate, @Param("toDate") String toDate);

    // between + facado null + sin aplicadas
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
          AND CAST(f.FACFCO AS DATE) BETWEEN CAST(:fromDate AS DATE) AND CAST(:toDate AS DATE)
          AND f.FACADO IS NULL
          AND ROUND(f.FACIMP, 2) <> ROUND(COALESCE(f.FACIEC, 0) + COALESCE(f.FACIDI, 0), 2)
        ORDER BY f.FACFCO ASC
        """, nativeQuery = true)
    List<Object[]> filterFacfcoDesdeHastaFacadoSinAplicadas(@Param("ent") int ent, @Param("eje") String eje, @Param("fromDate") String fromDate, @Param("toDate") String toDate);

    // --- end FACFCO variants ---
}

