package com.example.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.dto.PuaEntDTO;
import com.example.backend.sqlserver1.repository.PuaRepository;
@Service
public class PuaService {

    @Autowired
    private PuaRepository puaRepository;

    public List<PuaEntDTO> getFilteredData(String usucod) {
        List<Object[]> rows = puaRepository.findByUsucodAndAplcod7(usucod);
        List<PuaEntDTO> result = new ArrayList<>();

        for (Object[] row : rows) {
            String USUCOD = (String) row[0];
            int APLCOD = (Integer) row[1];
            int ENTCOD = (Integer) row[2];
            String PERCOD = (String) row[3];
            String ENTNOM = (String) row[4];
            // You can add ENTNIF if needed

            PuaEntDTO dto = new PuaEntDTO(USUCOD, APLCOD, ENTCOD, PERCOD, ENTNOM);
            result.add(dto);
        }

        return result;
    }
}
