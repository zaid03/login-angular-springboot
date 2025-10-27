package com.example.backend.sqlserver2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.backend.sqlserver2.model.Gbs;

@Repository
public interface  GbsRepository extends JpaRepository<Gbs, Integer>{
    //for the main list
    @Query(value="""
        SELECT 
            T1.CGECOD, 
            T1.CGEDES, 
            T1.CGECIC,
            T2.GBSREF,
            T2.GBSOPE, 
            T2.GBSORG, 
            T2.GBSFUN, 
            T2.GBSECO, 
            T2.GBSFOP, 
            T2.GBSIMP, 
            T2.GBSIBG, 
            T2.GBSIUS, 
            T2.GBSICO, 
            T2.GBSIUT, 
            T2.GBSICT
        FROM CGE T1, GBS T2
        WHERE T1.ENT = T2.ENT
            AND T1.EJE = T2.EJE
            AND T1.CGECOD = T2.CGECOD 
            AND T1.ENT = :ent
            AND T1.EJE = :eje
            AND T1.CGECOD = :cgecod
    """, nativeQuery = true)
    List<Object[]> getBolsas(@Param("ent") int ent, @Param("eje") String eje, @Param("cgecod") String cgecod);
}
