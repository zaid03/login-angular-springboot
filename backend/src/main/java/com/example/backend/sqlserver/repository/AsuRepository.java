package com.example.backend.sqlserver.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.sqlserver.model.Asu;

@Repository
public interface AsuRepository extends JpaRepository<Asu, String> {

    List<Asu> findByENTAndAFACOD(int ent, String afacod);
}