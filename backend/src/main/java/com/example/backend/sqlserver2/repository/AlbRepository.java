package com.example.backend.sqlserver2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.sqlserver2.model.Alb;
import com.example.backend.sqlserver2.model.AlbId;

@Repository
public interface AlbRepository extends JpaRepository<Alb, AlbId> {
    //fetch albaranes for facturas
    List<Alb> findByENTAndEJEAndFACNUM(Integer ent, String eje, Integer facnum);
}