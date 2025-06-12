package com.example.backend.sqlserver.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.backend.dto.TpeDto;
import com.example.backend.sqlserver.model.Tpe;

@Repository
public interface TpeRepository extends JpaRepository<Tpe, Long> {

    @Query(value = "SELECT new com.example.backend.dto.TpeDto(t.TPENOM, t.TPETEL, t.TPETMO, t.TPECOE, t.TPEOBS) FROM Tpe t WHERE t.ENT = :ent AND t.TERCOD = :tercod")
    Optional<TpeDto> findByENTAndTERCOD(
        @Param("ent") Integer ent,
        @Param("tercod") Integer tercod
    );
}
