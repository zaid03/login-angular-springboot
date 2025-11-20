package com.example.backend.sqlserver2.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.sqlserver2.model.Gbs;
import com.example.backend.sqlserver2.model.GbsId;

@Repository
public interface  GbsRepository extends JpaRepository<Gbs, GbsId>{
    //for the main list
    @Query(value = """
        SELECT 
            T1.CGECOD   AS CGECOD, 
            T1.CGEDES   AS CGEDES, 
            T1.CGECIC   AS CGECIC,
            T2.GBSREF   AS GBSREF,
            T2.GBSOPE   AS GBSOPE, 
            T2.GBSORG   AS GBSORG, 
            T2.GBSFUN   AS GBSFUN, 
            T2.GBSECO   AS GBSECO, 
            T2.GBSFOP   AS GBSFOP, 
            T2.GBSIMP   AS GBSIMP, 
            T2.GBSIBG   AS GBSIBG, 
            T2.GBSIUS   AS GBSIUS, 
            T2.GBSICO   AS GBSICO, 
            T2.GBSIUT   AS GBSIUT, 
            T2.GBSICT   AS GBSICT,
            T2.GBS413   AS GBS413
        FROM CGE T1
        JOIN GBS T2
          ON T1.ENT = T2.ENT
         AND T1.EJE = T2.EJE
         AND T1.CGECOD = T2.CGECOD 
        WHERE T1.ENT = :ent
          AND T1.EJE = :eje
          AND T1.CGECOD = :cgecod
    """, nativeQuery = true)
    List<GbsProjection> getBolsas(@Param("ent") int ent, @Param("eje") String eje, @Param("cgecod") String cgecod);

    //modifying a bolsa
    @Modifying
    @Transactional
    @Query("""
        UPDATE Gbs g 
        SET
            g.GBSIMP = :GBSIMP, 
            g.GBSIUS = :GBSIUS, 
            g.GBSECO = :GBSECO, 
            g.GBSFOP = :GBSFOP 
        WHERE 
            g.ENT = :ENT 
            AND g.EJE = :EJE 
            AND g.CGECOD = :CGECOD 
            AND g.GBSREF = :GBSREF 
        """)
    int updateABolsa(
        @Param("GBSIMP") double GBSIMP,
        @Param("GBSIUS") double GBSIUS,
        @Param("GBSECO") String GBSECO,
        @Param("GBSFOP") LocalDateTime GBSFOP,
        @Param("ENT") Integer ENT,
        @Param("EJE") String EJE,
        @Param("CGECOD") String CGECOD,
        @Param("GBSREF") String GBSREF
    );
}
