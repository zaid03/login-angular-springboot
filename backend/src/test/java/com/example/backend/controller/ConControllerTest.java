package com.example.backend.controller;

import com.example.backend.config.TestSecurityConfig;
import com.example.backend.config.TestExceptionHandler;
import com.example.backend.dto.ContratoDto;
import com.example.backend.service.CotContratoProjection;
import com.example.backend.sqlserver2.repository.CotRepository;
import com.example.backend.sqlserver2.repository.ConRepository;
import com.example.backend.sqlserver2.model.Cot;
import com.example.backend.sqlserver2.model.Conn;
import com.example.backend.sqlserver2.model.ConId;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ConController.class)
@ActiveProfiles("test")
@Import({TestSecurityConfig.class, TestExceptionHandler.class})
public class ConControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CotRepository cotRepository;

    @MockitoBean
    private ConRepository conRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private CotContratoProjection createMockProjection() {
        CotContratoProjection projection = new CotContratoProjection() {
            @Override
            public ConnInfo getConn() {
                return new ConnInfo() {
                    @Override
                    public Integer getCONCOD() { return 100; }
                    @Override
                    public String getCONLOT() { return "LOT001"; }
                    @Override
                    public String getCONDES() { return "Contract Description"; }
                    @Override
                    public LocalDateTime getCONFIN() { return LocalDateTime.now(); }
                    @Override
                    public LocalDateTime getCONFFI() { return LocalDateTime.now(); }
                    @Override
                    public Integer getCONBLO() { return 0; }
                };
            }

            @Override
            public TerInfo getTer() {
                return new TerInfo() {
                    @Override
                    public Integer getTERCOD() { return 200; }
                    @Override
                    public String getTERNOM() { return "Supplier Name"; }
                };
            }
        };
        return projection;
    }

    @Test
    void fetchContratos_returns200WithContratos() throws Exception {
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJE(3, 1, "E1"))
            .thenReturn(List.of(createMockProjection()));

        mockMvc.perform(get("/api/con/fetch-contratos/1/E1"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].concod").value(100))
            .andExpect(jsonPath("$[0].ternom").value("Supplier Name"));
    }

    @Test
    void fetchContratos_returns404WhenEmpty() throws Exception {
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJE(3, 1, "E1"))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/con/fetch-contratos/1/E1"))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(content().string("Sin resultado"));
    }

    @Test
    void fetchContratos_returns500OnException() throws Exception {
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJE(3, 1, "E1"))
            .thenThrow(new DataAccessResourceFailureException("Database error"));

        mockMvc.perform(get("/api/con/fetch-contratos/1/E1"))
            .andDo(print())
            .andExpect(status().isInternalServerError())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Error :")));
    }

    @Test
    void searchContratosCodigoBloqueado_returns200() throws Exception {
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONCODAndConnCONBLO(3, 1, "E1", 100, 0))
            .thenReturn(List.of(createMockProjection()));

        mockMvc.perform(get("/api/con/searchByCodigoBloque/1/E1/100"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].concod").value(100));
    }

    @Test
    void searchContratosCodigoBloqueado_returns404WhenEmpty() throws Exception {
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONCODAndConnCONBLO(3, 1, "E1", 100, 0))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/con/searchByCodigoBloque/1/E1/100"))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(content().string("Sin resultado"));
    }

    @Test
    void searchContratosCodigoBloqueado_returns500OnException() throws Exception {
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONCODAndConnCONBLO(3, 1, "E1", 100, 0))
            .thenThrow(new DataAccessResourceFailureException("Database error"));

        mockMvc.perform(get("/api/con/searchByCodigoBloque/1/E1/100"))
            .andDo(print())
            .andExpect(status().isInternalServerError());
    }

    @Test
    void searchContratosCodigoNoBloqueado_returns200() throws Exception {
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONCODAndConnCONBLONot(3, 1, "E1", 100, 0))
            .thenReturn(List.of(createMockProjection()));

        mockMvc.perform(get("/api/con/searchByCodigoNoBloque/1/E1/100"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].concod").value(100));
    }

    @Test
    void searchContratosCodigoNoBloqueado_returns404WhenEmpty() throws Exception {
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONCODAndConnCONBLONot(3, 1, "E1", 100, 0))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/con/searchByCodigoNoBloque/1/E1/100"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchContratosCodigoTodos_returns200() throws Exception {
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONCOD(3, 1, "E1", 100))
            .thenReturn(List.of(createMockProjection()));

        mockMvc.perform(get("/api/con/searchByCodigoTodos/1/E1/100"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].concod").value(100));
    }

    @Test
    void searchContratosCodigoTodos_returns404WhenEmpty() throws Exception {
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONCOD(3, 1, "E1", 100))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/con/searchByCodigoTodos/1/E1/100"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchContratosDescBloqueado_returns200() throws Exception {
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONDESContainingAndConnCONBLO(3, 1, "E1", "desc", 0))
            .thenReturn(List.of(createMockProjection()));

        mockMvc.perform(get("/api/con/searchByDescBloque/1/E1/desc"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].concod").value(100));
    }

    @Test
    void searchContratosDescBloqueado_returns404WhenEmpty() throws Exception {
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONDESContainingAndConnCONBLO(3, 1, "E1", "desc", 0))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/con/searchByDescBloque/1/E1/desc"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchContratosDescNoBloqueado_returns200() throws Exception {
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONDESContainingAndConnCONBLONot(3, 1, "E1", "desc", 0))
            .thenReturn(List.of(createMockProjection()));

        mockMvc.perform(get("/api/con/searchByDescNoBloque/1/E1/desc"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].concod").value(100));
    }

    @Test
    void searchContratosDescNoBloqueado_returns404WhenEmpty() throws Exception {
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONDESContainingAndConnCONBLONot(3, 1, "E1", "desc", 0))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/con/searchByDescNoBloque/1/E1/desc"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchContratosDescTodos_returns200() throws Exception {
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONDESContaining(3, 1, "E1", "desc"))
            .thenReturn(List.of(createMockProjection()));

        mockMvc.perform(get("/api/con/searchByDescTodos/1/E1/desc"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].concod").value(100));
    }

    @Test
    void searchContratosDescTodos_returns404WhenEmpty() throws Exception {
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONDESContaining(3, 1, "E1", "desc"))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/con/searchByDescTodos/1/E1/desc"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchContratosBloqu_returns200() throws Exception {
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONBLO(3, 1, "E1", 0))
            .thenReturn(List.of(createMockProjection()));

        mockMvc.perform(get("/api/con/searchByBloqu/1/E1"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].concod").value(100));
    }

    @Test
    void searchContratosBloqu_returns404WhenEmpty() throws Exception {
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONBLO(3, 1, "E1", 0))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/con/searchByBloqu/1/E1"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchContratosNobloq_returns200() throws Exception {
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONBLONot(3, 1, "E1", 0))
            .thenReturn(List.of(createMockProjection()));

        mockMvc.perform(get("/api/con/searchByNobloq/1/E1"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].concod").value(100));
    }

    @Test
    void searchContratosNobloq_returns404WhenEmpty() throws Exception {
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONBLONot(3, 1, "E1", 0))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/con/searchByNobloq/1/E1"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void updateContrato_returns204OnSuccess() throws Exception {
        ConId id = new ConId(1, "E1", 100);
        Conn conn = new Conn();
        conn.setCONCOD(100);

        when(conRepository.findById(id)).thenReturn(Optional.of(conn));
        when(conRepository.save(any(Conn.class))).thenReturn(conn);

        String payload = objectMapper.writeValueAsString(Map.of(
            "CONBLO", 1,
            "CONFIN", "2026-03-21T10:00:00",
            "CONFFI", "2026-03-21T10:00:00",
            "CONDES", "Updated Contract"
        ));

        mockMvc.perform(patch("/api/con/update-contrato/1/E1/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andDo(print())
            .andExpect(status().isNoContent());

        verify(conRepository).save(any(Conn.class));
    }

    @Test
    void updateContrato_returns400OnMissingCondes() throws Exception {
        ConId id = new ConId(1, "E1", 100);

        String payload = objectMapper.writeValueAsString(Map.of(
            "CONBLO", 1,
            "CONFIN", "2026-03-21T10:00:00",
            "CONFFI", "2026-03-21T10:00:00"
        ));

        mockMvc.perform(patch("/api/con/update-contrato/1/E1/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Faltan datos obligatorios."));
    }

    @Test
    void updateContrato_returns404WhenNotFound() throws Exception {
        ConId id = new ConId(1, "E1", 100);

        when(conRepository.findById(id)).thenReturn(Optional.empty());

        String payload = objectMapper.writeValueAsString(Map.of(
            "CONBLO", 1,
            "CONFIN", "2026-03-21T10:00:00",
            "CONFFI", "2026-03-21T10:00:00",
            "CONDES", "Updated Contract"
        ));

        mockMvc.perform(patch("/api/con/update-contrato/1/E1/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(content().string("Sin resultado"));
    }

    @Test
    void updateContrato_returns500OnException() throws Exception {
        ConId id = new ConId(1, "E1", 100);

        when(conRepository.findById(id)).thenThrow(new DataAccessResourceFailureException("Database error"));

        String payload = objectMapper.writeValueAsString(Map.of(
            "CONBLO", 1,
            "CONFIN", "2026-03-21T10:00:00",
            "CONFFI", "2026-03-21T10:00:00",
            "CONDES", "Updated Contract"
        ));

        mockMvc.perform(patch("/api/con/update-contrato/1/E1/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andDo(print())
            .andExpect(status().isInternalServerError())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Error :")));
    }

    @Test
    void addContrato_returns204OnSuccess() throws Exception {
        Conn conn = new Conn();
        when(conRepository.findFirstByENTAndEJEOrderByCONCODDesc(1, "E1"))
            .thenReturn(Optional.empty());
        when(conRepository.save(any(Conn.class))).thenReturn(conn);
        when(cotRepository.save(any(Cot.class))).thenReturn(new Cot());

        String payload = objectMapper.writeValueAsString(Map.of(
            "ENT", 1,
            "EJE", "E1",
            "CONLOT", "LOT001",
            "CONBLO", 0,
            "CONFIN", "2026-03-21T10:00:00",
            "CONFFI", "2026-03-21T10:00:00",
            "CONDES", "New Contract",
            "TERCOD", 200
        ));

        mockMvc.perform(post("/api/con/add-contrato")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andDo(print())
            .andExpect(status().isNoContent());

        verify(conRepository).save(any(Conn.class));
        verify(cotRepository).save(any(Cot.class));
    }

    @Test
    void addContrato_returns400OnMissingENT() throws Exception {
        String payload = objectMapper.writeValueAsString(Map.of(
            "EJE", "E1",
            "CONLOT", "LOT001",
            "CONDES", "New Contract",
            "TERCOD", 200
        ));

        mockMvc.perform(post("/api/con/add-contrato")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Faltan datos obligatorios."));
    }

    @Test
    void addContrato_returns400OnMissingEJE() throws Exception {
        String payload = objectMapper.writeValueAsString(Map.of(
            "ENT", 1,
            "CONLOT", "LOT001",
            "CONDES", "New Contract",
            "TERCOD", 200
        ));

        mockMvc.perform(post("/api/con/add-contrato")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Faltan datos obligatorios."));
    }

    @Test
    void addContrato_returns400OnMissingCONDES() throws Exception {
        String payload = objectMapper.writeValueAsString(Map.of(
            "ENT", 1,
            "EJE", "E1",
            "CONLOT", "LOT001",
            "TERCOD", 200
        ));

        mockMvc.perform(post("/api/con/add-contrato")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Faltan datos obligatorios."));
    }

    @Test
    void addContrato_returns400OnMissingCONLOT() throws Exception {
        String payload = objectMapper.writeValueAsString(Map.of(
            "ENT", 1,
            "EJE", "E1",
            "CONDES", "New Contract",
            "TERCOD", 200
        ));

        mockMvc.perform(post("/api/con/add-contrato")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Faltan datos obligatorios."));
    }

    @Test
    void addContrato_returns400OnMissingTERCOD() throws Exception {
        String payload = objectMapper.writeValueAsString(Map.of(
            "ENT", 1,
            "EJE", "E1",
            "CONLOT", "LOT001",
            "CONDES", "New Contract"
        ));

        mockMvc.perform(post("/api/con/add-contrato")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Faltan datos obligatorios."));
    }

    @Test
    void addContrato_returns500OnException() throws Exception {
        when(conRepository.findFirstByENTAndEJEOrderByCONCODDesc(1, "E1"))
            .thenThrow(new DataAccessResourceFailureException("Database error"));

        String payload = objectMapper.writeValueAsString(Map.of(
            "ENT", 1,
            "EJE", "E1",
            "CONLOT", "LOT001",
            "CONBLO", 0,
            "CONFIN", "2026-03-21T10:00:00",
            "CONFFI", "2026-03-21T10:00:00",
            "CONDES", "New Contract",
            "TERCOD", 200
        ));

        mockMvc.perform(post("/api/con/add-contrato")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andDo(print())
            .andExpect(status().isInternalServerError())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Error :")));
    }

    @Test
    void addContrato_nexConcodWithNullConcod() throws Exception {
        when(conRepository.findFirstByENTAndEJEOrderByCONCODDesc(1, "E1"))
            .thenReturn(Optional.of(new Conn() {{ setCONCOD(null); }}));

        String payload = objectMapper.writeValueAsString(Map.of(
            "ENT", 1,
            "EJE", "E1",
            "CONLOT", "LOT001",
            "CONBLO", 0,
            "CONFIN", "2026-03-21T10:00:00",
            "CONFFI", "2026-03-21T10:00:00",
            "CONDES", "New Contract",
            "TERCOD", 200
        ));

        mockMvc.perform(post("/api/con/add-contrato")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andDo(print())
            .andExpect(status().isNoContent());

        verify(conRepository).save(any(Conn.class));
        verify(cotRepository).save(any(Cot.class));
    }
}