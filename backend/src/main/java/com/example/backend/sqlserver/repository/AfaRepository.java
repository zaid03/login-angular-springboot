package com.example.backend.sqlserver.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.sqlserver.model.Afa;

@Repository
public interface AfaRepository extends JpaRepository<Afa, String> {

    List<Afa> findByENT(int ent);
}