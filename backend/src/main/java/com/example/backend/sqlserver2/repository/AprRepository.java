package com.example.backend.sqlserver2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.AprDto;
import com.example.backend.sqlserver2.model.Apr;

@Repository
public interface AprRepository extends JpaRepository<Apr, Integer> {

    //select * from APR where ENT = :ent and TERCOD = :tercod
    @Query("SELECT new com.example.backend.dto.AprDto(a.ENT, a.TERCOD, a.AFACOD, a.ASUCOD, a.ARTCOD, a.APRREF, a.APRPRE, a.APRUEM, a.APROBS, a.APRACU) FROM Apr a WHERE a.ENT = :ent  AND a.TERCOD = :tercod")
    List<AprDto> findByEnt(
        @Param("ent") Integer ent, @Param("tercod") Integer tercod
    );

    //modifying the data
    @Modifying
    @Transactional
    @Query("UPDATE Apr a SET a.APRREF = :aprref, a.APRPRE = :aprpre, a.APRUEM = :apruem, a.APROBS = :aprobs, a.APRACU = :apracud " +
        "WHERE a.ENT = :ent AND a.TERCOD = :tercod AND a.AFACOD = :afacod AND a.ASUCOD = :asucod AND a.ARTCOD = :artcod")
    int updateOneApr(
        @Param("aprref") String aprref,
        @Param("aprpre") Double aprpre,
        @Param("apruem") Double apruem,
        @Param("aprobs") String aprobs,
        @Param("apracud") Integer apracud,
        @Param("ent") Integer ent,
        @Param("tercod") Integer tercod,
        @Param("afacod") String afacod,
        @Param("asucod") String asucod,
        @Param("artcod") String artcod
    );

    //deleting data
    @Transactional
    int deleteByENTAndTERCODAndAFACODAndASUCODAndARTCOD(
        Integer ent, Integer tercod, String afacod, String asucod, String artcod
    );
}
