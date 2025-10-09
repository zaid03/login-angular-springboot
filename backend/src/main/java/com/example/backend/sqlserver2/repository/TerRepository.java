package com.example.backend.sqlserver2.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.backend.sqlserver2.model.Ter;

@Repository
public interface TerRepository extends JpaRepository<Ter, Integer> {
    //for the main list
    List<Ter> findByENT(int ent);

    //for the list filtered by TERCOD and option bloqueado
    @Query(value = "SELECT * FROM TER WHERE ENT = :ent AND TERCOD = :tercod AND TERBLO = 0", nativeQuery = true)
    List<Ter> findByENTAndTERCODAndTERBLOZero(
        @Param("ent") int ent,
        @Param("tercod") Integer tercod
    );

    //for the list filtered by TERCOD and option no bloqueado
    @Query("SELECT t FROM Ter t WHERE t.ENT = :ent AND t.TERCOD = :tercod AND (t.TERBLO <> :terblo OR t.TERBLO IS NULL)")
    List<Ter> findByENTAndTERCODAndTERBLONot(
        @Param("ent") int ent,
        @Param("tercod") Integer tercod,
        @Param("terblo") Integer terblo
    );

    //for the list filtered by TERNIF and option bloqueado
    @Query("SELECT t FROM Ter t WHERE t.ENT = :ent AND t.TERNIF LIKE %:ternif% AND t.TERBLO = 0")
    List<Ter> findByENTAndTERNIFAndTERBLO(
        @Param("ent") int ent,
        @Param("ternif") String ternif
    );

    //for the list filtered by TERNIF and option no bloqueado   
    @Query("SELECT t FROM Ter t WHERE t.ENT = :ent AND t.TERNIF LIKE %:ternif% AND (t.TERBLO <> :terblo OR t.TERBLO IS NULL)")
    List<Ter> findByENTAndTERNIFContainingAndTERBLONot(
        @Param("ent") int ent,
        @Param("ternif") String ternif,
        @Param("terblo") Integer terblo
    );

    //for the list filtered by TERNIF and TERNOM and TERALI bloqueado
    @Query("""
        SELECT t FROM Ter t 
        WHERE t.ENT = :ent 
        AND t.TERBLO = 0
        AND (
            t.TERNIF LIKE %:term% 
            OR t.TERNOM LIKE %:term% 
            OR t.TERALI LIKE %:term%
        )
        """)
    List<Ter> searchFiltered(
        @Param("ent") int ent,
        @Param("term") String term
    );

    //for the list filtered by TERNIF and TERNOM and TERALI no bloqueado
    @Query(value = """
        SELECT * FROM TER
        WHERE ENT = :ent 
          AND (
          TERBLO <> :terblo 
          OR TERBLO IS NULL)
          AND (
            TERNIF LIKE '%' + :term + '%'
            OR TERNOM LIKE '%' + :term + '%'
            OR TERALI LIKE '%' + :term + '%'
          )
        """, nativeQuery = true)
    List<Ter> searchByTerm(
        @Param("ent") int ent,
        @Param("term") String term,
        @Param("terblo") Integer terblo
    );

    //for the list filtered by TERNIF and TERNOM and TERALI bloqueado
    @Query(value = """
    SELECT * FROM TER 
    WHERE ENT = :ent 
      AND TERBLO = 0
      AND (
        TERNOM LIKE '%' + :term + '%'
        OR TERALI LIKE '%' + :term + '%'
      )
    """, nativeQuery = true)
    List<Ter> searchByNomOrAli(
        @Param("ent") int ent,
        @Param("term") String term
    );

    //for the list filtered by TERNIF and TERNOM and TERALI no bloqueado
    @Query(value = """
    SELECT * FROM TER 
    WHERE ENT = :ent 
      AND (TERBLO <> :terblo OR TERBLO IS NULL)
      AND (
        TERNOM LIKE '%' + :term + '%'
        OR TERALI LIKE '%' + :term + '%'
      )
    """, nativeQuery = true)
    List<Ter> findMatchingNomOrAli(
        @Param("ent") int ent,
        @Param("term") String term,
        @Param("terblo") Integer terblo
    );

    // For TERCOD, no TERBLO filter
    @Query("SELECT t FROM Ter t WHERE t.ENT = :ent AND t.TERCOD = :tercod")
    List<Ter> findByENTAndTERCOD(@Param("ent") int ent, @Param("tercod") Integer tercod);

    // For TERNIF, no TERBLO filter
    @Query("SELECT t FROM Ter t WHERE t.ENT = :ent AND t.TERNIF LIKE %:ternif%")
    List<Ter> findByENTAndTERNIF(@Param("ent") int ent, @Param("ternif") String ternif);

    // For search term, no TERBLO filter
    @Query("SELECT t FROM Ter t WHERE t.ENT = :ent AND (t.TERNIF LIKE %:term% OR t.TERNOM LIKE %:term% OR t.TERALI LIKE %:term%)")
    List<Ter> searchTodos(@Param("ent") int ent, @Param("term") String term);

    //for modifying a Ter record
    Optional<Ter> findByTERCOD(Integer tercod);

    //for selected proveedores to be added from sicalwin
    @Query(value = "SELECT ISNULL(MAX(TERCOD),0) + 1 FROM dbo.TER WITH (UPDLOCK, HOLDLOCK) WHERE ENT = :ent", nativeQuery = true)
    Integer findNextTercodForEnt(@Param("ent") int ent);
}
