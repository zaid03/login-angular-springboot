package com.example.backend.sqlserver2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.sqlserver2.model.Fac;

@Repository
public interface FacRepository extends JpaRepository<Fac, Integer>{
    //for the main list
    List<Fac> findByENTAndEJE(Integer ENT, Integer EJE);
}