package com.example.backend.sqlserver2.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.backend.dto.TpeDto;
import com.example.backend.sqlserver2.model.Tpe;
import com.example.backend.sqlserver2.model.TpeId;

@Repository
public interface TpeRepository extends JpaRepository<Tpe, TpeId> {

    // Custom query to find Tpe by ENT and TERCOD
   @Query("SELECT new com.example.backend.dto.TpeDto(t.tercod, t.tpenom, t.tpetel, t.tpetmo, t.tpecoe, t.tpeobs) FROM Tpe t WHERE t.ent = :ent AND t.tercod = :tercod")
    List<TpeDto> findDtoByEntAndTercod(
        @Param("ent") Integer ent,
        @Param("tercod") Integer tercod
    );

    // modifying the data
    List<Tpe> findByEntAndTercod(Integer ent, Integer tercod);

    // Deleting data
    void deleteByEntAndTercod(Integer ent, Integer tercod);
}
