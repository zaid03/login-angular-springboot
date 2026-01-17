package com.example.backend.sqlserver2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.servicesPerPersons;
import com.example.backend.dto.DepCodDesDto;
import com.example.backend.sqlserver2.model.Dpe;
import com.example.backend.sqlserver2.model.DpeId;

@Repository
public interface DpeRepository extends JpaRepository<Dpe, DpeId> {
    //selecting personas for servicios
    @Query("""
        SELECT
            p.PERCOD,
            p.PERNOM
        FROM Dpe d
        JOIN Per p ON p.PERCOD = d.PERCOD
        WHERE d.ENT = :ENT
            AND d.EJE = :EJE
            AND d.DEPCOD = :DEPCOD  
    """)
    List<Object[]> fetchPersonas(
        @Param("ENT") Integer ENT,
        @Param("EJE") String EJE,
        @Param("DEPCOD") String DEPCOD
    );

    //selecting a persona's services
    @Query("""
        SELECT new com.example.backend.dto.DepCodDesDto(d.DEPCOD, d.DEPDES)
        FROM Dpe dpe
        JOIN Dep d ON d.ENT = dpe.ENT AND d.EJE = dpe.EJE AND d.DEPCOD = dpe.DEPCOD
        WHERE dpe.ENT = :ENT AND dpe.EJE = :EJE AND dpe.PERCOD = :PERCOD
    """)
    List<DepCodDesDto> personaServices(
        @Param("ENT") Integer ENT,
        @Param("EJE") String EJE,
        @Param("PERCOD") String PERCOD
    );

    //deleting a persona's services
    @Transactional
    @Modifying
    @Query("""
        DELETE FROM Dpe d
        WHERE d.ENT = :ENT
        AND d.EJE = :EJE
        AND d.DEPCOD = :DEPCOD
        AND d.PERCOD = :PERCOD
    """)
    void deletePersonaService(
        @Param("ENT") Integer ENT,
        @Param("EJE") String EJE,
        @Param("DEPCOD") String DEPCOD,
        @Param("PERCOD") String PERCOD
    );

    //needed for copy perfil function and selecting centro getor for login
    List<Dpe> findByENTAndEJEAndPERCOD(Integer ENT, String EJE, String PERCOD);

    //deleting a persona
    @Transactional
    @Modifying
    @Query("""
        DELETE FROM Dpe d
        WHERE d.ENT = :ENT
        AND d.EJE = :EJE
        AND d.PERCOD = :PERCOD
    """)
    int deletePersonaServices(
        @Param("ENT") Integer ENT,
        @Param("EJE") String EJE,
        @Param("PERCOD") String PERCOD
    );

    //selecting services per personas 
    @Query("""
        SELECT new com.example.backend.dto.servicesPerPersons(
            D.PERCOD, 
            P.PERNOM, 
            D.DEPCOD, 
            S.DEPDES, 
            S.DEPALM, 
            S.DEPCOM, 
            S.DEPINT, 
            G.CGECOD, 
            G.CGEDES
        )
        FROM 
            Dpe D, Dep S, Per P, Cge G
        WHERE 
            D.ENT = :ENT 
            AND D.EJE = :EJE
        AND 
            D.ENT = S.ENT 
        AND 
            S.EJE = G.EJE 
        AND 
            D.DEPCOD = S.DEPCOD
        AND 
            D. PERCOD = P. PERCOD
        AND 
            S.ENT = G.ENT 
        AND 
            S.EJE = G.EJE 
        AND 
            S.CGECOD = G.CGECOD
    """)
    List<servicesPerPersons> findByENTAndEJE(
        @Param("ENT") Integer ENT,
        @Param("EJE") String EJE
    );

    //searching services per personas
    @Query("""
        SELECT new com.example.backend.dto.servicesPerPersons(
            D.PERCOD, 
            P.PERNOM, 
            D.DEPCOD, 
            S.DEPDES, 
            S.DEPALM, 
            S.DEPCOM, 
            S.DEPINT, 
            G.CGECOD, 
            G.CGEDES
        )
        FROM Dpe D
        JOIN Dep S ON D.ENT = S.ENT AND D.DEPCOD = S.DEPCOD
        JOIN Per P ON D.PERCOD = P.PERCOD
        JOIN Cge G ON S.ENT = G.ENT AND S.EJE = G.EJE AND S.CGECOD = G.CGECOD
        WHERE D.ENT = :ENT
        AND D.EJE = :EJE
        AND (
            (:servicio IS NULL)
            OR (
            (LENGTH(:servicio) <= 8 AND D.DEPCOD LIKE CONCAT('%', :servicio, '%'))
            OR (LENGTH(:servicio) > 6 AND S.DEPDES LIKE CONCAT('%', :servicio, '%'))
            )
        )
        AND (
            (:persona IS NULL)
            OR (
            (LENGTH(:persona) <= 20 AND D.PERCOD = :persona)
            OR (P.PERNOM LIKE CONCAT('%', :persona, '%'))
            )
        )
        AND (:cgecod IS NULL OR G.CGECOD = :cgecod)
        AND (
            (:perfil IS NULL)
            OR (:perfil = 'ALMACEN' AND S.DEPALM = 1)
            OR (:perfil = 'COMPRADOR' AND S.DEPCOM = 1)
            OR (:perfil = 'CONTABILIDAD' AND S.DEPINT = 1)
            OR (:perfil = 'CENTRO' AND S.DEPALM = 0 AND S.DEPCOM = 0 AND S.DEPINT = 0)
        )
    """) 
    List<servicesPerPersons> searchServicesPerPersons(
        Integer ENT,
        String EJE,
        String servicio,
        String persona,
        String cgecod,
        String perfil
    );
}