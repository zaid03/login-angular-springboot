package com.example.backend.sqlserver2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.dto.DepWithCgeView;
import com.example.backend.sqlserver2.model.Dep;
import com.example.backend.sqlserver2.model.DepId;

@Repository
public interface DepRepository  extends JpaRepository<Dep, DepId> {
    // for deleting centro gestor
    long countByENTAndEJEAndCGECOD(Integer ENT, String EJE, String CGECOD);

    // fetching all services
    List<Dep> findByENTAndEJE(Integer ENT, String EJE);

    // fetching services for a user (main panel)
    List<DepWithCgeView> findByENTAndEJEAndDpes_PERCOD(Integer ent, String eje, String percod);

    // for search
    List<Dep> findByENTAndEJEAndDEPCODContainingOrDEPDESContaining(Integer ENT, String EJE, String search, String search1);
    List<Dep> findByENTAndEJEAndCGECOD(Integer ENT, String EJE, String CGECOD);

    // for adding a service
    List<Dep> findByENTAndEJEAndDEPCOD(int ent, String eje, String depcod);

    // needed for deleting centro de coste
    long countByENTAndEJEAndCCOCOD(Integer ENT, String EJE, String CCOCOD);
}