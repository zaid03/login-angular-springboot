package com.example.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.sqlserver2.model.Dep;
import com.example.backend.sqlserver2.model.Dpe;
import com.example.backend.sqlserver2.repository.DepRepository;
import com.example.backend.sqlserver2.repository.DpeRepository;

@RestController
@RequestMapping("/api/dep")
public class DepController {
    @Autowired
    private DepRepository depRepository;
    @Autowired
    private DpeRepository dpeRepository;

    //fetching all services
    @GetMapping("/fetch-services/{ent}/{eje}/{percod}")
    public ResponseEntity<?> fetchAllservices(
        @PathVariable Integer ent,
        @PathVariable String eje,
        @PathVariable String percod
    ) {
        try {
            List<Dep> services = depRepository.findByEntAndEjeAndPercod(ent, eje, percod);
            if (services.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontraron servicios");
            }
            return ResponseEntity.ok(services);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al consultar servicios: " + ex.getMostSpecificCause().getMessage());
        }
    }

    //modifying a service
    public record serviceUpdate(String depdes, Integer depalm, Integer depcom, Integer depint, String ccocod) {}
    @PatchMapping("/update-service/{ent}/{eje}/{depcod}")
    public ResponseEntity<?> updateCentro(
        @PathVariable Integer ent,
        @PathVariable String eje,
        @PathVariable String depcod,
        @RequestBody serviceUpdate payload
    ) {
        try {
            if (payload == null || payload.depdes() == null || payload.depalm() == null || payload.depcom() == null || payload.depint() == null || payload.ccocod() == null) {
                return ResponseEntity.badRequest().body("Faltan datos obligatorios.");
            }

            int updated = depRepository.updateService(
                payload.depdes(),
                payload.depalm(),
                payload.depcom(),
                payload.depint(),
                payload.ccocod(),
                ent,
                eje,
                depcod
            );

            if (updated == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró ninguna centro gestor para los datos.");
            }
            return ResponseEntity.noContent().build();
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Update failed: " + ex.getMostSpecificCause().getMessage());
        }
    }

    //adding a service
    public record serviceAdd(Integer ent, String eje, String depcod, String depdes, Integer depalm, Integer depcom, Integer depint, String ccocod, String cgecod, String percod) {}
    @PostMapping("/Insert-service")
    public ResponseEntity<?> addCentroGestor(
        @RequestBody serviceAdd payload
    ) {
        try {
            if (payload == null || payload.ent() == null || payload.eje() == null || payload.depcod() == null || payload.depdes() == null || payload.depalm() == null || payload.depcom() == null || payload.depint() == null || payload.ccocod() == null || payload.cgecod() == null || payload.percod == null) {
                return ResponseEntity.badRequest().body("Faltan datos obligatorios.");
            }
            if(!depRepository.findByENTAndEJEAndDEPCOD(payload.ent(), payload.eje(), payload.depcod()).isEmpty()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Este servicio ya existe   .");
            }

            Dep nueva = new Dep();
            nueva.setENT(payload.ent());
            nueva.setEJE(payload.eje());
            nueva.setDEPCOD(payload.depcod());
            nueva.setDEPDES(payload.depdes());
            nueva.setDEPALM(payload.depalm());
            nueva.setDEPCOM(payload.depcom());
            nueva.setCCOCOD(payload.ccocod());
            nueva.setCGECOD(payload.cgecod());
            depRepository.save(nueva);

            Dpe nuevoDpe = new Dpe();
            nuevoDpe.setENT(payload.ent());
            nuevoDpe.setEJE(payload.eje());
            nuevoDpe.setDEPCOD(payload.depcod());
            nuevoDpe.setPERCOD(payload.percod());
            dpeRepository.save(nuevoDpe);
            
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Update failed: " + ex.getMostSpecificCause().getMessage());
        }
    }

    //modifying rest of service 
    public record ServiceUpdateSecond(
    String depd1c, String depd1d,
    String depd2c, String depd2d,
    String depd3c, String depd3d,
    String depdco, String depden) {}
    @PatchMapping("/update-service-second/{ent}/{eje}/{depcod}")
    public ResponseEntity<?> updateServiceSecond(
        @PathVariable Integer ent,
        @PathVariable String eje,
        @PathVariable String depcod,
        @RequestBody ServiceUpdateSecond payload
    ) {
        try {
            if (payload == null ||
                payload.depd1c() == null || payload.depd1d() == null ||
                payload.depd2c() == null || payload.depd2d() == null ||
                payload.depd3c() == null || payload.depd3d() == null ||
                payload.depdco() == null || payload.depden() == null) {
                return ResponseEntity.badRequest().body("Faltan datos obligatorios.");
            }

            int updated = depRepository.updateServiceSecond(
                payload.depd1c(), payload.depd1d(),
                payload.depd2c(), payload.depd2d(),
                payload.depd3c(), payload.depd3d(),
                payload.depdco(), payload.depden(),
                ent, eje, depcod
            );

            if (updated == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró ninguna centro gestor para los datos.");
            }
            return ResponseEntity.noContent().build();
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Update failed: " + ex.getMostSpecificCause().getMessage());
        }
    }
    
    //search function
    @GetMapping("/search")
    public ResponseEntity<?> searchServices(
        @RequestParam Integer ent,
        @RequestParam String eje,
        @RequestParam String percod,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) String cgecod,
        @RequestParam(required = false) String perfil
    ) {
        try {
            List<Dep> results = depRepository.searchServices(ent, eje, percod, search, cgecod, perfil);
            if (results.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontraron servicios con los filtros dados.");
            }
            return ResponseEntity.ok(results);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al buscar servicios: " + ex.getMostSpecificCause().getMessage());
        }
    }
}
