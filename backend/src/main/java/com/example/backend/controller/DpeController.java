package com.example.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.DepCodDesDto;
import com.example.backend.dto.PersonaDto;
import com.example.backend.dto.PersonaServiceRequest;
import com.example.backend.dto.ServicePersonaRequest;
import com.example.backend.dto.personasPorServiciosProjection;
import com.example.backend.service.DpePersonasForService;
import com.example.backend.service.DpeService;
import com.example.backend.sqlserver2.model.Dep;
import com.example.backend.sqlserver2.model.Dpe;
import com.example.backend.sqlserver2.model.DpeId;
import com.example.backend.sqlserver2.model.Per;
import com.example.backend.sqlserver2.repository.DepRepository;
import com.example.backend.sqlserver2.repository.DpeRepository;
import com.example.backend.sqlserver2.repository.PerRepository;

@RestController
@RequestMapping("/api/depe")
public class DpeController {
    @Autowired
    private DpeRepository dpeRepository;

    @Autowired
    private PerRepository perRepository;

    @Autowired 
    private DepRepository depRepository;

    private static final String SIN_RESULTADO = "Sin resultado";
    private static final String ERROR = "Error :";
    private static final String Peticionario = "peticionario";

    //for adding personas to services and vice versa
    private final DpeService dpeService;
    private final DpePersonasForService dpePersonasForService;

    public DpeController(DpeService dpeService, DpePersonasForService dpePersonasForService) {
        this.dpeService = dpeService;
        this.dpePersonasForService = dpePersonasForService;
    }

    //selecting personas for servicios
    @GetMapping("/fetch-service-personas/{ent}/{eje}/{depcod}")
    public ResponseEntity<?> fetchAllservices(
        @PathVariable Integer ent,
        @PathVariable String eje,
        @PathVariable String depcod
    ) {
        try {
            List<Dpe> dpes = dpeRepository.findByENTAndEJEAndDEPCOD(ent, eje, depcod);

            List<String> percods = dpes.stream()
                .map(Dpe::getPERCOD)
                .distinct()
                .toList();

            List<Per> personas = perRepository.findByPERCODIn(percods);

            List<PersonaDto> result = personas.stream()
                .map(p -> new PersonaDto(p.getPERCOD(), p.getPERNOM()))
                .toList();

            if (result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(SIN_RESULTADO);
            }
            return ResponseEntity.ok(result);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ERROR + ex.getMostSpecificCause().getMessage());
        }
    } 

    //deleting a persona from a service
    @DeleteMapping("/delete-persona-service/{ent}/{eje}/{depcod}/{percod}")
    public ResponseEntity<?> fetchAllservices(
        @PathVariable Integer ent,
        @PathVariable String eje,
        @PathVariable String depcod,
        @PathVariable String percod
    ) {
        try {
            DpeId id = new DpeId(ent, eje, depcod, percod);
            if(!dpeRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(SIN_RESULTADO);
            }

            dpeRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ERROR + ex.getMostSpecificCause().getMessage());
        }
    }

    //selecting a persona's services
    @GetMapping("/fetch-persona-service/{ent}/{eje}/{percod}")
    public ResponseEntity<?> fetchPersonaService(
         @PathVariable Integer ent,
        @PathVariable String eje,
        @PathVariable String percod
    ) {
        try{
            List<Dpe> dpes = dpeRepository.findByENTAndEJEAndPERCOD(ent, eje, percod);
            if (dpes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(SIN_RESULTADO);
            }

            List<String> depcods = dpes.stream()
            .map(Dpe::getDEPCOD)
            .distinct()
            .toList();

            List<Dep> deps = depRepository.findByENTAndEJEAndDEPCODIn(ent, eje, depcods);

            List<DepCodDesDto> result = deps.stream()
                .map(d -> new DepCodDesDto(d.getDEPCOD(), d.getDEPDES()))
                .toList();

            return ResponseEntity.ok(result);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ERROR + ex.getMostSpecificCause().getMessage());
        }
    }

    //deleting a persona's services
    @DeleteMapping("/delete-service-persona/{ent}/{eje}/{depcod}/{percod}")
    public ResponseEntity<?> deleteService(
        @PathVariable Integer ent,
        @PathVariable String eje,
        @PathVariable String depcod,
        @PathVariable String percod
    ) {
        try {
            DpeId id = new DpeId(ent, eje, depcod, percod);
            if(!dpeRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(SIN_RESULTADO);
            }

            dpeRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ERROR + ex.getMostSpecificCause().getMessage());
        }
    }

    //adding serivces for a person
    @PostMapping("/add-persona-services")
    public ResponseEntity<?> addPersonaServices(@RequestBody PersonaServiceRequest request) {
        try {
            if (request.getPercod() == null || request.getPercod().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Falta un dato obligatorio");
            }
            if (request.getServices() == null || request.getServices().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Debe seleccionar al menos un servicio.");
            }

            dpeService.savePersonaServices(request);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ERROR + ex.getMessage());
        }
    }

    //needed for copy perfil function
    @DeleteMapping("/delete-persona-Allservice/{ent}/{eje}/{percod}")
    public ResponseEntity<?> deleteServices(
        @PathVariable Integer ent,
        @PathVariable String eje,
        @PathVariable String percod
    ) {
        try {
            List<Dpe> existing = dpeRepository.findByENTAndEJEAndPERCOD(ent, eje, percod);
            if (existing.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(SIN_RESULTADO);
            }

            int deletedd = dpeRepository.deleteByENTAndEJEAndPERCOD(ent, eje, percod);
            if (deletedd == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(SIN_RESULTADO);
            }
            return ResponseEntity.noContent().build();
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error durante la eliminación: " + ex.getMostSpecificCause().getMessage());
        }
    }

    //adding serivces for a person
    @PostMapping("/add-services-persona")
    public ResponseEntity<?> addServicePersonas(@RequestBody ServicePersonaRequest request) {
        try {
            if (request.getDepcod() == null || request.getDepcod().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El código de servicio es obligatorio.");
            }
            if (request.getPersonas() == null || request.getPersonas().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Debe seleccionar al menos un persona.");
            }

            dpePersonasForService.saveServicePersonas(request);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ERROR + ex.getMessage());
        }
    }

    //selecting personas por servicios
    @GetMapping("/personas-servicios/{ent}/{eje}/{page}")
    public ResponseEntity<?> fetchPersonasServicios(
        @PathVariable Integer ent,
        @PathVariable String eje,
        @PathVariable Integer page
    ) {
        try {
            int size = 20;
            Pageable pageable = PageRequest.of(page, size);
            List<personasPorServiciosProjection> personas = dpeRepository.findByENTAndEJE(ent, eje, pageable);

            if (personas == null || personas.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(SIN_RESULTADO);
            }

            return ResponseEntity.ok(personas);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ERROR + ex.getMessage());
        }
    }

    //search in personas de servicio
    @GetMapping("/personas-servicios/search")
    public ResponseEntity<?> searchPersonasServicios(
        @RequestParam Integer ent,
        @RequestParam String eje,
        @RequestParam(required = false) String servicio,
        @RequestParam(required = false) String persona,
        @RequestParam(required = false) String cgecod,
        @RequestParam(required = false) String perfil
    ) {
        try {
            String perfilType = mapPerfilType(perfil);
            
            List<personasPorServiciosProjection> result = getInitialData(ent, eje, servicio, persona, cgecod);
            result = applyFilters(result, servicio, persona, cgecod, perfilType);

            if (result == null || result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(SIN_RESULTADO);
            }
            return ResponseEntity.ok(result);
            
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ERROR + ex.getMostSpecificCause().getMessage());
        }
    }

    private String mapPerfilType(String perfil) {
        if (perfil == null || perfil.isEmpty()) {
            return null;
        }
        return switch (perfil.toLowerCase()) {
            case "almacen" -> "depalm";
            case "comprador" -> "depcom";
            case "contabilidad" -> "depint";
            case Peticionario -> Peticionario;
            default -> null;
        };
    }

    private record FilterFlags(boolean hasServicio, boolean hasPersona, boolean hasCgecod, boolean hasPerfil) {}
    private FilterFlags buildFilterFlags(String servicio, String persona, String cgecod, String perfil) {
        return new FilterFlags(
            servicio != null && !servicio.isEmpty(),
            persona != null && !persona.isEmpty(),
            cgecod != null && !cgecod.isEmpty(),
            perfil != null
        );
    }

    private List<personasPorServiciosProjection> getInitialData(Integer ent, String eje, String servicio, String persona, String cgecod) {
        if (cgecod != null && !cgecod.isEmpty()) {
            return nullSafeList(dpeRepository.findByENTAndEJEAndDep_Cge_CGECOD(ent, eje, cgecod));
        }
        if (persona != null && !persona.isEmpty() && persona.length() <= 20) {
            return nullSafeList(dpeRepository.findProjectionByENTAndEJEAndPERCOD(ent, eje, persona));
        }
        if (persona != null && !persona.isEmpty()) {
            return nullSafeList(dpeRepository.findByENTAndEJEAndPer_PERNOMContaining(ent, eje, persona));
        }
        return nullSafeList(dpeRepository.findByENTAndEJE(ent, eje));
    }

    private List<personasPorServiciosProjection> applyFilters(
        List<personasPorServiciosProjection> data, 
        String servicio, 
        String persona, 
        String cgecod,
        String perfilType
    ) {
        if (servicio != null && !servicio.isEmpty()) {
            data = filterByServicio(data, servicio);
        }
        if (persona != null && !persona.isEmpty()) {
            data = filterByPersona(data, persona);
        }
        if (data != null && cgecod != null && !cgecod.isEmpty()) {
            data = data.stream()
                .filter(p -> p.getDep().getCge().getCGECOD().equals(cgecod))
                .toList();
        }
        if (perfilType != null) {
            data = filterByPerfil(data, perfilType);
        }
        return data;
    }

    private List<personasPorServiciosProjection> filterByPerfil(List<personasPorServiciosProjection> data, String perfilType) {
        if (data == null || data.isEmpty()) return data;
        
        if (Peticionario.equals(perfilType)) {
            return data.stream()
                .filter(p -> p.getDep().getDEPALM() == 0 && p.getDep().getDEPCOM() == 0 && p.getDep().getDEPINT() == 0)
                .toList();
        } else if ("depalm".equals(perfilType)) {
            return data.stream().filter(p -> p.getDep().getDEPALM() == 1).toList();
        } else if ("depcom".equals(perfilType)) {
            return data.stream().filter(p -> p.getDep().getDEPCOM() == 1).toList();
        } else if ("depint".equals(perfilType)) {
            return data.stream().filter(p -> p.getDep().getDEPINT() == 1).toList();
        }
        return data;
    }

    private List<personasPorServiciosProjection> filterByPersona(List<personasPorServiciosProjection> data, String persona) {
        if (data == null || data.isEmpty()) return data;
        
        if (persona.length() <= 20) {
            return data.stream().filter(p -> persona.equals(p.getPERCOD())).toList();
        } else {
            return data.stream().filter(p -> p.getPer().getPERNOM().contains(persona)).toList();
        }
    }

    private List<personasPorServiciosProjection> filterByServicio(List<personasPorServiciosProjection> data, String servicio) {
        if (data == null || data.isEmpty()) return data;
        
        if (servicio.length() <= 6) {
            return data.stream().filter(p -> p.getDEPCOD().contains(servicio)).toList();
        } else {
            return data.stream().filter(p -> p.getDep().getDEPDES().contains(servicio)).toList();
        }
    }

    private List<personasPorServiciosProjection> nullSafeList(List<personasPorServiciosProjection> data) {
        return data == null ? List.of() : data;
    }
}