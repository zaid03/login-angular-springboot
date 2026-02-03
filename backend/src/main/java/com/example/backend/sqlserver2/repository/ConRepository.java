package com.example.backend.sqlserver2.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.sqlserver2.model.Conn;
import com.example.backend.sqlserver2.model.ConId;

public interface ConRepository extends JpaRepository<Conn, ConId> {

}
