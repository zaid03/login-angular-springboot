package com.example.backend.sqlserver2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.DepCodDesDto;
import com.example.backend.sqlserver2.model.Dpe;
import com.example.backend.sqlserver2.model.DpeId;

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

    //selecting a persona's services
    @Query("""
        SELECT new com.example.backend.dto.DepCodDesDto(d.DEPCOD, d.DEPDES)
        FROM Dpe dpe
        JOIN Dep d ON d.ENT = dpe.ENT AND d.EJE = dpe.EJE AND d.DEPCOD = dpe.DEPCOD
        WHERE dpe.ENT = :ENT AND dpe.EJE = :EJE AND dpe.PERCOD = :PERCOD
    """)
    List<DepCodDesDto> personaServices(
        @Param("ENT") Integer ENT,
        @Param("EJE") String EJE,
        @Param("PERCOD") String PERCOD
    );

    //deleting a persona's services
    @Transactional
    @Modifying
    @Query("""
        DELETE FROM Dpe d
        WHERE d.ENT = :ENT
        AND d.EJE = :EJE
        AND d.DEPCOD = :DEPCOD
        AND d.PERCOD = :PERCOD
    """)
    void deletePersonaService(
        @Param("ENT") Integer ENT,
        @Param("EJE") String EJE,
        @Param("DEPCOD") String DEPCOD,
        @Param("PERCOD") String PERCOD
    );
}