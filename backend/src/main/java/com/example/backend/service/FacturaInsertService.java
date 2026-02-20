package com.example.backend.service;

import com.example.backend.dto.FacturaInsertDto;
import com.example.backend.sqlserver2.model.Cfg;
import com.example.backend.sqlserver2.model.Fac;
import com.example.backend.sqlserver2.model.Ter;
import com.example.backend.sqlserver2.repository.CfgRepository;
import com.example.backend.sqlserver2.repository.FacRepository;
import com.example.backend.sqlserver2.repository.TerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacturaInsertService {
    @Autowired
    private TerRepository terRepository;
    @Autowired
    private CfgRepository cfgRepository;
    @Autowired
    private FacRepository facRepository;

    public void insertFacturas(List<FacturaInsertDto> facturas) {
        for (FacturaInsertDto dto : facturas) {
            Ter ter = terRepository.findByENTAndTERNIF(dto.ENT, dto.tercero);
            if (ter == null) continue; // or throw error

            List<Cfg> cfgList = cfgRepository.findByENTAndEJE(dto.ENT, dto.EJE);
            if (cfgList == null || cfgList.isEmpty()) continue;
            Cfg cfg = cfgList.get(0);

            Fac fac = new Fac();
            fac.setENT(dto.ENT);
            fac.setEJE(dto.EJE);
            fac.setTERCOD(ter.getTERCOD());
            fac.setCGECOD(dto.CGECOD);
            fac.setFACIMP(dto.FACIMP);
            fac.setFACIEC(dto.FACIEC);
            fac.setFACIDI(dto.FACIDI);
            fac.setFACTDC(dto.FACTDC);
            fac.setFACANN(dto.FACANN);
            fac.setFACFAC(dto.FACFAC);
            fac.setFACDOC(dto.FACDOC);
            fac.setFACDAT(dto.FACDAT);
            fac.setFACTXT(dto.FACTXT);
            fac.setFACDTO(dto.FACDTO);
            fac.setFACFRE(dto.FACFRE);
            fac.setFACFPG(cfg.getCFGFPG());
            fac.setFACOPG(cfg.getCFGOPG());
            fac.setFACTPG(cfg.getCFGTPG());
            facRepository.save(fac);
        }
    }
}
