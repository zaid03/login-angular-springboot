package com.example.backend.sqlserver2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.TpeDto;
import com.example.backend.sqlserver2.model.Tpe;
import com.example.backend.sqlserver2.model.TpeId;

@Repository
public interface TpeRepository extends JpaRepository<Tpe, TpeId> {

    // Custom query to find Tpe by ENT and TERCOD
   @Query("SELECT new com.example.backend.dto.TpeDto(t.tercod, t.tpecod, t.tpenom, t.tpetel, t.tpetmo, t.tpecoe, t.tpeobs) FROM Tpe t WHERE t.ent = :ent AND t.tercod = :tercod")
    List<TpeDto> findDtoByEntAndTercod(
        @Param("ent") Integer ent,
        @Param("tercod") Integer tercod
    );

    // modifying the data
    @Modifying
    @Transactional
    @Query("""
        UPDATE Tpe t SET
            t.tpenom = :tpenom, 
            t.tpetel = :tpetel, 
            t.tpetmo = :tpetmo, 
            t.tpecoe = :tpecoe, 
            t.tpeobs = :tpeobs 
        WHERE
            t.ent = :ent 
            AND t.tercod = :tercod 
            AND t.tpecod = :tpecod
    """)
    int updatePersona(
        @Param("tpenom") String tpenom,
        @Param("tpetel") String tpetel,
        @Param("tpetmo") String tpetmo,
        @Param("tpecoe") String tpecoe,
        @Param("tpeobs") String tpeobs,
        @Param("ent") Integer ent,
        @Param("tercod") Integer tercod,
        @Param("tpecod") Integer tpecod
    );

    // Deleting data
    void deleteByEntAndTercodAndTpecod(Integer ent, Integer tercod, Integer Tpecod);
}
