package com.example.backend.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.Operaciones;
import com.example.backend.service.OperacionesService;

@RestController
@RequestMapping("/api/sical")
@CrossOrigin(origins = "http://localhost:4200")
public class OperacionesController {

    private final OperacionesService operacionesService;

    public OperacionesController(OperacionesService operacionesService) {
        this.operacionesService = operacionesService;
    }

    @GetMapping("/operaciones")
    public ResponseEntity<?> getOperaciones(
        @RequestParam(name = "numeroOperDesde", required = false) String numeroOperDesde,
        @RequestParam(name = "numeroOperHasta", required = false) String numeroOperHasta,
        @RequestParam(name = "codigoOperacion", required = false) String codigoOperacion,
        @RequestParam(name = "signo", required = false) String signo,
        @RequestParam(name = "areaGestora", required = false) String areaGestora,
        @RequestParam(name = "fase", required = false) String fase,
        @RequestParam(name = "fechaOperDesde", required = false) String fechaOperDesde,
        @RequestParam(name = "fechaOperHasta", required = false) String fechaOperHasta,
        @RequestParam(name = "tercero", required = false) String tercero,
        @RequestParam(name = "ascendente", required = false) String ascendente,
        @RequestParam(name = "referencia", required = false) String referencia,
        @RequestParam(name = "organica", required = false) String organica,
        @RequestParam(name = "funcional", required = false) String funcional,
        @RequestParam(name = "economica", required = false) String economica,
        @RequestParam(name = "importeDesde", required = false) String importeDesde,
        @RequestParam(name = "importeHasta", required = false) String importeHasta,
        @RequestParam(name = "expediente", required = false) String expediente,
        @RequestParam(name = "grupoApunte", required = false) String grupoApunte,
        @RequestParam(name = "oficina", required = false) String oficina,
        @RequestParam(name = "fechaArqueo", required = false) String fechaArqueo,
        @RequestParam(name = "ordinal", required = false) String ordinal,
        @RequestParam(name = "codterr", required = false) String codterr,
        @RequestParam(name = "PActMun", required = false) String pActMun,
        @RequestParam(name = "ejeapli", required = false) String ejeapli,
        @RequestParam(name = "tipContrato", required = false) String tipContrato,
        @RequestParam(name = "proContrato", required = false) String proContrato,
        @RequestParam(name = "criContrato", required = false) String criContrato,
        @RequestParam(name = "TipoRelacion", required = false) String tipoRelacion,
        @RequestParam(name = "AnnoRelacion", required = false) String annoRelacion,
        @RequestParam(name = "OrdenRelacion", required = false) String ordenRelacion,
        @RequestParam(name = "solosaldo", required = false) String solosaldo,
        @RequestParam(name = "nlinea", required = false) String nlinea,
        @RequestParam(name = "indice", required = false) String indice,
        @RequestParam(name = "numRegDev", required = false) Integer numRegDev,
        @RequestParam(name = "ExpedienteElectronico", required = false) String expedienteElectronico,
        @RequestParam(name = "desdetalle", required = false, defaultValue = "S") String desdetalle) {
        try {
            OperacionesService.SearchCriteria criteria = new OperacionesService.SearchCriteria.Builder()
                    .numeroOperDesde(numeroOperDesde)
                    .numeroOperHasta(numeroOperHasta)
                    .codigoOperacion(codigoOperacion)
                    .signo(signo)
                    .areaGestora(areaGestora)
                    .fase(fase)
                    .fechaOperDesde(fechaOperDesde)
                    .fechaOperHasta(fechaOperHasta)
                    .tercero(tercero)
                    .ascendente(ascendente)
                    .referencia(referencia)
                    .organica(organica)
                    .funcional(funcional)
                    .economica(economica)
                    .importeDesde(importeDesde)
                    .importeHasta(importeHasta)
                    .expediente(expediente)
                    .grupoApunte(grupoApunte)
                    .oficina(oficina)
                    .fechaArqueo(fechaArqueo)
                    .ordinal(ordinal)
                    .codterr(codterr)
                    .pActMun(pActMun)
                    .ejeapli(ejeapli)
                    .tipContrato(tipContrato)
                    .proContrato(proContrato)
                    .criContrato(criContrato)
                    .tipoRelacion(tipoRelacion)
                    .annoRelacion(annoRelacion)
                    .ordenRelacion(ordenRelacion)
                    .solosaldo(solosaldo)
                    .nlinea(nlinea)
                    .indice(indice)
                    .numRegDev(numRegDev)
                    .expedienteElectronico(expedienteElectronico)
                    .desdetalle(desdetalle)
                    .build();
            List<Operaciones> operaciones = operacionesService.getOperaciones(criteria);
            return ResponseEntity.ok(operaciones);
        } catch (com.example.backend.exception.SmlProcessingException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "SML processing error: " + ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", ex.getMessage()));
        }
    }
}