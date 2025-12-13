package com.example.backend.sqlserver2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

import com.example.backend.sqlserver2.model.Cge;
import com.example.backend.sqlserver2.model.CgeId;

@Repository
public interface CgeRepository extends JpaRepository<Cge, CgeId> {
    //to fetch all centro gestores
    List<Cge> findByENTAndEJE(int ent, String eje);

    //to update 
    @Modifying
    @Transactional
    @Query("""
        UPDATE Cge c
        SET
            c.CGEDES = :CGEDES,
            c.CGEORG = :CGEORG,
            c.CGEFUN = :CGEFUN,
            c.CGEDAT = :CGEDAT,
            c.CGECIC = :CGECIC
        WHERE
            c.ENT = :ENT
            AND c.EJE = :EJE
            AND c.CGECOD = :CGECOD
    """)
    int updateCentroGestor(
        @Param("CGEDES") String CGEDES,
        @Param("CGEORG") String CGEORG,
        @Param("CGEFUN") String CGEFUN,
        @Param("CGEDAT") String CGEDAT,
        @Param("CGECIC") Integer CGECIC,
        @Param("ENT") Integer ENT,
        @Param("EJE") String EJE,
        @Param("CGECOD") String CGECOD
    );

    //deleting centro gestor
    @Modifying
    @Transactional
    @Query("""
        DELETE FROM Cge c
        WHERE 
            c.ENT = :ENT
            AND c.EJE = :EJE
            AND c.CGECOD = :CGECOD 
    """)
    int deleteCentroGestor(
        @Param("ENT") Integer ENT,
        @Param("EJE") String EJE,
        @Param("CGECOD") String CGECOD
    );

    //needed for adding a centro gestor
    List<Cge> findByENTAndEJEAndCGECOD(int ent, String eje, String cgecod);
}