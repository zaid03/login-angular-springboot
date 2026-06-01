package com.example.backend.sqlserver2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.service.CotContratoProjection;
import com.example.backend.sqlserver2.model.Cot;
import com.example.backend.sqlserver2.model.CotId;

public interface CotRepository extends JpaRepository<Cot, CotId> {
    //selecting all contratos and search todos
    List<CotContratoProjection> findAllProjectedByConnCONTIPAndConnENTAndConnEJE(Integer CONTIP, Integer ent, String eje);

    //searching by todos 
    List<CotContratoProjection> findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONCOD(Integer CONTIP, Integer ent, String eje, Integer concod);

    List<CotContratoProjection> findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONDES(Integer CONTIP, Integer ent, String eje, String condes);

    //searching by no bloque
    List<CotContratoProjection> findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONCODAndConnCONBLONot(Integer CONTIP, Integer ent, String eje, Integer concod, Integer conblo);

    List<CotContratoProjection> findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONDESAndConnCONBLONot(Integer CONTIP, Integer ent, String eje, String condes, Integer conblo);

    List<CotContratoProjection> findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONBLONot(Integer CONTIP, Integer ent, String eje, Integer conblo);

    //searching by bloque
    List<CotContratoProjection> findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONCODAndConnCONBLO(Integer CONTIP, Integer ent, String eje, Integer concod, Integer conblo);

    List<CotContratoProjection> findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONDESAndConnCONBLO(Integer CONTIP, Integer ent, String eje, String condes, Integer conblo);

    List<CotContratoProjection> findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONBLO(Integer CONTIP, Integer ent, String eje, Integer conblo);
}