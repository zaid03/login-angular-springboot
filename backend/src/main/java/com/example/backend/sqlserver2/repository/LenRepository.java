package com.example.backend.sqlserver2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.backend.sqlserver2.model.Len;

import java.util.List;
@Repository
public interface LenRepository extends JpaRepository<Len, Integer> {
    //query needed for adding a lugar de entrega
    @Query("SELECT COALESCE(MAX(l.LENCOD), 0) FROM Len l")
    Integer findMaxLencod();

    //update lugar 
    @Modifying 
    @Transactional
    @Query("""
        UPDATE Len SET 
            LENDES = :LENDES, 
            LENTXT = :LENTXT 
        WHERE LENCOD = :LENCOD 
    """)
    int updateLugar(
        @Param("LENDES") String LENDES,
        @Param("LENTXT") String LENTXT,
        @Param("LENCOD") Integer LENCOD
    );

    //search with lencod
    @Query("""
        SELECT l
        FROM Len l 
        WHERE l.LENCOD = :LENCOD 
    """)
    List<Len> filterByLencod(
        @Param("LENCOD") Integer LENCOD
    );

    //search with lendes
    @Query("""
        SELECT l 
        FROM Len l 
        WHERE l.LENDES like CONCAT('%', :LENDES, '%')
    """)
    List<Len> filterByLendes(
        @Param("LENDES") String LENDES
    );
}