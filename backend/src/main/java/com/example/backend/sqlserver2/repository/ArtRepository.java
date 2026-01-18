package com.example.backend.sqlserver2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.backend.sqlserver2.model.Art;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ArtRepository extends JpaRepository<Art, String> {

    //fetch to find articulos
    List<Art> findByENTAndAFACOD(int ent, String afacod);
    List<Art> findByENTAndASUCOD(int ent, String asucod);
    List<Art> findByENTAndARTCOD(int ent, String artcod);

    // Method to find Art records by ENT and artdes like
    List<Art> findByENTAndARTDESContaining(int ent, String artdes);

    //find an art name
    List<Art> findByENTAndAFACODAndASUCODAndARTCOD(int ent, String afacod, String asucod, String artcod);

    //delete a familia check
    Long countByENTAndAFACOD(Integer ent, String afacod);

    //delete a subfamilia check
    Long countByENTAndASUCOD(Integer ent, String asucod);
}
