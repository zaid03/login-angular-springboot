package com.example.backend.sqlserver2.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.dto.albFacturaDto;
import com.example.backend.sqlserver2.model.Alb;
import com.example.backend.sqlserver2.model.AlbId;

@Repository
public interface AlbRepository extends JpaRepository<Alb, AlbId> {
    //fetch albaranes for facturas
    List<Alb> findByENTAndEJEAndFACNUM(Integer ent, String eje, Integer facnum);

    //fetching albaranes for adding to a factura
    List<albFacturaDto> findByENTAndTERCODAndDep_EJEAndDep_CGECOD(Integer ent, Integer tercod, String eje, String cgecod);

    //searching in albaranes for adding to a factura
    List<albFacturaDto> findByENTAndTERCODAndALBDATGreaterThanEqualAndDep_EJEAndDep_CGECOD(Integer ent, Integer tercod, LocalDateTime albdat, String eje, String cgecod);
    List<albFacturaDto> findByENTAndTERCODAndALBDATLessThanEqualAndDep_EJEAndDep_CGECOD(Integer ent, Integer tercod, LocalDateTime albdat, String eje, String cgecod);
}