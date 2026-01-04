package com.example.backend.sqlserver2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend.sqlserver2.model.Cco;
import com.example.backend.sqlserver2.model.CcoId;

import java.util.List;

@Repository
public interface CcoRepository extends JpaRepository<Cco, CcoId> {
    //selecting all centros de coste
    List<Cco> findByENTAndEJE(
        @Param("ENT") Integer ENT,
        @Param("EJE") String EJE
    );

    //search by ccocod also needed for adding
    List<Cco> findByENTAndEJEAndCCOCOD(
        @Param("ENT") Integer ENT,
        @Param("EJE") String EJE,
        @Param("CCOCOD") String CCOCOD
    );

    //search by ccodes
    List<Cco> findByENTAndEJEAndCCODESContaining(
        @Param("ENT") Integer ENT,
        @Param("EJE") String EJE,
        @Param("CCODES") String CCODES
    );

    //update a centro coste
    @Modifying 
    @Transactional
    @Query("""
        UPDATE Cco c 
        SET
            c.CCODES = :CCODES
        WHERE
            c.ENT = :ENT
            AND c.EJE = :EJE
            AND c.CCOCOD = :CCOCOD
    """)
    int updateCoste(
        @Param("CCODES") String CCODES,
        @Param("ENT") Integer ENT,
        @Param("EJE") String EJE,
        @Param("CCOCOD") String CCOCOD
    );

    //needed for adding a service
    @Query(
        value = """
            SELECT COUNT(*)
            FROM CCO
            WHERE ENT = :ENT
            AND EJE = :EJE
            AND CCOCOD = :CCOCOD
        """,
        nativeQuery = true
    )
    Long countByENTAndEJEAndCCOCOD(
        @Param("ENT") Integer ENT,
        @Param("EJE") String EJE,
        @Param("CCOCOD") String CCOCOD
    );
}
