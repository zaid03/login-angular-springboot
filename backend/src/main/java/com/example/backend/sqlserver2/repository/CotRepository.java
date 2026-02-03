package com.example.backend.sqlserver2.repository;

import com.example.backend.service.CotContratoProjection;
import com.example.backend.sqlserver2.model.Cot;
import com.example.backend.sqlserver2.model.CotId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CotRepository extends JpaRepository<Cot, CotId> {
    //selecting all contratos
    List<CotContratoProjection> findAllProjectedBy();
}