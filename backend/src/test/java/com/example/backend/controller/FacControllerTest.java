package com.example.backend.controller;

import com.example.backend.config.TestSecurityConfig;
import com.example.backend.dto.FacWithTerProjection;
import com.example.backend.config.TestExceptionHandler;
import com.example.backend.service.FacturaInsertService;
import com.example.backend.sqlserver2.model.Fac;
import com.example.backend.sqlserver2.model.FacId;
import com.example.backend.sqlserver2.model.Fde;
import com.example.backend.sqlserver2.model.Gbs;
import com.example.backend.sqlserver2.model.GbsId;
import com.example.backend.sqlserver2.model.Ter;
import com.example.backend.sqlserver2.repository.FacRepository;
import com.example.backend.sqlserver2.repository.TerRepository;
import com.example.backend.sqlserver2.repository.FdeRepository;
import com.example.backend.sqlserver2.repository.GbsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = FacController.class)
@ActiveProfiles("test")
@Import({TestSecurityConfig.class, TestExceptionHandler.class})
public class FacControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FacRepository facRepository;

    @MockitoBean
    private TerRepository terRepository;

    @MockitoBean
    private FacturaInsertService facturaInsertService;

    @MockitoBean
    private FdeRepository fdeRepository;

    @MockitoBean
    private GbsRepository gbsRepository;

    static class FacWithTerProjectionImpl implements FacWithTerProjection {
        private Integer ent; private String eje; private Integer facnum; private Integer tercod;
        private String cgecod; private String facobs; private Double facimp; private Double faciec;
        private Double facidi; private String factdc; private Integer facann; private Integer facfac;
        private String facdoc; private LocalDateTime facdat; private LocalDateTime facfco; private String facado;
        private String factxt; private LocalDateTime facfre; private String conctp; private String concpr;
        private String conccr; private Integer facoct; private String facfpg; private String facopg;
        private String factpg; private Double facdto; private String terNom; private String terNif;

        public Integer getENT() { return ent; }
        public String getEJE() { return eje; }
        public Integer getFACNUM() { return facnum; }
        public Integer getTERCOD() { return tercod; }
        public String getCGECOD() { return cgecod; }
        public String getFACOBS() { return facobs; }
        public Double getFACIMP() { return facimp; }
        public Double getFACIEC() { return faciec; }
        public Double getFACIDI() { return facidi; }
        public String getFACTDC() { return factdc; }
        public Integer getFACANN() { return facann; }
        public Integer getFACFAC() { return facfac; }
        public String getFACDOC() { return facdoc; }
        public LocalDateTime getFACDAT() { return facdat; }
        public LocalDateTime getFACFCO() { return facfco; }
        public String getFACADO() { return facado; }
        public String getFACTXT() { return factxt; }
        public LocalDateTime getFACFRE() { return facfre; }
        public String getCONCTP() { return conctp; }
        public String getCONCPR() { return concpr; }
        public String getCONCCR() { return conccr; }
        public Integer getFACOCT() { return facoct; }
        public String getFACFPG() { return facfpg; }
        public String getFACOPG() { return facopg; }
        public String getFACTPG() { return factpg; }
        public Double getFACDTO() { return facdto; }
        public String getTer_TERNOM() { return terNom; }
        public String getTer_TERNIF() { return terNif; }
    }

    @Test
    void getFacturas_returns200WithDto() throws Exception {
        FacWithTerProjectionImpl projection = new FacWithTerProjectionImpl();
        projection.ent = 1;
        projection.eje = "E1";
        projection.facnum = 123;
        projection.tercod = 1;
        projection.cgecod = "C1";
        projection.facobs = "OBS1";
        projection.facimp = 100.5;
        projection.faciec = 0.0;
        projection.facidi = 0.0;
        projection.factdc = "TDC";
        projection.facann = 0;
        projection.facfac = 0;
        projection.facdoc = "DOC";
        projection.facdat = null;
        projection.facfco = null;
        projection.facado = "ADO";
        projection.factxt = "TXT";
        projection.facfre = null;
        projection.conctp = "CTP";
        projection.concpr = "CPR";
        projection.conccr = "CCR";
        projection.facoct = 0;
        projection.facfpg = "FPG";
        projection.facopg = "OPG";
        projection.factpg = "TPG";
        projection.facdto = 0.0;
        projection.terNom = "Cliente X";
        projection.terNif = "NIF123";

        when(facRepository.findByENTAndEJEAndCGECODOrderByFACFREAsc(1, "E1", "C1"))
            .thenReturn(List.of(projection));

        mockMvc.perform(get("/api/fac/1/E1/C1")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].facnum", is(123)))
            .andExpect(jsonPath("$[0].ter_TERNOM", is("Cliente X")));
    }

    @Test
    void getFacturas_returnsNotFoundWhenEmpty() throws Exception {
        when(facRepository.findByENTAndEJEAndCGECODOrderByFACFREAsc(1, "E1", "C1"))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/fac/1/E1/C1")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(content().string(containsString("Sin resultado")));
    }

    @Test
    void getFacturas_returnsBadRequestOnDataAccessException() throws Exception {
        when(facRepository.findByENTAndEJEAndCGECODOrderByFACFREAsc(anyInt(), anyString(), anyString()))
            .thenThrow(new DataAccessResourceFailureException("DB down"));

        mockMvc.perform(get("/api/fac/1/E1/C1")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Error :")));
    }

    @Test
    void searchFacturas_returns200WithResults() throws Exception {
        Fac f = new Fac();
        f.setENT(1);
        f.setEJE("E1");
        f.setFACNUM(321);
        f.setTERCOD(10);
        f.setCGECOD("C1");
        Ter t = new Ter();
        t.setTERNOM("Cliente Y");
        t.setTERNIF("NIF456");
        f.setTer(t);

        when(facRepository.findAll(any(Specification.class))).thenReturn(List.of(f));

        mockMvc.perform(get("/api/fac/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("cgecod", "C1")
                .param("estado", "TODAS")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void searchFacturas_returns200WhenEmpty() throws Exception {
        when(facRepository.findAll(any(Specification.class))).thenReturn(List.of());

        mockMvc.perform(get("/api/fac/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("cgecod", "C1")
                .param("estado", "TODAS")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void searchFacturas_returnsBadRequestOnException() throws Exception {
        when(facRepository.findAll(any(Specification.class)))
            .thenThrow(new RuntimeException("Something went wrong"));

        mockMvc.perform(get("/api/fac/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("cgecod", "C1")
                .param("estado", "TODAS")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Error :")));
    }

    @Test
    void addFacturas_returnsOkWithMessages() throws Exception {
        List<String> messages = List.of("Factura 1 insertada", "Factura 2 insertada");
        when(facturaInsertService.insertFacturas(any())).thenReturn(messages);

        List<Map<String, Object>> payload = List.of(new HashMap<>());

        mockMvc.perform(post("/api/fac/add-facturas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));

        verify(facturaInsertService).insertFacturas(any());
    }

    @Test
    void addFacturas_returnsBadRequestOnException() throws Exception {
        when(facturaInsertService.insertFacturas(any()))
            .thenThrow(new RuntimeException("Insert failed"));

        mockMvc.perform(post("/api/fac/add-facturas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of())))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Error :")));
    }

    @Test
    void updateFactura_returnsNoContentOnSuccess() throws Exception {
        FacId id = new FacId(1, "E1", 100);
        Fac factura = new Fac();
        when(facRepository.findById(id)).thenReturn(Optional.of(factura));

        Map<String, Object> payload = Map.of(
            "FACOBS", "Observacion",
            "CONCTP", "TP",
            "CONCPR", "PR",
            "CONCCR", "CR",
            "FACFRE", "2026-01-22T12:00:00",
            "FACFPG", "FPG",
            "FACOPG", "OPG",
            "FACTPG", "TPG",
            "FACOCT", 5
        );

        mockMvc.perform(patch("/api/fac/update-factura/1/E1/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andDo(print())
            .andExpect(status().isNoContent());

        verify(facRepository).save(any(Fac.class));
    }

    @Test
    void updateFactura_returnsNotFoundWhenMissing() throws Exception {
        FacId id = new FacId(9, "X", 999);
        when(facRepository.findById(id)).thenReturn(Optional.empty());

        Map<String, Object> payload = Map.of(
            "FACOBS", "Obs",
            "CONCTP", "TP",
            "CONCPR", "PR",
            "CONCCR", "CR",
            "FACFRE", "2026-01-22T12:00:00",
            "FACFPG", "FPG",
            "FACOPG", "OPG",
            "FACTPG", "TPG",
            "FACOCT", 0
        );

        mockMvc.perform(patch("/api/fac/update-factura/9/X/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(content().string("Sin resultado"));
    }

    @Test
    void updateFactura_returnsNoContentWithMissingFields() throws Exception {
        FacId id = new FacId(1, "E1", 100);
        Fac factura = new Fac();
        when(facRepository.findById(id)).thenReturn(Optional.of(factura));

        Map<String, Object> payload = Map.of(
            "FACOBS", "Obs",
            "CONCTP", "TP"
        );

        mockMvc.perform(patch("/api/fac/update-factura/1/E1/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andDo(print())
            .andExpect(status().isNoContent());

        verify(facRepository).save(any(Fac.class));
    }

    @Test
    void updateFactura_returnsBadRequestOnException() throws Exception {
        FacId id = new FacId(1, "E1", 100);
        when(facRepository.findById(id)).thenThrow(new DataAccessResourceFailureException("DB error"));

        Map<String, Object> payload = Map.of(
            "FACOBS", "Obs",
            "CONCTP", "TP",
            "CONCPR", "PR",
            "CONCCR", "CR",
            "FACFRE", "2026-01-22T12:00:00",
            "FACFPG", "FPG",
            "FACOPG", "OPG",
            "FACTPG", "TPG",
            "FACOCT", 0
        );

        mockMvc.perform(patch("/api/fac/update-factura/1/E1/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Error :")));
    }

    @Test
    void searchContabilizacion_returnsOkWithResults() throws Exception {
        Fac f = new Fac();
        f.setENT(1);
        f.setEJE("E1");
        f.setFACNUM(200);
        when(facRepository.findAll(any(Specification.class))).thenReturn(List.of(f));

        mockMvc.perform(get("/api/fac/contabilizacion/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("cgecod", "C1")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void searchContabilizacion_returnsNotFoundWhenEmpty() throws Exception {
        when(facRepository.findAll(any(Specification.class))).thenReturn(List.of());

        mockMvc.perform(get("/api/fac/contabilizacion/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("cgecod", "C1")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(content().string("Sin resultado"));
    }

    @Test
    void searchContabilizacion_returnsBadRequestOnException() throws Exception {
        when(facRepository.findAll(any(Specification.class)))
            .thenThrow(new DataAccessResourceFailureException("DB error"));

        mockMvc.perform(get("/api/fac/contabilizacion/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("cgecod", "C1")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Error :")));
    }

    @Test
    void contabilizarFactura_returnsBadRequestWhenPayloadNull() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("ENT", 1);
        payload.put("EJE", null);
        payload.put("FACNUM", 100);
        payload.put("FACADO", "ADO");
        payload.put("FACFCO", "2026-01-22T12:00:00");
        payload.put("CGECOD", "C1");
        payload.put("ESCONTRATO", false);

        mockMvc.perform(patch("/api/fac/contabilizar-facturas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Falta un dato obligatorio")));
    }

    @Test
    void contabilizarFactura_returnsNotFoundWhenFacturaMissing() throws Exception {
        when(facRepository.findById(any(FacId.class))).thenReturn(Optional.empty());

        Map<String, Object> payload = Map.of(
            "ENT", 1,
            "EJE", "E1",
            "FACNUM", 100,
            "FACADO", "ADO",
            "FACFCO", "2026-01-22T12:00:00",
            "CGECOD", "C1",
            "ESCONTRATO", false
        );

        mockMvc.perform(patch("/api/fac/contabilizar-facturas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(content().string(containsString("Factura no encontrada")));
    }

    @Test
    void contabilizarFactura_returnsBadRequestWhenBolsaMissing() throws Exception {
        FacId facId = new FacId(1, "E1", 100);
        Fac factura = new Fac();
        when(facRepository.findById(facId)).thenReturn(Optional.of(factura));

        Fde fde = new Fde();
        fde.setFDEORG("ORG");
        fde.setFDEFUN("FUN");
        fde.setFDEECO("ECO");
        when(fdeRepository.findByENTAndEJEAndFACNUM(1, "E1", 100)).thenReturn(List.of(fde));
        when(gbsRepository.findByENTAndEJEAndCGECODAndGBSORGAndGBSFUNAndGBSECO(1, "E1", "C1", "ORG", "FUN", "ECO"))
            .thenReturn(Optional.empty());

        Map<String, Object> payload = Map.of(
            "ENT", 1,
            "EJE", "E1",
            "FACNUM", 100,
            "FACADO", "ADO",
            "FACFCO", "2026-01-22T12:00:00",
            "CGECOD", "C1",
            "ESCONTRATO", false
        );

        mockMvc.perform(patch("/api/fac/contabilizar-facturas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("No existe bolsa para")));
    }

    @Test
    void contabilizarFactura_returnsNoContentOnSuccessWithContract() throws Exception {
        FacId facId = new FacId(1, "E1", 100);
        Fac factura = new Fac();
        when(facRepository.findById(facId)).thenReturn(Optional.of(factura));

        Map<String, Object> payload = Map.of(
            "ENT", 1,
            "EJE", "E1",
            "FACNUM", 100,
            "FACADO", "ADO",
            "FACFCO", "2026-01-22T12:00:00",
            "CGECOD", "C1",
            "ESCONTRATO", true
        );

        mockMvc.perform(patch("/api/fac/contabilizar-facturas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andDo(print())
            .andExpect(status().isNoContent());

        verify(facRepository).save(any(Fac.class));
        verify(fdeRepository, never()).findByENTAndEJEAndFACNUM(anyInt(), anyString(), anyInt());
    }

    @Test
    void contabilizarFactura_returnsNoContentOnSuccessWithBolsas() throws Exception {
        FacId facId = new FacId(1, "E1", 100);
        Fac factura = new Fac();
        when(facRepository.findById(facId)).thenReturn(Optional.of(factura));

        Fde fde = new Fde();
        fde.setFDEORG("ORG");
        fde.setFDEFUN("FUN");
        fde.setFDEECO("ECO");
        fde.setFDEIMP(50.0);
        fde.setFDEDIF(10.0);
        when(fdeRepository.findByENTAndEJEAndFACNUM(1, "E1", 100)).thenReturn(List.of(fde));

        GbsId gbsId = new GbsId(1, "E1", "C1", "REF");
        Gbs bolsa = new Gbs();
        bolsa.setGBSIUS(100.0);
        bolsa.setGBSIUT(100.0);
        when(gbsRepository.findByENTAndEJEAndCGECODAndGBSORGAndGBSFUNAndGBSECO(1, "E1", "C1", "ORG", "FUN", "ECO"))
            .thenReturn(Optional.of(bolsa));

        Map<String, Object> payload = Map.of(
            "ENT", 1,
            "EJE", "E1",
            "FACNUM", 100,
            "FACADO", "ADO",
            "FACFCO", "2026-01-22T12:00:00",
            "CGECOD", "C1",
            "ESCONTRATO", false
        );

        mockMvc.perform(patch("/api/fac/contabilizar-facturas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andDo(print())
            .andExpect(status().isNoContent());

        verify(facRepository).save(any(Fac.class));
        verify(gbsRepository).save(any(Gbs.class));
    }

    @Test
    void contabilizarFactura_returnsBadRequestOnException() throws Exception {
        when(facRepository.findById(any(FacId.class)))
            .thenThrow(new DataAccessResourceFailureException("DB error"));

        Map<String, Object> payload = Map.of(
            "ENT", 1,
            "EJE", "E1",
            "FACNUM", 100,
            "FACADO", "ADO",
            "FACFCO", "2026-01-22T12:00:00",
            "CGECOD", "C1",
            "ESCONTRATO", false
        );

        mockMvc.perform(patch("/api/fac/contabilizar-facturas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Error :")));
    }
}