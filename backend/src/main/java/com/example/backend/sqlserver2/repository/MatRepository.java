package com.example.backend.sqlserver2.repository;

import com.example.backend.sqlserver2.model.Mat;
import com.example.backend.sqlserver2.model.MatId;
import com.example.backend.dto.MatShortDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MatRepository extends JpaRepository<Mat, MatId> {

    @Query("SELECT DISTINCT new com.example.backend.dto.MatShortDto(mta.MTACOD, mta.MTADES) " +
           "FROM Mag mag " +
           "JOIN Mat mat ON mat.ENT = mag.ENT AND mat.MAGCOD = mag.MAGCOD " +
           "JOIN Mta mta ON mta.ENT = mat.ENT AND mta.MTACOD = mat.MTACOD " +
           "WHERE mag.ENT = :ent AND mag.DEPCOD = :depcod")
    List<MatShortDto> findDistinctMtaByEntAndDepcod(
        @Param("ent") Integer ent,
        @Param("depcod") String depcod
    );
}