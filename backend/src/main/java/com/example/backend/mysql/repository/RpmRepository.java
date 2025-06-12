package com.example.backend.mysql.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.backend.mysql.model.Rpm;

@Repository
public interface RpmRepository extends JpaRepository<Rpm, Long> {

    @Query("SELECT r.MNUCOD FROM Rpm r WHERE r.PERCOD = :PERCOD AND r.APLCOD = 7")
    List<String> findMNUCODsByPERCOD(@Param("PERCOD") String PERCOD);

}
