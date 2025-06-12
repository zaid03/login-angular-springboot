package com.example.backend.sqlserver.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.backend.dto.AprDto;
import com.example.backend.sqlserver.model.Apr;

@Repository
public interface AprRepository extends JpaRepository<Apr, Integer> {

    @Query("SELECT new com.example.backend.dto.AprDto(a.ENT, a.TERCOD, a.AFACOD, a.ASUCOD, a.ARTCOD, a.APRREF, a.APRPRE, a.APRUEM, a.APROBS) FROM Apr a WHERE a.ENT = :ent  AND a.TERCOD = :tercod")
    Optional<AprDto> findByEnt(
        @Param("ent") Integer ent, @Param("tercod") Integer tercod
    );
}
