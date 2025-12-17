package com.example.backend.sqlserver2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.backend.sqlserver2.model.Dpe;
import com.example.backend.sqlserver2.model.DpeId;

import java.util.List;

@Repository
public interface DpeRepository extends JpaRepository<Dpe, DpeId> {
    //selecting personas for servicios
    @Query("""
        SELECT
            p.PERCOD,
            p.PERNOM
        FROM Dpe d
        JOIN Per p ON p.PERCOD = d.PERCOD
        WHERE d.ENT = :ENT
            AND d.EJE = :EJE
            AND d.DEPCOD = :DEPCOD  
    """)
    List<Object[]> fetchPersonas(
        @Param("ENT") Integer ENT,
        @Param("EJE") String EJE,
        @Param("DEPCOD") String DEPCOD
    );
}
