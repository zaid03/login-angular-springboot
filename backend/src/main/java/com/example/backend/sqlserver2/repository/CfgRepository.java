package com.example.backend.sqlserver2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.backend.sqlserver2.model.Cfg;
import com.example.backend.sqlserver2.model.CfgId;

@Repository
public interface CfgRepository extends JpaRepository<Cfg, CfgId> {
    
    //method to ejercicio in Cfg table by entidad and CFGEST
    @Query("SELECT c.EJE FROM Cfg c WHERE c.ENT = :ent AND c.CFGEST = 0")
    List<String> findEjeByEntAndCfgest(@Param("ent") int ent);
}
