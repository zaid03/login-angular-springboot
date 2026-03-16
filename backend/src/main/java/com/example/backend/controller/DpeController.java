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
    @Autowired DepRepository depRepository;

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
            return ResponseEntity.ok(result);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + ex.getMostSpecificCause().getMessage());
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
                .body("Sin resultado");
            }

            dpeRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + ex.getMostSpecificCause().getMessage());
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
                    .body("Sin resultado");
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + ex.getMostSpecificCause().getMessage());
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
                .body("Sin resultado");
            }

            dpeRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + ex.getMostSpecificCause().getMessage());
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
                .body("Error: " + ex.getMessage());
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
                    .body("Sin resultado");
            }

            int deletedd = dpeRepository.deleteByENTAndEJEAndPERCOD(ent, eje, percod);
            if (deletedd == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Sin resultado");
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
                .body("Error: " + ex.getMessage());
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

            if (personas.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sin resultado");
            }

            return ResponseEntity.ok(personas);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + ex.getMessage());
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
            List<personasPorServiciosProjection> result = null;

            String perfilType = null;
            if (perfil != null && !perfil.isEmpty()) {
                switch (perfil.toLowerCase()) {
                    case "almacen": perfilType = "depalm"; break;
                    case "comprador": perfilType = "depcom"; break;
                    case "contabilidad": perfilType = "depint"; break;
                    case "peticionario": perfilType = "peticionario"; break;
                }
            }

            boolean hasServicio = servicio != null && !servicio.isEmpty();
            boolean hasPersona = persona != null && !persona.isEmpty();
            boolean hasCgecod = cgecod != null && !cgecod.isEmpty();
            boolean hasPerfil = perfilType != null;

            if (hasServicio && hasPersona && hasCgecod && hasPerfil) {
                List<personasPorServiciosProjection> data = dpeRepository.findByENTAndEJE(ent, eje);
                data = filterByServicio(data, servicio);
                data = filterByPersona(data, persona);
                data = filterByServicio(data, servicio); // Keep CGE filtering
                List<personasPorServiciosProjection> cgFiltered = data.stream()
                    .filter(p -> p.getDep().getCge().getCGECOD().equals(cgecod))
                    .toList();
                result = filterByPerfil(cgFiltered, perfilType);
            }
            else if (hasServicio && hasPersona && hasCgecod) {
                List<personasPorServiciosProjection> data = dpeRepository.findByENTAndEJE(ent, eje);
                data = filterByServicio(data, servicio);
                data = filterByPersona(data, persona);
                result = data.stream()
                    .filter(p -> p.getDep().getCge().getCGECOD().equals(cgecod))
                    .toList();
            }
            else if (hasServicio && hasPersona && hasPerfil) {
                List<personasPorServiciosProjection> data = dpeRepository.findByENTAndEJE(ent, eje);
                data = filterByServicio(data, servicio);
                data = filterByPersona(data, persona);
                result = filterByPerfil(data, perfilType);
            }
            else if (hasServicio && hasCgecod && hasPerfil) {
                List<personasPorServiciosProjection> data = dpeRepository.findByENTAndEJEAndDep_Cge_CGECOD(ent, eje, cgecod);
                data = filterByServicio(data, servicio);
                result = filterByPerfil(data, perfilType);
            }
            else if (hasPersona && hasCgecod && hasPerfil) {
                List<personasPorServiciosProjection> data;
                if (persona.length() <= 20) {
                    data = dpeRepository.findByENTAndEJEAndPERCODAndDep_Cge_CGECOD(ent, eje, persona, cgecod);
                } else {
                    data = dpeRepository.findByENTAndEJEAndPer_PERNOMContainingAndDep_Cge_CGECOD(ent, eje, persona, cgecod);
                }
                result = filterByPerfil(data, perfilType);
            }
            else if (hasServicio && hasPersona) {
                List<personasPorServiciosProjection> data = dpeRepository.findByENTAndEJE(ent, eje);
                data = filterByServicio(data, servicio);
                result = filterByPersona(data, persona);
            }
            else if (hasServicio && hasCgecod) {
                List<personasPorServiciosProjection> data = dpeRepository.findByENTAndEJEAndDep_Cge_CGECOD(ent, eje, cgecod);
                result = filterByServicio(data, servicio);
            }
            else if (hasPersona && hasCgecod) {
                if (persona.length() <= 20) {
                    result = dpeRepository.findByENTAndEJEAndPERCODAndDep_Cge_CGECOD(ent, eje, persona, cgecod);
                } else {
                    result = dpeRepository.findByENTAndEJEAndPer_PERNOMContainingAndDep_Cge_CGECOD(ent, eje, persona, cgecod);
                }
            }
            else if (hasServicio && hasPerfil) {
                List<personasPorServiciosProjection> data = dpeRepository.findByENTAndEJE(ent, eje);
                data = filterByServicio(data, servicio);
                result = filterByPerfil(data, perfilType);
            }
            else if (hasPersona && hasPerfil) {
                List<personasPorServiciosProjection> data;
                if (persona.length() <= 20) {
                    data = dpeRepository.findProjectionByENTAndEJEAndPERCOD(ent, eje, persona);
                } else {
                    data = dpeRepository.findByENTAndEJEAndPer_PERNOMContaining(ent, eje, persona);
                }
                result = filterByPerfil(data, perfilType);
            }
            else if (hasCgecod && hasPerfil) {
                List<personasPorServiciosProjection> data = dpeRepository.findByENTAndEJEAndDep_Cge_CGECOD(ent, eje, cgecod);
                result = filterByPerfil(data, perfilType);
            }
            else if (hasServicio) {
                List<personasPorServiciosProjection> data = dpeRepository.findByENTAndEJE(ent, eje);
                result = filterByServicio(data, servicio);
            }
            else if (hasPersona) {
                if (persona.length() <= 20) {
                    result = dpeRepository.findProjectionByENTAndEJEAndPERCOD(ent, eje, persona);
                } else {
                    result = dpeRepository.findByENTAndEJEAndPer_PERNOMContaining(ent, eje, persona);
                }
            }
            else if (hasCgecod) {
                result = dpeRepository.findByENTAndEJEAndDep_Cge_CGECOD(ent, eje, cgecod);
            }
            else if (hasPerfil) {
                List<personasPorServiciosProjection> data = dpeRepository.findByENTAndEJE(ent, eje);
                result = filterByPerfil(data, perfilType);
            }

            if (result == null || result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sin resultado");
            }
            return ResponseEntity.ok(result);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Error: " + ex.getMostSpecificCause().getMessage());
        }
    }

    private List<personasPorServiciosProjection> filterByPerfil(List<personasPorServiciosProjection> data, String perfilType) {
        if (data == null || data.isEmpty()) return data;
        
        if ("peticionario".equals(perfilType)) {
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
}