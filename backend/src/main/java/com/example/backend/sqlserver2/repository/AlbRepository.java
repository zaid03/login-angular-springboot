package com.example.backend.sqlserver2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.backend.dto.AlbResumeDto;
import com.example.backend.sqlserver2.model.Alb;
import com.example.backend.sqlserver2.model.AlbId;

@Repository
public interface AlbRepository extends JpaRepository<Alb, AlbId> {
    @Query("""
        SELECT new com.example.backend.dto.AlbResumeDto(
            a.ALBNUM,
            a.ALBREF,
            a.ALBDAT,
            a.ALBBIM,
            a.SOLNUM,
            a.SOLSUB,
            a.ALBOBS
        )
        FROM Alb a
        JOIN Fac f
          ON f.ENT = a.ENT
         AND f.EJE = a.EJE
         AND f.FACNUM = a.FACNUM
        WHERE f.ENT = :ent
          AND f.EJE = :eje
          AND f.FACNUM = :facnum
    """)
    List<AlbResumeDto> findResumenByFactura(
            @Param("ent") Integer ent,
            @Param("eje") String eje,
            @Param("facnum") String facnum);
}
