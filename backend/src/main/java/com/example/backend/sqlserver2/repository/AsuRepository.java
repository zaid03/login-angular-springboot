package com.example.backend.sqlserver2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.sqlserver2.model.Asu;
import com.example.backend.sqlserver2.model.AsuId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface AsuRepository extends JpaRepository<Asu, AsuId> {

    // Method to find Asu records by ENT and AFACOD and ASUCOD
    @Query("SELECT a FROM Asu a WHERE a.ENT = :ent AND (a.AFACOD = :afacod OR a.ASUCOD = :asucod)")
    List<Asu> findByEntAndAfacodOrAsucod(@Param("ent") int ent, @Param("afacod") String afacod, @Param("asucod") String asucod);

    // Method to find Asu records by ENT and ASUCOD like
    List<Asu> findByENTAndASUDESContaining(int ent, String asudes);

    //find an art name
    @Query("SELECT a FROM Asu a WHERE a.ENT = :ent AND a.AFACOD = :afacod AND ASUCOD = :asucod")
    List <Asu> getArtName(@Param("ent") int ent, @Param("afacod") String afacod, @Param("asucod") String asucod);
}
