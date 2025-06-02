package com.example.backend.repository;

import com.example.backend.model.Rpm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface RpmRepository extends JpaRepository<Rpm, Long> {

    @Query("SELECT r.MNUCOD FROM Rpm r WHERE r.PERCOD = :PERCOD AND r.APLCOD = 7")
    List<String> findMNUCODsByPERCOD(@Param("PERCOD") String PERCOD);

}
