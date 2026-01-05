package com.example.backend.sqlserver2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.sqlserver2.model.Per;

@Repository
public interface PerRepository extends JpaRepository<Per, String> {

    //for search in personas first case
    List<Per> findByPERCODOrPERNOMContaining(String PERCOD, String PERNOM);

    //for search in personas second case
    List<Per> findByPERNOMContaining(String PERNOM);

    //modifying a persona
    @Modifying
    @Transactional
    @Query("""
        UPDATE Per SET 
            PERNOM = :PERNOM, 
            PERCOE = :PERCOE, 
            PERTEL = :PERTEL, 
            PERTMO = :PERTMO, 
            PERCAR = :PERCAR, 
            PEROBS = :PEROBS 
        WHERE 
            PERCOD = :PERCOD
    """)
    int updatePersona(
        @Param("PERNOM") String PERNOM,
        @Param("PERCOE") String PERCOE,
        @Param("PERTEL") String PERTEL,
        @Param("PERTMO") String PERTMO,
        @Param("PERCAR") String PERCAR,
        @Param("PEROBS") String PEROBS,
        @Param("PERCOD") String PERCOD
    );
}