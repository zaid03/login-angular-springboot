package com.example.backend.repository;

import com.example.backend.model.Pua;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

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
