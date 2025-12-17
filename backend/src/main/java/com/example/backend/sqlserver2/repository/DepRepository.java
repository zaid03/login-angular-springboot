package com.example.backend.sqlserver2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

import com.example.backend.sqlserver2.model.Dep;
import com.example.backend.sqlserver2.model.DepId;

import java.util.List;

@Repository
public interface DepRepository  extends JpaRepository<Dep, DepId> {
    //for deleting centro gestor
    @Query(
        value = """
            SELECT count (*)
            FROM Dep
            WHERE 
                ENT = :ENT 
                AND EJE = :EJE 
                AND CGECOD = :CGECOD   
        """, nativeQuery = true
    )
    Long countServices(
        @Param("ENT") Integer ENT,
        @Param("EJE") String EJE,
        @Param("CGECOD") String CGECOD
    );

    //fetching all services
    List<Dep> findByENTAndEJE(int ent, String eje);

    //modifying a service
    @Modifying
    @Transactional
    @Query("""
        UPDATE Dep d
        SET 
            d.DEPDES = :DEPDES, 
            d.DEPALM = :DEPALM, 
            d.DEPCOM = :DEPCOM, 
            d.DEPINT = :DEPINT, 
            d.CCOCOD = :CCOCOD 
        where 
            d.ENT = :ENT
            AND d.EJE = :EJE
            AND d.DEPCOD = :DEPCOD
    """)
    int updateService(
        @Param("DEPDES") String DEPDES,
        @Param("DEPALM") Integer DEPALM,
        @Param("DEPCOM") Integer DEPCOM,
        @Param("DEPINT") Integer DEPINT,
        @Param("CCOCOD") String CCOCOD,
        @Param("ENT") Integer ENT,
        @Param("EJE") String EJE,
        @Param("DEPCOD") String DEPCOD
    );

    //for adding a service
    List<Dep> findByENTAndEJEAndDEPCOD(int ent, String eje, String depcod);

    //modifying the rest of the service
    @Modifying
    @Transactional
    @Query("""
        UPDATE Dep d
        SET 
            d.DEPD1C = :DEPD1C, 
            d.DEPD1D = :DEPD1D, 
            d.DEPD2C = :DEPD2C, 
            d.DEPD2D = :DEPD2D, 
            d.DEPD3C = :DEPD3C,
            d.DEPD3D = :DEPD3D,
            d.DEPDCO = :DEPDCO,
            d.DEPDEN = :DEPDEN
        where 
            d.ENT = :ENT
            AND d.EJE = :EJE
            AND d.DEPCOD = :DEPCOD  
    """)
    int updateServiceSecond(
        @Param("DEPD1C") String DEPD1C,
        @Param("DEPD1D") String DEPD1D,
        @Param("DEPD2C") String DEPD2C,
        @Param("DEPD2D") String DEPD2D,
        @Param("DEPD3C") String DEPD3C,
        @Param("DEPD3D") String DEPD3D,
        @Param("DEPDCO") String DEPDCO,
        @Param("DEPDEN") String DEPDEN,
        @Param("ENT") Integer ENT,
        @Param("EJE") String EJE,
        @Param("DEPCOD") String DEPCOD
    );
}
