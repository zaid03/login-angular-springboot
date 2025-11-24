package com.example.backend.sqlserver2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.backend.dto.FdtResumeDto;
import com.example.backend.sqlserver2.model.Fdt;
import com.example.backend.sqlserver2.model.FdtId;

@Repository
public interface FdtRepository extends JpaRepository<Fdt, FdtId> {

    @Query("""
        SELECT new com.example.backend.dto.FdtResumeDto(
            ft.FDTARE, 
            ft.FDTORG, 
            ft.FDTFUN, 
            ft.FDTECO, 
            ft.FDTBSE, 
            ft.FDTPRE, 
            ft.FDTDTO, 
            ft.FDTTXT
        )
        FROM Fdt ft
        JOIN Fac fa
            ON fa.ENT = ft.ENT
            AND fa.EJE = ft.EJE
            AND fa.FACNUM = ft.FACNUM
        WHERE fa.ENT = :ent
            AND fa.EJE = :eje
            AND fa.FACNUM = :facnum
    """)
    List<FdtResumeDto> findByFdt(
            @Param("ent") Integer ent,
            @Param("eje") String eje,
            @Param("facnum") String facnum);
    
}
