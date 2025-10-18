package com.example.backend.sqlserver2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
