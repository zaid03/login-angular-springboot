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
          f.FACNUM AS facnum,
            f.FACDAT AS facdat,
            f.FACFCO AS facfco,
            f.FACDOC AS facdoc,
            f.FACFAC AS facfac,
            f.TERCOD AS tercod,
            t.TERNOM AS ternom,
            t.TERNIF AS ternif,       
            f.CGECOD AS cgecod,
            f.FACIMP AS facimp,
            f.FACIEC AS faciec,
            f.FACIDI AS facidi,
            f.FACFRE AS facfre
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
          f.FACNUM AS facnum,
            f.FACDAT AS facdat,
            f.FACFCO AS facfco,
            f.FACDOC AS facdoc,
            f.FACFAC AS facfac,
            f.TERCOD AS tercod,
            t.TERNOM AS ternom,
            t.TERNIF AS ternif,       
            f.CGECOD AS cgecod,
            f.FACIMP AS facimp,
            f.FACIEC AS faciec,
            f.FACIDI AS facidi,
            f.FACFRE AS facfre
        FROM FAC f
        INNER JOIN TER t
          ON f.ENT = t.ENT
         AND f.TERCOD = t.TERCOD
        WHERE f.ENT = :ent
          AND f.EJE = :eje
          AND CAST(f.FACFRE AS DATE) <= CAST(:fromDate AS DATE)
        ORDER BY f.FACFRE ASC
        """, nativeQuery = true)
    List<Object[]> filterFacfreHasta(@Param("ent") int ent, @Param("eje") String eje, @Param("fromDate") String fromDate);

    //filter by facfre desde hasta
    @Query(value = """
        SELECT
          f.FACNUM AS facnum,
            f.FACDAT AS facdat,
            f.FACFCO AS facfco,
            f.FACDOC AS facdoc,
            f.FACFAC AS facfac,
            f.TERCOD AS tercod,
            t.TERNOM AS ternom,
            t.TERNIF AS ternif,       
            f.CGECOD AS cgecod,
            f.FACIMP AS facimp,
            f.FACIEC AS faciec,
            f.FACIDI AS facidi,
            f.FACFRE AS facfre
        FROM FAC f
        INNER JOIN TER t
          ON f.ENT = t.ENT
         AND f.TERCOD = t.TERCOD
        WHERE f.ENT = :ent
          AND f.EJE = :eje
          AND CAST(f.FACFRE AS DATE) BETWEEN CAST(:fromDate AS DATE) AND CAST(:fromDate AS DATE)
        ORDER BY f.FACFRE ASC
        """, nativeQuery = true)
    List<Object[]> filterFacfreHastaDesde(@Param("ent") int ent, @Param("eje") String eje, @Param("fromDate") String fromDate, @Param("toDate") String toDate);
}