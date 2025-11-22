package com.example.backend.sqlserver2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.backend.sqlserver2.model.Dep;
import com.example.backend.sqlserver2.model.DepId;

@Repository
public interface CentroGestor extends JpaRepository<Dep, DepId> {
    @Query(value = """
        SELECT DISTINCT 
            D.CGECOD, 
            D.DEPINT,
            G.CGEDES,
            G.CGECIC   
        FROM DEP D, DPE P, CGE G   
        WHERE P.DEPCOD = D.DEPCOD 
            AND P.ENT = D.ENT 
            AND P.EJE = D.EJE 
            AND G.ENT = D.ENT 
            AND G.EJE = D.EJE 
            AND G.CGECOD = D.CGECOD      
            AND P.PERCOD = :percod
            AND D.ENT = :ent   
            AND D.EJE = :eje
        """, nativeQuery = true)
    List<Object[]> findDepartmentsByUserAndEntity(
        @Param("percod") String percod,
        @Param("ent") Integer ent, 
        @Param("eje") String eje
    );
}
