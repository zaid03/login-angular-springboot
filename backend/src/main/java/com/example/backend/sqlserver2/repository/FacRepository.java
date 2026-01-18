package com.example.backend.sqlserver2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.backend.sqlserver2.model.Fac;

@Repository
public interface FacRepository extends JpaRepository<Fac, Integer>, JpaSpecificationExecutor<Fac>{
    //for the main list
    List<Fac> findByENTAndEJEAndCGECODOrderByFACFREAsc(Integer ent, String eje, String cgecod);
}