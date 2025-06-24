package com.example.backend.sqlserver.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.sqlserver.model.Art;

@Repository
public interface ArtRepository extends JpaRepository<Art, String> {
    List<Art> findByENTAndAFACODAndASUCOD(int ent, String afacod, String asucod);
}
