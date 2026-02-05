package com.example.backend.sqlserver2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.backend.sqlserver2.model.Cog;
import com.example.backend.sqlserver2.model.CogId;
import com.example.backend.dto.CogCgeProjection;

import java.util.List;

public interface CogRepository extends JpaRepository<Cog, CogId> {
    //selecting centro gestores for contrato
    List<CogCgeProjection> findAllByENTAndEJEAndCONCOD(Integer ENT, String EJE, Integer CONCOD);
}
