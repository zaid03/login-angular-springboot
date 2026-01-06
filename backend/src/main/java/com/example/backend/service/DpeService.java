package com.example.backend.service;

import com.example.backend.dto.PersonaServiceRequest;
import com.example.backend.sqlserver2.model.Dpe;
import com.example.backend.sqlserver2.model.DpeId;
import com.example.backend.sqlserver2.repository.DpeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class DpeService {

    private final DpeRepository dpeRepository;

    public DpeService(DpeRepository dpeRepository) {
        this.dpeRepository = dpeRepository;
    }

    @Transactional
    public void savePersonaServices(PersonaServiceRequest req) {
        System.out.println(">>> savePersonaServices called: " + req);

        if (req.getServices() == null || req.getServices().isEmpty()) {
            System.out.println(">>> services is null or empty, nothing to save");
            return;
        }

        Integer ent = req.getEnt();
        String eje = req.getEje();
        String percod = req.getPercod();

        System.out.println(">>> ent=" + ent + " eje=" + eje + " percod=" + percod);

        for (String depcod : req.getServices()) {
            DpeId id = new DpeId(ent, eje, depcod, percod);
            if (dpeRepository.existsById(id)) {
                System.out.println(">>> DPE already exists, skipping: " + id);
                continue;
            }

            Dpe dpe = new Dpe();
            dpe.setENT(ent);
            dpe.setEJE(eje);
            dpe.setDEPCOD(depcod);
            dpe.setPERCOD(percod);

            dpeRepository.save(dpe);
            System.out.println(">>> INSERTED DPE: " + id);
        }
    }
}