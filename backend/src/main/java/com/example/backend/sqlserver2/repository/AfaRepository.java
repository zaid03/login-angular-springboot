package com.example.backend.sqlserver2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.sqlserver2.model.Afa;
import com.example.backend.sqlserver2.model.AfaId;

@Repository
public interface AfaRepository extends JpaRepository<Afa, AfaId> {

    // Method to find Afa records by ENT and AFACOD
    List<Afa> findByENTAndAFACOD(int ent, String afacod);

    // Method to find afa records by ent and afacod using like
    List<Afa> findByENTAndAFADESContaining(int ent, String afades);

    //find an art name
    @Query("SELECT a FROM Afa a WHERE a.ENT = :ent AND a.AFACOD = :afacod")
    List <Afa> getArtName(@Param("ent") int ent, @Param("afacod") String afacod);

    //find familias by ent
    List<Afa> findByENT(int ent);

    //update description of familias
    @Modifying
    @Transactional
    @Query("""
        UPDATE Afa a
        SET
            a.AFADES = :AFADES
        WHERE
            a.ENT = :ENT
            AND a.AFACOD = :AFACOD
    """)
    int updateFamilia(
        @Param("AFADES") String AFADES,
        @Param("ENT") Integer ENT,
        @Param("AFACOD") String AFACOD
    );

    //deleting a familia
    @Modifying
    @Transactional
    @Query("""
        DELETE FROM Afa a
        WHERE a.ENT = :ENT
          AND a.AFACOD = :AFACOD
    """)
    int deleteByEntAndAfacod(
        @Param("ENT") Integer ENT,
        @Param("AFACOD") String AFACOD
    );
}
