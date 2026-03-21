package com.example.backend.controller;

import com.example.backend.config.TestSecurityConfig;
import com.example.backend.config.TestExceptionHandler;
import com.example.backend.dto.DepWithCgeView;
import com.example.backend.sqlserver2.model.Cco;
import com.example.backend.sqlserver2.model.CcoId;
import com.example.backend.sqlserver2.model.Dep;
import com.example.backend.sqlserver2.model.DepId;
import com.example.backend.sqlserver2.repository.CcoRepository;
import com.example.backend.sqlserver2.repository.DepRepository;
import com.example.backend.sqlserver2.repository.DpeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DepController.class)
@ActiveProfiles("test")
@Import({TestSecurityConfig.class, TestExceptionHandler.class})
public class DepControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DepRepository depRepository;

    @MockitoBean
    private DpeRepository dpeRepository;

    @MockitoBean
    private CcoRepository ccoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void fetchServices_returns200WithList() throws Exception {
        Dep d = new Dep(); d.setDEPCOD("D1"); d.setDEPDES("S1");
        when(depRepository.findByENTAndEJE(1, "E1")).thenReturn(List.of(d));

        mockMvc.perform(get("/api/dep/fetch-services/1/E1")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].depcod", is("D1")));
    }

    @Test
    void fetchServices_returns404WhenEmpty() throws Exception {
        when(depRepository.findByENTAndEJE(1, "E1")).thenReturn(List.of());

        mockMvc.perform(get("/api/dep/fetch-services/1/E1"))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(content().string("Sin resultado"));
    }

    @Test
    void fetchServices_returns500OnDataAccessException() throws Exception {
        when(depRepository.findByENTAndEJE(anyInt(), anyString()))
            .thenThrow(new DataAccessResourceFailureException("DB down"));

        mockMvc.perform(get("/api/dep/fetch-services/1/E1"))
            .andDo(print())
            .andExpect(status().isInternalServerError())
            .andExpect(content().string(containsString("Error :")));
    }

    @Test
    void fetchServicesPersona_returns404WhenEmpty() throws Exception {
        when(depRepository.findByENTAndEJEAndDpes_PERCOD(1, "E1", "U1")).thenReturn(List.of());

        mockMvc.perform(get("/api/dep/fetch-services-persona/1/E1/U1"))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(content().string("Sin resultado"));
    }

    @Test
    void fetchServicesPersona_returns500OnException() throws Exception {
        when(depRepository.findByENTAndEJEAndDpes_PERCOD(anyInt(), anyString(), anyString()))
            .thenThrow(new DataAccessResourceFailureException("DB down"));

        mockMvc.perform(get("/api/dep/fetch-services-persona/1/E1/U1"))
            .andDo(print())
            .andExpect(status().isInternalServerError())
            .andExpect(content().string(containsString("Error :")));
    }

    @Test
    void updateCentro_returns204OnSuccess() throws Exception {
        Dep existing = new Dep();
        existing.setDEPDES("old");
        when(depRepository.findById(new DepId(1, "E1", "D1"))).thenReturn(Optional.of(existing));

        Map<String,Object> payload = Map.of("depdes", "new", "depalm", 1, "depcom", 0, "depint", 0);

        mockMvc.perform(patch("/api/dep/update-service/1/E1/D1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andDo(print())
            .andExpect(status().isNoContent());

        ArgumentCaptor<Dep> cap = ArgumentCaptor.forClass(Dep.class);
        verify(depRepository).save(cap.capture());
        assertEquals("new", cap.getValue().getDEPDES());
    }

    @Test
    void updateCentro_returns400OnMissingFields() throws Exception {
        Map<String,Object> payload = Map.of("depdes", "x", "depalm", 1); 

        mockMvc.perform(patch("/api/dep/update-service/1/E1/D1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Faltan datos obligatorios")));
    }

    @Test
    void updateCentro_returns404WhenNotFound() throws Exception {
        when(depRepository.findById(new DepId(1, "E1", "NX"))).thenReturn(Optional.empty());

        Map<String,Object> payload = Map.of("depdes", "n", "depalm", 1, "depcom", 1, "depint", 0);

        mockMvc.perform(patch("/api/dep/update-service/1/E1/NX")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(content().string("Sin resultado"));
    }

    @Test
    void updateCentro_returns400OnDataAccessException() throws Exception {
        Dep existing = new Dep();
        when(depRepository.findById(new DepId(1, "E1", "D1"))).thenReturn(Optional.of(existing));
        when(depRepository.save(Mockito.<Dep>any()))
            .thenThrow(new DataAccessResourceFailureException("DB error"));

        Map<String,Object> payload = Map.of("depdes", "n", "depalm", 1, "depcom", 1, "depint", 0);

        mockMvc.perform(patch("/api/dep/update-service/1/E1/D1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Error :")));
    }

    @Test
    void updateServiceSecond_returns204OnSuccess() throws Exception {
        Dep existing = new Dep();
        when(depRepository.findById(new DepId(1, "E1", "D2"))).thenReturn(Optional.of(existing));

        Map<String,Object> payload = Map.of(
            "depd1c", "a","depd1d","b",
            "depd2c","c","depd2d","d",
            "depd3c","e","depd3d","f",
            "depdco","g","depden","h"
        );

        mockMvc.perform(patch("/api/dep/update-service-second/1/E1/D2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andDo(print())
            .andExpect(status().isNoContent());

        verify(depRepository).save(Mockito.<Dep>any());
    }

    @Test
    void updateServiceSecond_returns400OnMissingFields() throws Exception {
        Map<String,Object> payload = Map.of("depd1c", "a", "depd1d", "b"); 

        mockMvc.perform(patch("/api/dep/update-service-second/1/E1/D2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Faltan datos obligatorios")));
    }

    @Test
    void updateServiceSecond_returns404WhenNotFound() throws Exception {
        when(depRepository.findById(new DepId(1, "E1", "NX"))).thenReturn(Optional.empty());

        Map<String,Object> payload = Map.of(
            "depd1c", "a","depd1d","b",
            "depd2c","c","depd2d","d",
            "depd3c","e","depd3d","f",
            "depdco","g","depden","h"
        );

        mockMvc.perform(patch("/api/dep/update-service-second/1/E1/NX")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(content().string("Sin resultado"));
    }

    @Test
    void updateServiceSecond_returns400OnException() throws Exception {
        Dep existing = new Dep();
        when(depRepository.findById(new DepId(1, "E1", "D2"))).thenReturn(Optional.of(existing));
        when(depRepository.save(Mockito.<Dep>any()))
            .thenThrow(new DataAccessResourceFailureException("DB error"));

        Map<String,Object> payload = Map.of(
            "depd1c", "a","depd1d","b",
            "depd2c","c","depd2d","d",
            "depd3c","e","depd3d","f",
            "depdco","g","depden","h"
        );

        mockMvc.perform(patch("/api/dep/update-service-second/1/E1/D2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Error :")));
    }

    @Test
    void addCentroGestor_returns201OnSuccess() throws Exception {
        Map<String,Object> payload = Map.of(
            "ent", 1, "eje", "E1", "depcod", "D3", "depdes", "Desc",
            "depalm", 0, "depcom", 0, "depint", 0,
            "ccocod", "C1", "cgecod", "G1", "percod", "U1"
        );
        when(depRepository.findByENTAndEJEAndDEPCOD(1, "E1", "D3")).thenReturn(List.of());
        when(ccoRepository.countByENTAndEJEAndCCOCOD(1, "E1", "C1")).thenReturn(1L);

        mockMvc.perform(post("/api/dep/Insert-service")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andDo(print())
            .andExpect(status().isCreated());

        verify(depRepository).save(Mockito.<Dep>any());
    }

    @Test
    void addCentroGestor_returnsConflictWhenExists() throws Exception {
        Map<String,Object> payload = Map.of(
            "ent", 1, "eje", "E1", "depcod", "D3", "depdes", "Desc",
            "depalm", 0, "depcom", 0, "depint", 0,
            "ccocod", "C1", "cgecod", "G1", "percod", "U1"
        );
        when(depRepository.findByENTAndEJEAndDEPCOD(1, "E1", "D3")).thenReturn(List.of(new Dep()));

        mockMvc.perform(post("/api/dep/Insert-service")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andDo(print())
            .andExpect(status().isConflict())
            .andExpect(content().string(containsString("Este servicio ya existe")));
    }

    @Test
    void addCentroGestor_returnsConflictWhenCcoMissing() throws Exception {
        Map<String,Object> payload = Map.of(
            "ent", 1, "eje", "E1", "depcod", "D3", "depdes", "Desc",
            "depalm", 0, "depcom", 0, "depint", 0,
            "ccocod", "C1", "cgecod", "G1", "percod", "U1"
        );
        when(depRepository.findByENTAndEJEAndDEPCOD(1, "E1", "D3")).thenReturn(List.of());
        when(ccoRepository.countByENTAndEJEAndCCOCOD(1, "E1", "C1")).thenReturn(0L);

        mockMvc.perform(post("/api/dep/Insert-service")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andDo(print())
            .andExpect(status().isConflict())
            .andExpect(content().string(containsString("El Centro de Coste no existe")));
    }

    @Test
    void addCentroGestor_returns400OnMissingFields() throws Exception {
        Map<String,Object> payload = Map.of(
            "ent", 1, "eje", "E1", "depcod", "D3"
        );

        mockMvc.perform(post("/api/dep/Insert-service")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Faltan datos obligatorios")));
    }

    @Test
    void addCentroGestor_returns400OnDataAccessException() throws Exception {
        Map<String,Object> payload = Map.of(
            "ent", 1, "eje", "E1", "depcod", "D3", "depdes", "Desc",
            "depalm", 0, "depcom", 0, "depint", 0,
            "ccocod", "C1", "cgecod", "G1", "percod", "U1"
        );
        when(depRepository.findByENTAndEJEAndDEPCOD(1, "E1", "D3")).thenReturn(List.of());
        when(ccoRepository.countByENTAndEJEAndCCOCOD(1, "E1", "C1")).thenReturn(1L);
        when(depRepository.save(Mockito.<Dep>any()))
            .thenThrow(new DataAccessResourceFailureException("DB error"));

        mockMvc.perform(post("/api/dep/Insert-service")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Error :")));
    }

    @Test
    void search_returnsResultsAndFilters() throws Exception {
        Dep d = new Dep(); d.setDEPCOD("S1"); d.setDEPDES("Servicio");
        when(depRepository.findByENTAndEJE(1, "E1")).thenReturn(List.of(d));

        mockMvc.perform(get("/api/dep/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("search", "serv")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void search_returns404WhenEmpty() throws Exception {
        when(depRepository.findByENTAndEJE(1, "E1")).thenReturn(List.of());

        mockMvc.perform(get("/api/dep/search")
                .param("ent", "1")
                .param("eje", "E1"))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(content().string("Sin resultado"));
    }

    @Test
    void search_returns500OnDataAccessException() throws Exception {
        when(depRepository.findByENTAndEJE(anyInt(), anyString()))
            .thenThrow(new DataAccessResourceFailureException("boom"));

        mockMvc.perform(get("/api/dep/search")
                .param("ent", "1")
                .param("eje", "E1"))
            .andDo(print())
            .andExpect(status().isInternalServerError());
    }

    @Test
    void search_filtersByCgecod() throws Exception {
        Dep d1 = new Dep(); d1.setDEPCOD("D1"); d1.setDEPDES("Service1"); d1.setCGECOD("G1");
        Dep d2 = new Dep(); d2.setDEPCOD("D2"); d2.setDEPDES("Service2"); d2.setCGECOD("G2");
        when(depRepository.findByENTAndEJE(1, "E1")).thenReturn(List.of(d1, d2));

        mockMvc.perform(get("/api/dep/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("cgecod", "G1")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].depcod", is("D1")));
    }

    @Test
    void search_filtersByAlmacenPerfil() throws Exception {
        Dep d1 = new Dep(); d1.setDEPCOD("D1"); d1.setDEPALM(1); d1.setDEPCOM(0); d1.setDEPINT(0);
        Dep d2 = new Dep(); d2.setDEPCOD("D2"); d2.setDEPALM(0); d2.setDEPCOM(1); d2.setDEPINT(0);
        when(depRepository.findByENTAndEJE(1, "E1")).thenReturn(List.of(d1, d2));

        mockMvc.perform(get("/api/dep/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("perfil", "almacen")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].depcod", is("D1")));
    }

    @Test
    void search_filtersByCompradorPerfil() throws Exception {
        Dep d1 = new Dep(); d1.setDEPCOD("D1"); d1.setDEPALM(1); d1.setDEPCOM(0); d1.setDEPINT(0);
        Dep d2 = new Dep(); d2.setDEPCOD("D2"); d2.setDEPALM(0); d2.setDEPCOM(1); d2.setDEPINT(0);
        when(depRepository.findByENTAndEJE(1, "E1")).thenReturn(List.of(d1, d2));

        mockMvc.perform(get("/api/dep/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("perfil", "comprador")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].depcod", is("D2")));
    }

    @Test
    void search_filtersByContabilidadPerfil() throws Exception {
        Dep d1 = new Dep(); d1.setDEPCOD("D1"); d1.setDEPALM(0); d1.setDEPCOM(0); d1.setDEPINT(1);
        Dep d2 = new Dep(); d2.setDEPCOD("D2"); d2.setDEPALM(1); d2.setDEPCOM(0); d2.setDEPINT(0);
        when(depRepository.findByENTAndEJE(1, "E1")).thenReturn(List.of(d1, d2));

        mockMvc.perform(get("/api/dep/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("perfil", "contabilidad")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].depcod", is("D1")));
    }

    @Test
    void search_filtersByPeticionarioPerfil() throws Exception {
        Dep d1 = new Dep(); d1.setDEPCOD("D1"); d1.setDEPALM(0); d1.setDEPCOM(0); d1.setDEPINT(0);
        Dep d2 = new Dep(); d2.setDEPCOD("D2"); d2.setDEPALM(1); d2.setDEPCOM(0); d2.setDEPINT(0);
        when(depRepository.findByENTAndEJE(1, "E1")).thenReturn(List.of(d1, d2));

        mockMvc.perform(get("/api/dep/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("perfil", "peticionario")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].depcod", is("D1")));
    }

    @Test
    void search_ignoresPerfilWhenTodos() throws Exception {
        Dep d1 = new Dep(); d1.setDEPCOD("D1"); d1.setDEPALM(1); d1.setDEPCOM(0); d1.setDEPINT(0);
        Dep d2 = new Dep(); d2.setDEPCOD("D2"); d2.setDEPALM(0); d2.setDEPCOM(1); d2.setDEPINT(0);
        when(depRepository.findByENTAndEJE(1, "E1")).thenReturn(List.of(d1, d2));

        mockMvc.perform(get("/api/dep/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("perfil", "todos")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void search_combinesSearchAndPerfil() throws Exception {
        Dep d = new Dep();
        d.setDEPCOD("D1");
        d.setDEPDES("Almacen Storage");
        d.setDEPALM(1);

        when(depRepository.findByENTAndEJE(1, "E1"))
            .thenReturn(List.of(d));

        mockMvc.perform(get("/api/dep/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("search", "Almacen")
                .param("perfil", "almacen")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].depdes", is("Almacen Storage")));
    }

    @Test
    void search_withSearchAndCgecod() throws Exception {
        Dep d = new Dep();
        d.setDEPCOD("D1");
        d.setDEPDES("Test Service");
        d.setCGECOD("G1");

        when(depRepository.findByENTAndEJE(1, "E1"))
            .thenReturn(List.of(d));

        mockMvc.perform(get("/api/dep/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("search", "Test")
                .param("cgecod", "G1"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void search_specialCharactersInSearch() throws Exception {
        Dep d = new Dep();
        d.setDEPCOD("D1");
        d.setDEPDES("Test & Special/Department");

        when(depRepository.findByENTAndEJE(1, "E1"))
            .thenReturn(List.of(d));

        mockMvc.perform(get("/api/dep/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("search", "Test & Special"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void search_emptySearchParamReturnsFull() throws Exception {
        Dep d = new Dep();
        d.setDEPCOD("D1");
        d.setENT(1);
        d.setEJE("E1");

        when(depRepository.findByENTAndEJE(1, "E1"))
            .thenReturn(List.of(d));

        mockMvc.perform(get("/api/dep/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("search", ""))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));

        verify(depRepository).findByENTAndEJE(1, "E1");
    }

    @Test
    void search_nullSearchParamReturnsFull() throws Exception {
        Dep d = new Dep();
        d.setDEPCOD("D1");
        d.setDEPDES("Service1");

        when(depRepository.findByENTAndEJE(1, "E1"))
            .thenReturn(List.of(d));

        mockMvc.perform(get("/api/dep/search?ent=1&eje=E1"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void updateCentro_maintainsReadOnlyFields() throws Exception {
        Dep existing = new Dep();
        existing.setDEPDES("original");
        existing.setENT(1);

        when(depRepository.findById(new DepId(1, "E1", "D1")))
            .thenReturn(Optional.of(existing));

        Map<String,Object> payload = Map.of("depdes", "new", "depalm", 1, "depcom", 1, "depint", 0);

        mockMvc.perform(patch("/api/dep/update-service/1/E1/D1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andDo(print())
            .andExpect(status().isNoContent());

        ArgumentCaptor<Dep> cap = ArgumentCaptor.forClass(Dep.class);
        verify(depRepository).save(cap.capture());
        assertEquals(1, cap.getValue().getENT()); 
    }

    @Test
    void addCentroGestor_withValidCcoReference() throws Exception {
        when(depRepository.findByENTAndEJEAndDEPCOD(1, "E1", "D2")).thenReturn(List.of());
        when(ccoRepository.countByENTAndEJEAndCCOCOD(1, "E1", "C1")).thenReturn(1L);
        when(depRepository.save(Mockito.<Dep>any()))
            .thenAnswer(inv -> inv.getArgument(0));

        Map<String,Object> payload = Map.of("ent", 1, "eje", "E1", "depcod", "D2", "depdes", "New", 
                                           "depalm", 0, "depcom", 0, "depint", 0, "ccocod", "C1", 
                                           "cgecod", "G1", "percod", "U1");

        mockMvc.perform(post("/api/dep/Insert-service")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andDo(print())
            .andExpect(status().isCreated());

        verify(depRepository).save(Mockito.<Dep>any());
    }
}