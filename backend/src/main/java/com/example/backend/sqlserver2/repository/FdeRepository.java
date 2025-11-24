package com.example.backend.sqlserver2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.backend.dto.FdeResumeDto;
import com.example.backend.sqlserver2.model.Fde;
import com.example.backend.sqlserver2.model.FdeId;

@Repository
public interface FdeRepository extends JpaRepository<Fde, FdeId> {

    @Query("""
        SELECT new com.example.backend.dto.FdeResumeDto(
            fd.FDEREF, 
            fd.FDEECO, 
            fd.FDEIMP, 
            fd.FDEDIF
        )
        FROM Fde fd
        JOIN Fac fa
            ON fa.ENT = fd.ENT
            AND fa.EJE = fd.EJE
            AND fa.FACNUM = fd.FACNUM
        WHERE fa.ENT = :ent
            AND fa.EJE = :eje   
            AND fa.FACNUM = :facnum
    """)
    List<FdeResumeDto> findByFactura(
            @Param("ent") Integer ent,
            @Param("eje") String eje,
            @Param("facnum") String facnum);
}
