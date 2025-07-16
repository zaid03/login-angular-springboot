package com.example.backend.sqlserver1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.backend.sqlserver1.model.Pua;

@Repository
public interface PuaRepository extends JpaRepository<Pua, Long> {

    @Query(value = """
        SELECT 
            p.USUCOD, p.APLCOD, p.ENTCOD, p.PERCOD,
            e.ENTNOM, e.ENTNIF
        FROM 
            Pua p
        JOIN 
            Ent e ON p.ENTCOD = e.ENTCOD
        WHERE 
            p.USUCOD = :usucod
            AND p.APLCOD = 7
        """, nativeQuery = true)
    List<Object[]> findByUsucodAndAplcod7(@Param("usucod") String usucod);
}
