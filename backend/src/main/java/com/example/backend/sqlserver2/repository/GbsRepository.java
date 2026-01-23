package com.example.backend.sqlserver2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.sqlserver2.model.Gbs;
import com.example.backend.sqlserver2.model.GbsId;

@Repository
public interface  GbsRepository extends JpaRepository<Gbs, GbsId>{
    //for the main list of bolsa por cge
    List<Gbs> findByENTAndEJEAndCGECOD(int ent, String eje, String cgecod);

    //for deleting a centro gestor
    long countByENTAndEJEAndCGECOD(Integer ENT, String EJE, String CGECOD);

    //for main list of bolsa 
    List<Gbs> findByENTAndEJE(Integer ent, String eje);
}
