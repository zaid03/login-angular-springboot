package com.example.backend.sqlserver2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.backend.sqlserver2.model.Dep;
import com.example.backend.sqlserver2.model.DepId;

@Repository
public interface DepRepository  extends JpaRepository<Dep, DepId> {
    //for deleting centro gestor
    @Query(
        value = """
            SELECT count (*)
            FROM Dep
            WHERE 
                ENT = :ENT 
                AND EJE = :EJE 
                AND CGECOD = :CGECOD   
        """, nativeQuery = true
    )
    Long countServices(
        @Param("ENT") Integer ENT,
        @Param("EJE") String EJE,
        @Param("CGECOD") String CGECOD
    );
}
