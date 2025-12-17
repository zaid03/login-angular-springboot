package com.example.backend.sqlserver2.repository;

import com.example.backend.sqlserver2.model.Mag;
import com.example.backend.sqlserver2.model.MagId;
import com.example.backend.dto.MagShortDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface MagRepository extends JpaRepository<Mag, MagId> {

    //selecting almacen name
    @Query("SELECT new com.example.backend.dto.MagShortDto(m.MAGCOD, m.MAGNOM) " +"FROM Mag m WHERE m.ENT = :ent AND m.DEPCOD = :depcod")
    Optional<MagShortDto> findShortByEntAndDepcod(
        @Param("ent") Integer ent, 
        @Param("depcod") String depcod
    );
}