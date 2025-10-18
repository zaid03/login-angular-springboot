package com.example.backend.sqlserver2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.backend.sqlserver2.model.Art;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ArtRepository extends JpaRepository<Art, String> {

    // Method to find Art records by ENT and AFACOD and artcod
    @Query("SELECT a FROM Art a WHERE a.ENT = :ent AND (a.AFACOD = :afacod OR a.ASUCOD = :asucod OR a.ARTCOD = :artcod)")
    List<Art> findByENTAndAFACODAndASUCODAndARTCOD(@Param("ent") int ent, @Param("afacod") String afacod, @Param("asucod") String asucod, @Param("artcod") String artcod);

    // Method to find Art records by ENT and artdes like
    List<Art> findByENTAndARTDESContaining(int ent, String artdes);

    //find an art name
    @Query("SELECT a FROM Art a WHERE a.ENT = :ent AND a.AFACOD = :afacod AND a.ASUCOD = :asucod AND a.ARTCOD = :artcod")
    List<Art> findArtName(@Param("ent") int ent, @Param("afacod") String afacod, @Param("asucod") String asucod, @Param("artcod") String artcod);
}
