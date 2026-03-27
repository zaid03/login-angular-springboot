package com.example.backend.controller;

import com.example.backend.config.TestSecurityConfig;
import com.example.backend.config.TestExceptionHandler;
import com.example.backend.dto.PersonaServiceRequest;
import com.example.backend.dto.ServicePersonaRequest;
import com.example.backend.sqlserver2.model.Dep;
import com.example.backend.sqlserver2.model.Dpe;
import com.example.backend.sqlserver2.model.Per;
import com.example.backend.sqlserver2.repository.DepRepository;
import com.example.backend.sqlserver2.repository.DpeRepository;
import com.example.backend.sqlserver2.repository.PerRepository;
import com.example.backend.service.DpePersonasForService;
import com.example.backend.service.DpeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.mockito.ArgumentCaptor;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DpeController.class)
@ActiveProfiles("test")
@Import({TestSecurityConfig.class, TestExceptionHandler.class})
public class DpeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DpeRepository dpeRepository;

    @MockitoBean
    private PerRepository perRepository;

    @MockitoBean
    private DepRepository depRepository;

    @MockitoBean
    private DpeService dpeService;

    @MockitoBean
    private DpePersonasForService dpePersonasForService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void fetchServicePersonas_returnsList() throws Exception {
        Dpe d1 = new Dpe(); d1.setPERCOD("U1");
        Dpe d2 = new Dpe(); d2.setPERCOD("U2");
        when(dpeRepository.findByENTAndEJEAndDEPCOD(1, "E1", "D1")).thenReturn(List.of(d1, d2));

        Per p1 = new Per(); p1.setPERCOD("U1"); p1.setPERNOM("User One");
        Per p2 = new Per(); p2.setPERCOD("U2"); p2.setPERNOM("User Two");
        when(perRepository.findByPERCODIn(List.of("U1", "U2"))).thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/api/depe/fetch-service-personas/1/E1/D1")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].percod", anyOf(is("U1"), is("U2"))));
    }

    @Test
    void fetchServicePersonas_returns500OnDataAccessException() throws Exception {
        when(dpeRepository.findByENTAndEJEAndDEPCOD(anyInt(), anyString(), anyString()))
            .thenThrow(new DataAccessResourceFailureException("DB down"));

        mockMvc.perform(get("/api/depe/fetch-service-personas/1/E1/D1"))
            .andDo(print())
            .andExpect(status().isInternalServerError())
            .andExpect(content().string(containsString("Error :")));
    }

    @Test
    void deletePersonaService_returnsNoContentWhenExists() throws Exception {
        when(dpeRepository.existsById(any())).thenReturn(true);

        mockMvc.perform(delete("/api/depe/delete-persona-service/1/E1/D1/U1"))
            .andDo(print())
            .andExpect(status().isNoContent());

        verify(dpeRepository).deleteById(any());
    }

    @Test
    void deletePersonaService_returnsNotFoundWhenMissing() throws Exception {
        when(dpeRepository.existsById(any())).thenReturn(false);

        mockMvc.perform(delete("/api/depe/delete-persona-service/1/E1/D1/U1"))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(content().string("Sin resultado"));
    }

    @Test
    void fetchPersonaService_returnsDeps() throws Exception {
        Dpe d1 = new Dpe(); d1.setDEPCOD("D1");
        when(dpeRepository.findByENTAndEJEAndPERCOD(1, "E1", "U1")).thenReturn(List.of(d1));

        Dep dep = new Dep(); dep.setDEPCOD("D1"); dep.setDEPDES("Service Desc");
        when(depRepository.findByENTAndEJEAndDEPCODIn(1, "E1", List.of("D1"))).thenReturn(List.of(dep));

        mockMvc.perform(get("/api/depe/fetch-persona-service/1/E1/U1")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].depcod").value("D1"))
            .andExpect(jsonPath("$[0].depdes").value("Service Desc"));
    }

    @Test
    void fetchPersonaService_returnsNotFoundWhenEmpty() throws Exception {
        when(dpeRepository.findByENTAndEJEAndPERCOD(1, "E1", "U2")).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/fetch-persona-service/1/E1/U2"))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(content().string("Sin resultado"));
    }

    @Test
     void addPersonaServices_validRequest_returnsCreated() throws Exception {
         PersonaServiceRequest req = new PersonaServiceRequest();
         req.setPercod("U1");
         req.setServices(List.of("S1", "S2"));
 
         mockMvc.perform(post("/api/depe/add-persona-services")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(req)))
             .andDo(print())
             .andExpect(status().isCreated());
 
        ArgumentCaptor<PersonaServiceRequest> cap = ArgumentCaptor.forClass(PersonaServiceRequest.class);
        verify(dpeService).savePersonaServices(cap.capture());
        PersonaServiceRequest passed = cap.getValue();
        assertEquals(req.getPercod(), passed.getPercod());
        assertEquals(req.getServices(), passed.getServices());
    }

    @Test
    void addPersonaServices_validationFails() throws Exception {
        PersonaServiceRequest missingUser = new PersonaServiceRequest();
        missingUser.setServices(List.of("S1"));
        mockMvc.perform(post("/api/depe/add-persona-services")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(missingUser)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Falta un dato obligatorio")));

        PersonaServiceRequest missingServices = new PersonaServiceRequest();
        missingServices.setPercod("U1");
        mockMvc.perform(post("/api/depe/add-persona-services")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(missingServices)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Debe seleccionar al menos un servicio.")));
    }

    @Test
    void deleteAllPersonaServices_returnsNoContentWhenDeleted() throws Exception {
        when(dpeRepository.findByENTAndEJEAndPERCOD(1, "E1", "U1")).thenReturn(List.of(new Dpe()));
        when(dpeRepository.deleteByENTAndEJEAndPERCOD(1, "E1", "U1")).thenReturn(1);

        mockMvc.perform(delete("/api/depe/delete-persona-Allservice/1/E1/U1"))
            .andDo(print())
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteAllPersonaServices_returnsNotFoundWhenMissing() throws Exception {
        when(dpeRepository.findByENTAndEJEAndPERCOD(1, "E1", "U1")).thenReturn(List.of());

        mockMvc.perform(delete("/api/depe/delete-persona-Allservice/1/E1/U1"))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(content().string("Sin resultado"));
    }

    @Test
    void addServicesPersona_validationAndSuccess() throws Exception {
        ServicePersonaRequest req = new ServicePersonaRequest();
        req.setDepcod("S1");
        req.setPersonas(List.of("U1", "U2"));

        mockMvc.perform(post("/api/depe/add-services-persona")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andDo(print())
            .andExpect(status().isCreated());

       ArgumentCaptor<ServicePersonaRequest> cap = ArgumentCaptor.forClass(ServicePersonaRequest.class);
       verify(dpePersonasForService).saveServicePersonas(cap.capture());
       ServicePersonaRequest passed = cap.getValue();
       assertEquals(req.getDepcod(), passed.getDepcod());
       assertEquals(req.getPersonas(), passed.getPersonas());
    }

    @Test
    void addServicesPersona_validationFails() throws Exception {
        ServicePersonaRequest missingDep = new ServicePersonaRequest();
        missingDep.setPersonas(List.of("U1"));
        mockMvc.perform(post("/api/depe/add-services-persona")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(missingDep)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("El código de servicio es obligatorio.")));

        ServicePersonaRequest missingPersons = new ServicePersonaRequest();
        missingPersons.setDepcod("S1");
        mockMvc.perform(post("/api/depe/add-services-persona")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(missingPersons)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Debe seleccionar al menos un persona.")));
    }

    @Test
    void deleteService_returnsNoContentWhenExists() throws Exception {
        when(dpeRepository.existsById(any())).thenReturn(true);

        mockMvc.perform(delete("/api/depe/delete-service-persona/1/E1/D1/U1"))
            .andDo(print())
            .andExpect(status().isNoContent());

        verify(dpeRepository).deleteById(any());
    }

    @Test
    void deleteService_returnsNotFoundWhenMissing() throws Exception {
        when(dpeRepository.existsById(any())).thenReturn(false);

        mockMvc.perform(delete("/api/depe/delete-service-persona/1/E1/D1/U1"))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(content().string("Sin resultado"));
    }

    @Test
    void fetchPersonasServicios_returnsPagedResults() throws Exception {
        when(dpeRepository.findByENTAndEJE(anyInt(), anyString(), any())).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/1/E1/0")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound());

        verify(dpeRepository).findByENTAndEJE(anyInt(), anyString(), any());
    }

    @Test
    void fetchPersonasServicios_withNullResultsReturnsNotFound() throws Exception {
        when(dpeRepository.findByENTAndEJE(anyInt(), anyString(), any())).thenReturn(null);

        mockMvc.perform(get("/api/depe/personas-servicios/1/E1/0")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(content().string("Sin resultado"));
    }

    @Test
    void searchPersonasServicios_returnsNotFoundWhenEmpty() throws Exception {
        when(dpeRepository.findByENTAndEJE(anyInt(), anyString())).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(content().string("Sin resultado"));
    }

    @Test
    void endpoints_return500OnDataAccessException() throws Exception {
        when(dpeRepository.findByENTAndEJEAndDEPCOD(anyInt(), anyString(), anyString()))
            .thenThrow(new DataAccessResourceFailureException("boom"));

        mockMvc.perform(get("/api/depe/fetch-service-personas/1/E1/D1"))
            .andDo(print())
            .andExpect(status().isInternalServerError());
    }

    @Test
    void fetchServicePersonas_returnsEmptyList() throws Exception {
        when(dpeRepository.findByENTAndEJEAndDEPCOD(1, "E1", "D1")).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/fetch-service-personas/1/E1/D1")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(content().string("Sin resultado"));
    }

    @Test
    void searchPersonasServicios_byPersonaCod() throws Exception {
        when(dpeRepository.findProjectionByENTAndEJEAndPERCOD(1, "E1", "U1")).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("persona", "U1")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_byPersonaName() throws Exception {
        when(dpeRepository.findByENTAndEJEAndPer_PERNOMContaining(1, "E1", "John Doe")).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("persona", "John Doe")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_byCentroGestor() throws Exception {
        when(dpeRepository.findByENTAndEJEAndDep_Cge_CGECOD(1, "E1", "CG1")).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("cgecod", "CG1")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_byServicioCod() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1")).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("servicio", "S1")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_byServicioName() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1")).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("servicio", "Service Description")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_byPerfilAlmacen() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1")).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("perfil", "almacen")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_byPerfilComprador() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1")).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("perfil", "comprador")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_byPerfilContabilidad() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1")).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("perfil", "contabilidad")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_byPerfilPeticionario() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1")).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("perfil", "peticionario")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_byInvalidPerfil() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1")).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("perfil", "invalid")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_dataAccessException() throws Exception {
        when(dpeRepository.findByENTAndEJE(anyInt(), anyString()))
            .thenThrow(new DataAccessResourceFailureException("DB error"));

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Error :")));
    }

    @Test
    void fetchPersonasServicios_returns500OnException() throws Exception {
        when(dpeRepository.findByENTAndEJE(anyInt(), anyString(), any()))
            .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/api/depe/personas-servicios/1/E1/0")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isInternalServerError())
            .andExpect(content().string(containsString("Error :")));
    }

    @Test
    void deleteAllPersonaServices_returns500OnDataAccessException() throws Exception {
        when(dpeRepository.findByENTAndEJEAndPERCOD(1, "E1", "U1")).thenReturn(List.of(new Dpe()));
        when(dpeRepository.deleteByENTAndEJEAndPERCOD(1, "E1", "U1"))
            .thenThrow(new DataAccessResourceFailureException("DB error"));

        mockMvc.perform(delete("/api/depe/delete-persona-Allservice/1/E1/U1"))
            .andDo(print())
            .andExpect(status().isInternalServerError());
    }

    @Test
    void addPersonaServices_exception() throws Exception {
        PersonaServiceRequest req = new PersonaServiceRequest();
        req.setPercod("U1");
        req.setServices(List.of("S1"));

        doThrow(new RuntimeException("Save failed")).when(dpeService).savePersonaServices(any());

        mockMvc.perform(post("/api/depe/add-persona-services")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andDo(print())
            .andExpect(status().isInternalServerError())
            .andExpect(content().string(containsString("Error :")));
    }

    @Test
    void addServicesPersona_exception() throws Exception {
        ServicePersonaRequest req = new ServicePersonaRequest();
        req.setDepcod("S1");
        req.setPersonas(List.of("U1"));

        doThrow(new RuntimeException("Save failed")).when(dpePersonasForService).saveServicePersonas(any());

        mockMvc.perform(post("/api/depe/add-services-persona")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andDo(print())
            .andExpect(status().isInternalServerError())
            .andExpect(content().string(containsString("Error :")));
    }

    @Test
    void fetchPersonaService_returns500OnDataAccessException() throws Exception {
        when(dpeRepository.findByENTAndEJEAndPERCOD(anyInt(), anyString(), anyString()))
            .thenThrow(new DataAccessResourceFailureException("DB error"));

        mockMvc.perform(get("/api/depe/fetch-persona-service/1/E1/U1"))
            .andDo(print())
            .andExpect(status().isInternalServerError());
    }

    @Test
    void deletePersonaService_returns500OnDataAccessException() throws Exception {
        when(dpeRepository.existsById(any())).thenReturn(true);
        doThrow(new DataAccessResourceFailureException("DB error")).when(dpeRepository).deleteById(any());

        mockMvc.perform(delete("/api/depe/delete-persona-service/1/E1/D1/U1"))
            .andDo(print())
            .andExpect(status().isInternalServerError());
    }

    @Test
    void deleteService_returns500OnDataAccessException() throws Exception {
        when(dpeRepository.existsById(any())).thenReturn(true);
        doThrow(new DataAccessResourceFailureException("DB error")).when(dpeRepository).deleteById(any());

        mockMvc.perform(delete("/api/depe/delete-service-persona/1/E1/D1/U1"))
            .andDo(print())
            .andExpect(status().isInternalServerError());
    }
    
    @Test
    void deleteAllPersonaServices_deleteReturnsZero() throws Exception {
        when(dpeRepository.findByENTAndEJEAndPERCOD(1, "E1", "U1")).thenReturn(List.of(new Dpe()));
        when(dpeRepository.deleteByENTAndEJEAndPERCOD(1, "E1", "U1")).thenReturn(0);

        mockMvc.perform(delete("/api/depe/delete-persona-Allservice/1/E1/U1"))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(content().string("Sin resultado"));
    }

    @Test
    void addPersonaServices_withEmptyPercod() throws Exception {
        PersonaServiceRequest req = new PersonaServiceRequest();
        req.setPercod("  ");
        req.setServices(List.of("S1"));

        mockMvc.perform(post("/api/depe/add-persona-services")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Falta un dato obligatorio")));
    }

    @Test
    void addServicesPersona_withEmptyDepcod() throws Exception {
        ServicePersonaRequest req = new ServicePersonaRequest();
        req.setDepcod("  ");
        req.setPersonas(List.of("U1"));

        mockMvc.perform(post("/api/depe/add-services-persona")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("El código de servicio es obligatorio.")));
    }

    @Test
    void addPersonaServices_withEmptyServices() throws Exception {
        PersonaServiceRequest req = new PersonaServiceRequest();
        req.setPercod("U1");
        req.setServices(List.of());

        mockMvc.perform(post("/api/depe/add-persona-services")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Debe seleccionar al menos un servicio.")));
    }

    @Test
    void addServicesPersona_withEmptyPersonas() throws Exception {
        ServicePersonaRequest req = new ServicePersonaRequest();
        req.setDepcod("S1");
        req.setPersonas(List.of());

        mockMvc.perform(post("/api/depe/add-services-persona")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Debe seleccionar al menos un persona.")));
    }

    @Test
    void searchPersonasServicios_withMultipleFilters_persona_and_servicio() throws Exception {
        when(dpeRepository.findByENTAndEJEAndPer_PERNOMContaining(1, "E1", "John")).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("persona", "John")
                .param("servicio", "Service")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_withMultipleFilters_cgecod_and_perfil() throws Exception {
        when(dpeRepository.findByENTAndEJEAndDep_Cge_CGECOD(1, "E1", "CG1")).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("cgecod", "CG1")
                .param("perfil", "almacen")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void fetchServicePersonas_verifiesRepositoryCalls() throws Exception {
        Dpe d1 = new Dpe(); d1.setPERCOD("U1");
        when(dpeRepository.findByENTAndEJEAndDEPCOD(1, "E1", "D1")).thenReturn(List.of(d1));

        Per p1 = new Per(); p1.setPERCOD("U1"); p1.setPERNOM("User");
        when(perRepository.findByPERCODIn(List.of("U1"))).thenReturn(List.of(p1));

        mockMvc.perform(get("/api/depe/fetch-service-personas/1/E1/D1"))
            .andDo(print())
            .andExpect(status().isOk());

        verify(dpeRepository).findByENTAndEJEAndDEPCOD(1, "E1", "D1");
        verify(perRepository).findByPERCODIn(List.of("U1"));
    }

    @Test
    void fetchPersonaService_verifiesRepositoryCalls() throws Exception {
        Dpe d1 = new Dpe(); d1.setDEPCOD("D1");
        when(dpeRepository.findByENTAndEJEAndPERCOD(1, "E1", "U1")).thenReturn(List.of(d1));

        Dep dep = new Dep(); dep.setDEPCOD("D1"); dep.setDEPDES("Desc");
        when(depRepository.findByENTAndEJEAndDEPCODIn(1, "E1", List.of("D1"))).thenReturn(List.of(dep));

        mockMvc.perform(get("/api/depe/fetch-persona-service/1/E1/U1"))
            .andDo(print())
            .andExpect(status().isOk());

        verify(dpeRepository).findByENTAndEJEAndPERCOD(1, "E1", "U1");
        verify(depRepository).findByENTAndEJEAndDEPCODIn(1, "E1", List.of("D1"));
    }

    @Test
    void deletePersonaService_verifiesRepositoryCalls() throws Exception {
        when(dpeRepository.existsById(any())).thenReturn(true);

        mockMvc.perform(delete("/api/depe/delete-persona-service/1/E1/D1/U1"))
            .andDo(print())
            .andExpect(status().isNoContent());

        verify(dpeRepository).existsById(any());
        verify(dpeRepository).deleteById(any());
    }

    @Test
    void deleteService_verifiesRepositoryCalls() throws Exception {
        when(dpeRepository.existsById(any())).thenReturn(true);

        mockMvc.perform(delete("/api/depe/delete-service-persona/1/E1/D1/U1"))
            .andDo(print())
            .andExpect(status().isNoContent());

        verify(dpeRepository).existsById(any());
        verify(dpeRepository).deleteById(any());
    }

    @Test
    void addPersonaServices_verifiesServiceCall() throws Exception {
        PersonaServiceRequest req = new PersonaServiceRequest();
        req.setPercod("U1");
        req.setServices(List.of("S1", "S2", "S3"));

        mockMvc.perform(post("/api/depe/add-persona-services")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andDo(print())
            .andExpect(status().isCreated());

        ArgumentCaptor<PersonaServiceRequest> cap = ArgumentCaptor.forClass(PersonaServiceRequest.class);
        verify(dpeService).savePersonaServices(cap.capture());
        PersonaServiceRequest captured = cap.getValue();
        assertEquals("U1", captured.getPercod());
        assertEquals(3, captured.getServices().size());
    }

    @Test
    void addServicesPersona_verifiesServiceCall() throws Exception {
        ServicePersonaRequest req = new ServicePersonaRequest();
        req.setDepcod("S1");
        req.setPersonas(List.of("U1", "U2", "U3"));

        mockMvc.perform(post("/api/depe/add-services-persona")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andDo(print())
            .andExpect(status().isCreated());

        ArgumentCaptor<ServicePersonaRequest> cap = ArgumentCaptor.forClass(ServicePersonaRequest.class);
        verify(dpePersonasForService).saveServicePersonas(cap.capture());
        ServicePersonaRequest captured = cap.getValue();
        assertEquals("S1", captured.getDepcod());
        assertEquals(3, captured.getPersonas().size());
    }

    @Test
    void fetchPersonasServicios_withHighPageNumber() throws Exception {
        when(dpeRepository.findByENTAndEJE(anyInt(), anyString(), any())).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/1/E1/100")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_caseInsensitivePerfilAlmacen() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1")).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("perfil", "ALMACEN")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_caseInsensitivePerfilComprador() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1")).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("perfil", "COMPRADOR")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_byPersonaCodeShort() throws Exception {
        when(dpeRepository.findProjectionByENTAndEJEAndPERCOD(1, "E1", "P1")).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("persona", "P1")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_byServiceCodeShort() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1")).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("servicio", "SC123")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void fetchServicePersonas_withDistinctPersonas() throws Exception {
        Dpe d1 = new Dpe(); d1.setPERCOD("U1");
        Dpe d2 = new Dpe(); d2.setPERCOD("U1");
        Dpe d3 = new Dpe(); d3.setPERCOD("U2");
        when(dpeRepository.findByENTAndEJEAndDEPCOD(1, "E1", "D1")).thenReturn(List.of(d1, d2, d3));

        Per p1 = new Per(); p1.setPERCOD("U1"); p1.setPERNOM("User One");
        Per p2 = new Per(); p2.setPERCOD("U2"); p2.setPERNOM("User Two");
        when(perRepository.findByPERCODIn(List.of("U1", "U2"))).thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/api/depe/fetch-service-personas/1/E1/D1"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void fetchPersonaService_withMultipleDeps() throws Exception {
        Dpe d1 = new Dpe(); d1.setDEPCOD("D1");
        Dpe d2 = new Dpe(); d2.setDEPCOD("D2");
        when(dpeRepository.findByENTAndEJEAndPERCOD(1, "E1", "U1")).thenReturn(List.of(d1, d2));

        Dep dep1 = new Dep(); dep1.setDEPCOD("D1"); dep1.setDEPDES("Service 1");
        Dep dep2 = new Dep(); dep2.setDEPCOD("D2"); dep2.setDEPDES("Service 2");
        when(depRepository.findByENTAndEJEAndDEPCODIn(1, "E1", List.of("D1", "D2"))).thenReturn(List.of(dep1, dep2));

        mockMvc.perform(get("/api/depe/fetch-persona-service/1/E1/U1"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void searchPersonasServicios_byPersonaCode_and_cgecod() throws Exception {
        when(dpeRepository.findProjectionByENTAndEJEAndPERCOD(1, "E1", "U1")).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("persona", "U1")
                .param("cgecod", "CG1")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void fetchPersonasServicios_verifyPageable() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1", PageRequest.of(2, 20))).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/1/E1/2")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteAllPersonaServices_dataAccessExceptionOnDelete() throws Exception {
        when(dpeRepository.findByENTAndEJEAndPERCOD(1, "E1", "U1")).thenReturn(List.of(new Dpe()));
        when(dpeRepository.deleteByENTAndEJEAndPERCOD(1, "E1", "U1"))
            .thenThrow(new DataAccessResourceFailureException("DB constraint"));

        mockMvc.perform(delete("/api/depe/delete-persona-Allservice/1/E1/U1"))
            .andDo(print())
            .andExpect(status().isInternalServerError())
            .andExpect(content().string(containsString("error durante la eliminación:")));
    }

    @Test
    void searchPersonasServicios_emptyPersonaParam() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1")).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("persona", "")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_emptyServicioParam() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1")).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("servicio", "")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_emptyCgecod() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1")).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("cgecod", "")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_emptyPerfilParam() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1")).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("perfil", "")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void addPersonaServices_withNullServices() throws Exception {
        PersonaServiceRequest req = new PersonaServiceRequest();
        req.setPercod("U1");
        req.setServices(null);

        mockMvc.perform(post("/api/depe/add-persona-services")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    void addServicesPersona_withNullPersonas() throws Exception {
        ServicePersonaRequest req = new ServicePersonaRequest();
        req.setDepcod("S1");
        req.setPersonas(null);

        mockMvc.perform(post("/api/depe/add-services-persona")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    void fetchServicePersonas_allDistinctPersonas() throws Exception {
        Dpe d1 = new Dpe(); d1.setPERCOD("U1");
        Dpe d2 = new Dpe(); d2.setPERCOD("U2");
        Dpe d3 = new Dpe(); d3.setPERCOD("U3");
        when(dpeRepository.findByENTAndEJEAndDEPCOD(1, "E1", "D1")).thenReturn(List.of(d1, d2, d3));

        Per p1 = new Per(); p1.setPERCOD("U1"); p1.setPERNOM("User One");
        Per p2 = new Per(); p2.setPERCOD("U2"); p2.setPERNOM("User Two");
        Per p3 = new Per(); p3.setPERCOD("U3"); p3.setPERNOM("User Three");
        when(perRepository.findByPERCODIn(List.of("U1", "U2", "U3"))).thenReturn(List.of(p1, p2, p3));

        mockMvc.perform(get("/api/depe/fetch-service-personas/1/E1/D1"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[*].percod", containsInAnyOrder("U1", "U2", "U3")));
    }

    @Test
    void fetchPersonaService_allDistinctDeps() throws Exception {
        Dpe d1 = new Dpe(); d1.setDEPCOD("D1");
        Dpe d2 = new Dpe(); d2.setDEPCOD("D2");
        Dpe d3 = new Dpe(); d3.setDEPCOD("D3");
        when(dpeRepository.findByENTAndEJEAndPERCOD(1, "E1", "U1")).thenReturn(List.of(d1, d2, d3));

        Dep dep1 = new Dep(); dep1.setDEPCOD("D1"); dep1.setDEPDES("Service 1");
        Dep dep2 = new Dep(); dep2.setDEPCOD("D2"); dep2.setDEPDES("Service 2");
        Dep dep3 = new Dep(); dep3.setDEPCOD("D3"); dep3.setDEPDES("Service 3");
        when(depRepository.findByENTAndEJEAndDEPCODIn(1, "E1", List.of("D1", "D2", "D3"))).thenReturn(List.of(dep1, dep2, dep3));

        mockMvc.perform(get("/api/depe/fetch-persona-service/1/E1/U1"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[*].depcod", containsInAnyOrder("D1", "D2", "D3")));
    }

    @Test
    void searchPersonasServicios_filterByServicio_shortCode() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1"))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("servicio", "D1"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_filterByServicio_longDescription() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1"))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("servicio", "Service Name"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_filterByServicio_withWhitespace() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1"))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("servicio", "   S1   "))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_filterByPersona_shortCode() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1"))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("persona", "U1"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_filterByPersona_longName() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1"))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("persona", "Very Long Persona Name"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_filterByPersona_withWhitespace() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1"))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("persona", "   U1   "))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_filterByPerfil_almacen() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1"))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("perfil", "almacen"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_filterByPerfil_comprador() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1"))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("perfil", "comprador"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_filterByPerfil_contabilidad() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1"))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("perfil", "contabilidad"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_filterByPerfil_peticionario() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1"))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("perfil", "peticionario"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_filterByPerfil_caseInsensitive() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1"))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("perfil", "ALMACEN"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_multipleFilters_servicio_and_persona() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1"))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("servicio", "D1")
                .param("persona", "U1"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_multipleFilters_all() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1"))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("servicio", "Service")
                .param("persona", "Juan")
                .param("cgecod", "CGE1")
                .param("perfil", "almacen"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_multipleFilters_servicio_and_cgecod() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1"))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("servicio", "S1")
                .param("cgecod", "CGE1"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_multipleFilters_persona_and_perfil() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1"))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("persona", "User")
                .param("perfil", "comprador"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_multipleFilters_cgecod_and_perfil() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1"))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("cgecod", "CGE1")
                .param("perfil", "contabilidad"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_emptyFilterValues_servicio() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1"))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("servicio", ""))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_cgecod_filtering() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1"))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("cgecod", "CENTER1"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_nullDataHandling() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1"))
            .thenReturn(null);

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1"))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(content().string("Sin resultado"));
    }

    @Test
    void searchPersonasServicios_emptyPersona_multipleFilters() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1"))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("persona", "")
                .param("servicio", "S1")
                .param("perfil", "almacen"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_whitespaceOnlyFilters() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1"))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("servicio", "     ")
                .param("persona", "     "))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_filterByPerfil_invalidType() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1"))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("perfil", "invalidProfile"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_filterByPerfil_almacenUppercase() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1"))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("perfil", "ALMACEN"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_filteredServicio_emptyPersona() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1"))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("servicio", "SRV01")
                .param("persona", ""))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_cgecod_nullHandling() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1"))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("cgecod", ""))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_filtersWithEmptyPerfil() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1"))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("perfil", ""))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_filterByPersona_boundary20Chars() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1"))
            .thenReturn(List.of());
        
        String persona20 = "12345678901234567890";
        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("persona", persona20))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_filterByPersona_boundary21Chars() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1"))
            .thenReturn(List.of());
        
        String persona21 = "123456789012345678901";
        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("persona", persona21))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_filterByServicio_boundary6Chars() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1"))
            .thenReturn(List.of());
        
        String servicio6 = "SERV01";
        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("servicio", servicio6))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_filterByServicio_boundary7Chars() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1"))
            .thenReturn(List.of());
        
        String servicio7 = "SERV001";
        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("servicio", servicio7))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_helperMethods_filterByServicioShort() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1")).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("servicio", "SRV"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_helperMethods_filterByServicioLong() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1")).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("servicio", "LongServiceDescription"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_helperMethods_filterByPersonaShort() throws Exception {
        when(dpeRepository.findProjectionByENTAndEJEAndPERCOD(1, "E1", "USR01")).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("persona", "USR01"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_helperMethods_filterByPersonaLong() throws Exception {
        when(dpeRepository.findByENTAndEJEAndPer_PERNOMContaining(1, "E1", "VeryLongPersonaName"))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("persona", "VeryLongPersonaName"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_helperMethods_filterByCgecod() throws Exception {
        when(dpeRepository.findByENTAndEJEAndDep_Cge_CGECOD(1, "E1", "CGE01")).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("cgecod", "CGE01"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_helperMethods_filterByPerfilAlmacen() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1")).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("perfil", "almacen"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_helperMethods_filterByPerfilComprador() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1")).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("perfil", "comprador"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_helperMethods_filterByPerfilContabilidad() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1")).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("perfil", "contabilidad"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_helperMethods_filterByPerfilPeticionario() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1")).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("perfil", "peticionario"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_helperMethods_multipleFiltersServicioAndPersona() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1")).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("servicio", "SRV01")
                .param("persona", "USR01"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_helperMethods_multipleFiltersCgecodAndPerfil() throws Exception {
        when(dpeRepository.findByENTAndEJEAndDep_Cge_CGECOD(1, "E1", "CGE01")).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("cgecod", "CGE01")
                .param("perfil", "almacen"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_helperMethods_applyFiltersWithAllParameters() throws Exception {
        when(dpeRepository.findByENTAndEJEAndDep_Cge_CGECOD(1, "E1", "CGE01")).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("servicio", "SRV01")
                .param("persona", "USR01")
                .param("cgecod", "CGE01")
                .param("perfil", "comprador"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_helperMethods_nullSafeListWithNull() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1")).thenReturn(null);

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_helperMethods_nullSafeListWithEmpty() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1")).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_helperMethods_filterByPerfilInvalid() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1")).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("perfil", "invalidPerfil"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_helperMethods_emptyServicioFilter() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1")).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("servicio", ""))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_helperMethods_emptyPersonaFilter() throws Exception {
        when(dpeRepository.findByENTAndEJE(1, "E1")).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("persona", ""))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_helperMethods_boundary20CharPersona() throws Exception {
        String persona20 = "12345678901234567890";
        when(dpeRepository.findProjectionByENTAndEJEAndPERCOD(1, "E1", persona20)).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("persona", persona20))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_helperMethods_boundary21CharPersona() throws Exception {
        String persona21 = "123456789012345678901";
        when(dpeRepository.findByENTAndEJEAndPer_PERNOMContaining(1, "E1", persona21)).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("persona", persona21))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_helperMethods_boundary6CharServicio() throws Exception {
        String servicio6 = "SERV01";
        when(dpeRepository.findByENTAndEJE(1, "E1")).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("servicio", servicio6))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPersonasServicios_helperMethods_boundary7CharServicio() throws Exception {
        String servicio7 = "SERV0001";
        when(dpeRepository.findByENTAndEJE(1, "E1")).thenReturn(List.of());

        mockMvc.perform(get("/api/depe/personas-servicios/search")
                .param("ent", "1")
                .param("eje", "E1")
                .param("servicio", servicio7))
            .andDo(print())
            .andExpect(status().isNotFound());
    }
}