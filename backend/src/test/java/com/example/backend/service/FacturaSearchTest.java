package com.example.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.backend.dto.FacWithTerProjection;
import com.example.backend.sqlserver2.repository.FacRepository;

@ExtendWith(MockitoExtension.class)
public class FacturaSearchTest {

    @Mock
    private FacRepository facRepository;

    @InjectMocks
    private FacturaSearch facturaSearch;

    static class FacProjectionMock implements FacWithTerProjection {
        Integer ent, facnum, tercod, facann, facfac, facoct;
        String eje, cgecod, facobs, factdc, facdoc, facado, factxt, conctp, concpr, conccr, facfpg, facopg, factpg;
        Double facimp, faciec, facidi, facdto;
        LocalDateTime facdat, facfco, facfre;
        String terNom, terNif;

        public FacProjectionMock(Integer third, String tnif, Double fimp, Double fiec, Double fidi, 
                                String fado, LocalDateTime ffre, LocalDateTime fdat, LocalDateTime ffco,
                                Integer fann, String tnom) {
            this.tercod = third;
            this.terNif = tnif;
            this.facimp = fimp;
            this.faciec = fiec;
            this.facidi = fidi;
            this.facado = fado;
            this.facfre = ffre;
            this.facdat = fdat;
            this.facfco = ffco;
            this.facann = fann;
            this.terNom = tnom;
        }

        @Override public Integer getENT() { return ent; }
        @Override public String getEJE() { return eje; }
        @Override public Integer getFACNUM() { return facnum; }
        @Override public Integer getTERCOD() { return tercod; }
        @Override public String getCGECOD() { return cgecod; }
        @Override public String getFACOBS() { return facobs; }
        @Override public Double getFACIMP() { return facimp; }
        @Override public Double getFACIEC() { return faciec; }
        @Override public Double getFACIDI() { return facidi; }
        @Override public String getFACTDC() { return factdc; }
        @Override public Integer getFACANN() { return facann; }
        @Override public Integer getFACFAC() { return facfac; }
        @Override public String getFACDOC() { return facdoc; }
        @Override public LocalDateTime getFACDAT() { return facdat; }
        @Override public LocalDateTime getFACFCO() { return facfco; }
        @Override public String getFACADO() { return facado; }
        @Override public String getFACTXT() { return factxt; }
        @Override public LocalDateTime getFACFRE() { return facfre; }
        @Override public String getCONCTP() { return conctp; }
        @Override public String getCONCPR() { return concpr; }
        @Override public String getCONCCR() { return conccr; }
        @Override public Integer getFACOCT() { return facoct; }
        @Override public String getFACFPG() { return facfpg; }
        @Override public String getFACOPG() { return facopg; }
        @Override public String getFACTPG() { return factpg; }
        @Override public Double getFACDTO() { return facdto; }
        @Override public String getTer_TERNOM() { return terNom; }
        @Override public String getTer_TERNIF() { return terNif; }
    }

    @BeforeEach
    void setUp() {
    }


    @Test
    void searchFactura_filtersBy_TercodWhenNumbersOnly_LessThanOrEqualTo5() {
        FacProjectionMock proj1 = new FacProjectionMock(100, "NIF001", 100.0, 50.0, 50.0, null, 
            LocalDateTime.now(), null, null, 2024, "Supplier A");
        FacProjectionMock proj2 = new FacProjectionMock(200, "NIF002", 200.0, 100.0, 100.0, null,
            LocalDateTime.now(), null, null, 2024, "Supplier B");

        when(facRepository.findByENTAndEJEAndCGECODOrderByFACFREAsc(1, "E1", "C1"))
            .thenReturn(List.of(proj1, proj2));

        List<FacWithTerProjection> result = facturaSearch.searchFactura(1, "E1", "C1", "100", null, null, null, null, null);

        assertEquals(1, result.size());
        assertEquals(100, result.get(0).getTERCOD());
    }

    @Test
    void searchFactura_filtersBy_TernifWhenNumbersOnly_GreaterThan5() {
        FacProjectionMock proj1 = new FacProjectionMock(100, "123456", 100.0, 50.0, 50.0, null,
            LocalDateTime.now(), null, null, 2024, "Supplier A");
        FacProjectionMock proj2 = new FacProjectionMock(200, "789012", 200.0, 100.0, 100.0, null,
            LocalDateTime.now(), null, null, 2024, "Supplier B");

        when(facRepository.findByENTAndEJEAndCGECODOrderByFACFREAsc(1, "E1", "C1"))
            .thenReturn(List.of(proj1, proj2));

        List<FacWithTerProjection> result = facturaSearch.searchFactura(1, "E1", "C1", "123456", null, null, null, null, null);

        assertEquals(1, result.size());
        assertEquals("123456", result.get(0).getTer_TERNIF());
    }

    @Test
    void searchFactura_filtersBy_TernifOrTernomOrFacdocWhenMixed() {
        FacProjectionMock proj1 = new FacProjectionMock(100, "ABC123", 100.0, 50.0, 50.0, null,
            LocalDateTime.now(), null, null, 2024, "Acme Corp");
        FacProjectionMock proj2 = new FacProjectionMock(200, "XYZ789", 200.0, 100.0, 100.0, null,
            LocalDateTime.now(), null, null, 2024, "Other Corp");

        proj1.facdoc = "FAC-2024-ABC";
        proj2.facdoc = "FAC-2024-XYZ";

        when(facRepository.findByENTAndEJEAndCGECODOrderByFACFREAsc(1, "E1", "C1"))
            .thenReturn(List.of(proj1, proj2));

        List<FacWithTerProjection> result = facturaSearch.searchFactura(1, "E1", "C1", "ABC", null, null, null, null, null);

        assertEquals(1, result.size());
        assertEquals("ABC123", result.get(0).getTer_TERNIF());
    }

    @Test
    void searchFactura_returnsAll_WhenMainFilterIsNull() {
        FacProjectionMock proj1 = new FacProjectionMock(100, "NIF001", 100.0, 50.0, 50.0, null,
            LocalDateTime.now(), null, null, 2024, "Supplier A");
        FacProjectionMock proj2 = new FacProjectionMock(200, "NIF002", 200.0, 100.0, 100.0, null,
            LocalDateTime.now(), null, null, 2024, "Supplier B");

        when(facRepository.findByENTAndEJEAndCGECODOrderByFACFREAsc(1, "E1", "C1"))
            .thenReturn(List.of(proj1, proj2));

        List<FacWithTerProjection> result = facturaSearch.searchFactura(1, "E1", "C1", null, null, null, null, null, null);

        assertEquals(2, result.size());
    }

    @Test
    void searchFactura_returnsAll_WhenMainFilterIsEmpty() {
        FacProjectionMock proj1 = new FacProjectionMock(100, "NIF001", 100.0, 50.0, 50.0, null,
            LocalDateTime.now(), null, null, 2024, "Supplier A");
        FacProjectionMock proj2 = new FacProjectionMock(200, "NIF002", 200.0, 100.0, 100.0, null,
            LocalDateTime.now(), null, null, 2024, "Supplier B");

        when(facRepository.findByENTAndEJEAndCGECODOrderByFACFREAsc(1, "E1", "C1"))
            .thenReturn(List.of(proj1, proj2));

        List<FacWithTerProjection> result = facturaSearch.searchFactura(1, "E1", "C1", "", null, null, null, null, null);

        assertEquals(2, result.size());
    }

    @Test
    void searchFactura_filtersBy_EstadoContabilizadas_WhenFacadoNotNull() {
        FacProjectionMock proj1 = new FacProjectionMock(100, "NIF001", 100.0, 50.0, 50.0, "2024-01-15",
            LocalDateTime.now(), null, null, 2024, "Supplier A");
        FacProjectionMock proj2 = new FacProjectionMock(200, "NIF002", 200.0, 100.0, 100.0, null,
            LocalDateTime.now(), null, null, 2024, "Supplier B");

        when(facRepository.findByENTAndEJEAndCGECODOrderByFACFREAsc(1, "E1", "C1"))
            .thenReturn(List.of(proj1, proj2));

        List<FacWithTerProjection> result = facturaSearch.searchFactura(1, "E1", "C1", null, null, "contabilizadas", null, null, null);

        assertEquals(1, result.size());
        assertEquals("2024-01-15", result.get(0).getFACADO());
    }

    @Test
    void searchFactura_filtersBy_EstadoNoContabilizadas_WhenFacadoNull() {
        FacProjectionMock proj1 = new FacProjectionMock(100, "NIF001", 100.0, 50.0, 50.0, "2024-01-15",
            LocalDateTime.now(), null, null, 2024, "Supplier A");
        FacProjectionMock proj2 = new FacProjectionMock(200, "NIF002", 200.0, 100.0, 100.0, null,
            LocalDateTime.now(), null, null, 2024, "Supplier B");

        when(facRepository.findByENTAndEJEAndCGECODOrderByFACFREAsc(1, "E1", "C1"))
            .thenReturn(List.of(proj1, proj2));

        List<FacWithTerProjection> result = facturaSearch.searchFactura(1, "E1", "C1", null, null, "noContabilizadas", null, null, null);

        assertEquals(1, result.size());
        assertNull(result.get(0).getFACADO());
    }

    @Test
    void searchFactura_filtersBy_EstadoPtApplidas_WhenFacadoNullAndEqualsApply() {
        FacProjectionMock proj1 = new FacProjectionMock(100, "NIF001", 100.0, 50.0, 50.0, null,
            LocalDateTime.now(), null, null, 2024, "Supplier A");
        FacProjectionMock proj2 = new FacProjectionMock(200, "NIF002", 200.0, 150.0, 100.0, null,
            LocalDateTime.now(), null, null, 2024, "Supplier B");

        when(facRepository.findByENTAndEJEAndCGECODOrderByFACFREAsc(1, "E1", "C1"))
            .thenReturn(List.of(proj1, proj2));

        List<FacWithTerProjection> result = facturaSearch.searchFactura(1, "E1", "C1", null, null, "ptApplidas", null, null, null);

        assertEquals(1, result.size());
        assertEquals(100.0, result.get(0).getFACIMP());
    }

    @Test
    void searchFactura_filtersBy_EstadoSinPtApplicar_WhenFacadoNullAndNotEquals() {
        FacProjectionMock proj1 = new FacProjectionMock(100, "NIF001", 100.0, 50.0, 50.0, null,
            LocalDateTime.now(), null, null, 2024, "Supplier A");
        FacProjectionMock proj2 = new FacProjectionMock(200, "NIF002", 200.0, 50.0, 50.0, null,
            LocalDateTime.now(), null, null, 2024, "Supplier B");

        when(facRepository.findByENTAndEJEAndCGECODOrderByFACFREAsc(1, "E1", "C1"))
            .thenReturn(List.of(proj1, proj2));

        List<FacWithTerProjection> result = facturaSearch.searchFactura(1, "E1", "C1", null, null, "sinPtApplicar", null, null, null);

        assertEquals(1, result.size());
        assertEquals(200.0, result.get(0).getFACIMP());
    }

    @Test
    void searchFactura_filtersBy_EjFacturaYear() {
        FacProjectionMock proj1 = new FacProjectionMock(100, "NIF001", 100.0, 50.0, 50.0, null,
            LocalDateTime.now(), null, null, 2024, "Supplier A");
        FacProjectionMock proj2 = new FacProjectionMock(200, "NIF002", 200.0, 100.0, 100.0, null,
            LocalDateTime.now(), null, null, 2025, "Supplier B");

        when(facRepository.findByENTAndEJEAndCGECODOrderByFACFREAsc(1, "E1", "C1"))
            .thenReturn(List.of(proj1, proj2));

        List<FacWithTerProjection> result = facturaSearch.searchFactura(1, "E1", "C1", null, 2024, null, null, null, null);

        assertEquals(1, result.size());
        assertEquals(2024, result.get(0).getFACANN());
    }

    @Test
    void searchFactura_returnsEmptyList_WhenEjFacturaNotMatched() {
        FacProjectionMock proj1 = new FacProjectionMock(100, "NIF001", 100.0, 50.0, 50.0, null,
            LocalDateTime.now(), null, null, 2024, "Supplier A");

        when(facRepository.findByENTAndEJEAndCGECODOrderByFACFREAsc(1, "E1", "C1"))
            .thenReturn(List.of(proj1));

        List<FacWithTerProjection> result = facturaSearch.searchFactura(1, "E1", "C1", null, 2025, null, null, null, null);

        assertEquals(0, result.size());
    }

    @Test
    void searchFactura_filtersBy_FechaRegistroBetween() {
        LocalDateTime date1 = LocalDateTime.of(2026, 1, 15, 10, 0);
        LocalDateTime date2 = LocalDateTime.of(2026, 2, 10, 10, 0);
        LocalDateTime date3 = LocalDateTime.of(2026, 4, 10, 10, 0);

        FacProjectionMock proj1 = new FacProjectionMock(100, "NIF001", 100.0, 50.0, 50.0, null, date1, null, null, 2024, "Supplier A");
        FacProjectionMock proj2 = new FacProjectionMock(200, "NIF002", 200.0, 100.0, 100.0, null, date2, null, null, 2024, "Supplier B");
        FacProjectionMock proj3 = new FacProjectionMock(300, "NIF003", 300.0, 150.0, 150.0, null, date3, null, null, 2024, "Supplier C");

        when(facRepository.findByENTAndEJEAndCGECODOrderByFACFREAsc(1, "E1", "C1"))
            .thenReturn(List.of(proj1, proj2, proj3));

        List<FacWithTerProjection> result = facturaSearch.searchFactura(1, "E1", "C1", null, null, null, "registro",
            LocalDate.of(2026, 1, 1), LocalDate.of(2026, 3, 31));

        assertEquals(2, result.size());
    }

    @Test
    void searchFactura_filtersBy_FechaRegistroFrom() {
        LocalDateTime date1 = LocalDateTime.of(2026, 1, 15, 10, 0);
        LocalDateTime date2 = LocalDateTime.of(2026, 4, 10, 10, 0);

        FacProjectionMock proj1 = new FacProjectionMock(100, "NIF001", 100.0, 50.0, 50.0, null, date1, null, null, 2024, "Supplier A");
        FacProjectionMock proj2 = new FacProjectionMock(200, "NIF002", 200.0, 100.0, 100.0, null, date2, null, null, 2024, "Supplier B");

        when(facRepository.findByENTAndEJEAndCGECODOrderByFACFREAsc(1, "E1", "C1"))
            .thenReturn(List.of(proj1, proj2));

        List<FacWithTerProjection> result = facturaSearch.searchFactura(1, "E1", "C1", null, null, null, "registro",
            LocalDate.of(2026, 3, 1), null);

        assertEquals(1, result.size());
    }

    @Test
    void searchFactura_filtersBy_FechaRegistroTo() {
        LocalDateTime date1 = LocalDateTime.of(2026, 1, 15, 10, 0);
        LocalDateTime date2 = LocalDateTime.of(2026, 4, 10, 10, 0);

        FacProjectionMock proj1 = new FacProjectionMock(100, "NIF001", 100.0, 50.0, 50.0, null, date1, null, null, 2024, "Supplier A");
        FacProjectionMock proj2 = new FacProjectionMock(200, "NIF002", 200.0, 100.0, 100.0, null, date2, null, null, 2024, "Supplier B");

        when(facRepository.findByENTAndEJEAndCGECODOrderByFACFREAsc(1, "E1", "C1"))
            .thenReturn(List.of(proj1, proj2));

        List<FacWithTerProjection> result = facturaSearch.searchFactura(1, "E1", "C1", null, null, null, "registro",
            null, LocalDate.of(2026, 3, 1));

        assertEquals(1, result.size());
    }

    @Test
    void searchFactura_filtersBy_FechaFacturaBetween() {
        LocalDateTime date1 = LocalDateTime.of(2026, 1, 15, 10, 0);
        LocalDateTime date2 = LocalDateTime.of(2026, 2, 10, 10, 0);
        LocalDateTime date3 = LocalDateTime.of(2026, 4, 10, 10, 0);

        FacProjectionMock proj1 = new FacProjectionMock(100, "NIF001", 100.0, 50.0, 50.0, null, null, date1, null, 2024, "Supplier A");
        FacProjectionMock proj2 = new FacProjectionMock(200, "NIF002", 200.0, 100.0, 100.0, null, null, date2, null, 2024, "Supplier B");
        FacProjectionMock proj3 = new FacProjectionMock(300, "NIF003", 300.0, 150.0, 150.0, null, null, date3, null, 2024, "Supplier C");

        when(facRepository.findByENTAndEJEAndCGECODOrderByFACFREAsc(1, "E1", "C1"))
            .thenReturn(List.of(proj1, proj2, proj3));

        List<FacWithTerProjection> result = facturaSearch.searchFactura(1, "E1", "C1", null, null, null, "factura",
            LocalDate.of(2026, 1, 1), LocalDate.of(2026, 3, 31));

        assertEquals(2, result.size());
    }

    @Test
    void searchFactura_filtersBy_FechaFacturaFrom() {
        LocalDateTime date1 = LocalDateTime.of(2026, 1, 15, 10, 0);
        LocalDateTime date2 = LocalDateTime.of(2026, 4, 10, 10, 0);

        FacProjectionMock proj1 = new FacProjectionMock(100, "NIF001", 100.0, 50.0, 50.0, null, null, date1, null, 2024, "Supplier A");
        FacProjectionMock proj2 = new FacProjectionMock(200, "NIF002", 200.0, 100.0, 100.0, null, null, date2, null, 2024, "Supplier B");

        when(facRepository.findByENTAndEJEAndCGECODOrderByFACFREAsc(1, "E1", "C1"))
            .thenReturn(List.of(proj1, proj2));

        List<FacWithTerProjection> result = facturaSearch.searchFactura(1, "E1", "C1", null, null, null, "factura",
            LocalDate.of(2026, 3, 1), null);

        assertEquals(1, result.size());
    }

    @Test
    void searchFactura_filtersBy_FechaFacturaTo() {
        LocalDateTime date1 = LocalDateTime.of(2026, 1, 15, 10, 0);
        LocalDateTime date2 = LocalDateTime.of(2026, 4, 10, 10, 0);

        FacProjectionMock proj1 = new FacProjectionMock(100, "NIF001", 100.0, 50.0, 50.0, null, null, date1, null, 2024, "Supplier A");
        FacProjectionMock proj2 = new FacProjectionMock(200, "NIF002", 200.0, 100.0, 100.0, null, null, date2, null, 2024, "Supplier B");

        when(facRepository.findByENTAndEJEAndCGECODOrderByFACFREAsc(1, "E1", "C1"))
            .thenReturn(List.of(proj1, proj2));

        List<FacWithTerProjection> result = facturaSearch.searchFactura(1, "E1", "C1", null, null, null, "factura",
            null, LocalDate.of(2026, 3, 1));

        assertEquals(1, result.size());
    }

    @Test
    void searchFactura_filtersBy_FechaContableBetween() {
        LocalDateTime date1 = LocalDateTime.of(2026, 1, 15, 10, 0);
        LocalDateTime date2 = LocalDateTime.of(2026, 2, 10, 10, 0);
        LocalDateTime date3 = LocalDateTime.of(2026, 4, 10, 10, 0);

        FacProjectionMock proj1 = new FacProjectionMock(100, "NIF001", 100.0, 50.0, 50.0, null, null, null, date1, 2024, "Supplier A");
        FacProjectionMock proj2 = new FacProjectionMock(200, "NIF002", 200.0, 100.0, 100.0, null, null, null, date2, 2024, "Supplier B");
        FacProjectionMock proj3 = new FacProjectionMock(300, "NIF003", 300.0, 150.0, 150.0, null, null, null, date3, 2024, "Supplier C");

        when(facRepository.findByENTAndEJEAndCGECODOrderByFACFREAsc(1, "E1", "C1"))
            .thenReturn(List.of(proj1, proj2, proj3));

        List<FacWithTerProjection> result = facturaSearch.searchFactura(1, "E1", "C1", null, null, null, "contable",
            LocalDate.of(2026, 1, 1), LocalDate.of(2026, 3, 31));

        assertEquals(2, result.size());
    }

    @Test
    void searchFactura_filtersBy_FechaContableFrom() {
        LocalDateTime date1 = LocalDateTime.of(2026, 1, 15, 10, 0);
        LocalDateTime date2 = LocalDateTime.of(2026, 4, 10, 10, 0);

        FacProjectionMock proj1 = new FacProjectionMock(100, "NIF001", 100.0, 50.0, 50.0, null, null, null, date1, 2024, "Supplier A");
        FacProjectionMock proj2 = new FacProjectionMock(200, "NIF002", 200.0, 100.0, 100.0, null, null, null, date2, 2024, "Supplier B");

        when(facRepository.findByENTAndEJEAndCGECODOrderByFACFREAsc(1, "E1", "C1"))
            .thenReturn(List.of(proj1, proj2));

        List<FacWithTerProjection> result = facturaSearch.searchFactura(1, "E1", "C1", null, null, null, "contable",
            LocalDate.of(2026, 3, 1), null);

        assertEquals(1, result.size());
    }

    @Test
    void searchFactura_filtersBy_FechaContableTo() {
        LocalDateTime date1 = LocalDateTime.of(2026, 1, 15, 10, 0);
        LocalDateTime date2 = LocalDateTime.of(2026, 4, 10, 10, 0);

        FacProjectionMock proj1 = new FacProjectionMock(100, "NIF001", 100.0, 50.0, 50.0, null, null, null, date1, 2024, "Supplier A");
        FacProjectionMock proj2 = new FacProjectionMock(200, "NIF002", 200.0, 100.0, 100.0, null, null, null, date2, 2024, "Supplier B");

        when(facRepository.findByENTAndEJEAndCGECODOrderByFACFREAsc(1, "E1", "C1"))
            .thenReturn(List.of(proj1, proj2));

        List<FacWithTerProjection> result = facturaSearch.searchFactura(1, "E1", "C1", null, null, null, "contable",
            null, LocalDate.of(2026, 3, 1));

        assertEquals(1, result.size());
    }

    @Test
    void searchFactura_combinedFilters_MainFilterAndEjFacturaAndEstado() {
        FacProjectionMock proj1 = new FacProjectionMock(100, "NIF001", 100.0, 50.0, 50.0, "2024-01-15",
            LocalDateTime.now(), null, null, 2024, "Supplier A");
        FacProjectionMock proj2 = new FacProjectionMock(200, "NIF002", 200.0, 100.0, 100.0, null,
            LocalDateTime.now(), null, null, 2024, "Supplier B");
        FacProjectionMock proj3 = new FacProjectionMock(100, "NIF001", 150.0, 75.0, 75.0, null,
            LocalDateTime.now(), null, null, 2025, "Supplier A");

        when(facRepository.findByENTAndEJEAndCGECODOrderByFACFREAsc(1, "E1", "C1"))
            .thenReturn(List.of(proj1, proj2, proj3));

        List<FacWithTerProjection> result = facturaSearch.searchFactura(1, "E1", "C1", "100", 2024, "contabilizadas", null, null, null);

        assertEquals(1, result.size());
        assertEquals(100, result.get(0).getTERCOD());
        assertEquals(2024, result.get(0).getFACANN());
        assertEquals("2024-01-15", result.get(0).getFACADO());
    }

    @Test
    void searchFactura_combinedFilters_AllParameters() {
        LocalDateTime date1 = LocalDateTime.of(2026, 1, 15, 10, 0);
        FacProjectionMock proj1 = new FacProjectionMock(100, "NIF001", 100.0, 50.0, 50.0, "2024-01-15",
            date1, LocalDateTime.of(2026, 1, 20, 10, 0), LocalDateTime.of(2026, 1, 25, 10, 0), 2024, "Supplier A");

        when(facRepository.findByENTAndEJEAndCGECODOrderByFACFREAsc(1, "E1", "C1"))
            .thenReturn(List.of(proj1));

        List<FacWithTerProjection> result = facturaSearch.searchFactura(
            1, "E1", "C1", "100", 2024, "contabilizadas", "registro",
            LocalDate.of(2026, 1, 1), LocalDate.of(2026, 2, 1));

        assertEquals(1, result.size());
    }

    @Test
    void searchFactura_returnsEmptyList_WhenRepositoryReturnsEmpty() {
        when(facRepository.findByENTAndEJEAndCGECODOrderByFACFREAsc(1, "E1", "C1"))
            .thenReturn(List.of());

        List<FacWithTerProjection> result = facturaSearch.searchFactura(1, "E1", "C1", null, null, null, null, null, null);

        assertEquals(0, result.size());
    }

    @Test
    void searchFactura_returnsEmptyList_WhenRepositoryReturnsNull() {
        when(facRepository.findByENTAndEJEAndCGECODOrderByFACFREAsc(1, "E1", "C1"))
            .thenReturn(null);

        List<FacWithTerProjection> result = facturaSearch.searchFactura(1, "E1", "C1", null, null, null, null, null, null);

        assertNull(result);
    }

    @Test
    void searchFactura_filtersIgnore_WhenEstadoIsEmpty() {
        FacProjectionMock proj1 = new FacProjectionMock(100, "NIF001", 100.0, 50.0, 50.0, "2024-01-15",
            LocalDateTime.now(), null, null, 2024, "Supplier A");
        FacProjectionMock proj2 = new FacProjectionMock(200, "NIF002", 200.0, 100.0, 100.0, null,
            LocalDateTime.now(), null, null, 2024, "Supplier B");

        when(facRepository.findByENTAndEJEAndCGECODOrderByFACFREAsc(1, "E1", "C1"))
            .thenReturn(List.of(proj1, proj2));

        List<FacWithTerProjection> result = facturaSearch.searchFactura(1, "E1", "C1", null, null, "", null, null, null);

        assertEquals(2, result.size());
    }

    @Test
    void searchFactura_filtersIgnore_WhenFechaIsEmpty() {
        FacProjectionMock proj1 = new FacProjectionMock(100, "NIF001", 100.0, 50.0, 50.0, null,
            LocalDateTime.now(), LocalDateTime.now(), null, 2024, "Supplier A");

        when(facRepository.findByENTAndEJEAndCGECODOrderByFACFREAsc(1, "E1", "C1"))
            .thenReturn(List.of(proj1));

        List<FacWithTerProjection> result = facturaSearch.searchFactura(1, "E1", "C1", null, null, null, "", 
            LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31));

        assertEquals(1, result.size());
    }

    @Test
    void searchFactura_noFiltersApplied_WhenAllParametersNull() {
        FacProjectionMock proj1 = new FacProjectionMock(100, "NIF001", 100.0, 50.0, 50.0, "2024-01-15",
            LocalDateTime.now(), null, null, 2024, "Supplier A");
        FacProjectionMock proj2 = new FacProjectionMock(200, "NIF002", 200.0, 100.0, 100.0, null,
            LocalDateTime.now(), null, null, 2024, "Supplier B");

        when(facRepository.findByENTAndEJEAndCGECODOrderByFACFREAsc(1, "E1", "C1"))
            .thenReturn(List.of(proj1, proj2));

        List<FacWithTerProjection> result = facturaSearch.searchFactura(1, "E1", "C1", null, null, null, null, null, null);

        assertEquals(2, result.size());
        verify(facRepository).findByENTAndEJEAndCGECODOrderByFACFREAsc(1, "E1", "C1");
    }

    @Test
    void searchFactura_handlesNullTercodGracefully() {
        FacProjectionMock proj1 = new FacProjectionMock(null, "NIF001", 100.0, 50.0, 50.0, null,
            LocalDateTime.now(), null, null, 2024, "Supplier A");

        when(facRepository.findByENTAndEJEAndCGECODOrderByFACFREAsc(1, "E1", "C1"))
            .thenReturn(List.of(proj1));

        List<FacWithTerProjection> result = facturaSearch.searchFactura(1, "E1", "C1", "100", null, null, null, null, null);

        assertEquals(0, result.size());
    }

    @Test
    void searchFactura_handlesNullTernifGracefully() {
        FacProjectionMock proj1 = new FacProjectionMock(100, null, 100.0, 50.0, 50.0, null,
            LocalDateTime.now(), null, null, 2024, "Supplier A");

        when(facRepository.findByENTAndEJEAndCGECODOrderByFACFREAsc(1, "E1", "C1"))
            .thenReturn(List.of(proj1));

        List<FacWithTerProjection> result = facturaSearch.searchFactura(1, "E1", "C1", "ABC123", null, null, null, null, null);

        assertEquals(0, result.size());
    }

    @Test
    void searchFactura_handlesNullDateFieldsGracefully() {
        FacProjectionMock proj1 = new FacProjectionMock(100, "NIF001", 100.0, 50.0, 50.0, null,
            null, null, null, 2024, "Supplier A");

        when(facRepository.findByENTAndEJEAndCGECODOrderByFACFREAsc(1, "E1", "C1"))
            .thenReturn(List.of(proj1));

        List<FacWithTerProjection> result = facturaSearch.searchFactura(1, "E1", "C1", null, null, null, "registro",
            LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31));

        assertEquals(0, result.size());
    }

    @Test
    void searchFactura_returnsMultipleResults_WhenMultipleMatch() {
        FacProjectionMock proj1 = new FacProjectionMock(100, "NIF001", 100.0, 50.0, 50.0, null,
            LocalDateTime.now(), null, null, 2024, "Supplier A");
        FacProjectionMock proj2 = new FacProjectionMock(100, "NIF002", 200.0, 100.0, 100.0, null,
            LocalDateTime.now(), null, null, 2024, "Supplier B");
        FacProjectionMock proj3 = new FacProjectionMock(100, "NIF003", 300.0, 150.0, 150.0, null,
            LocalDateTime.now(), null, null, 2024, "Supplier C");

        when(facRepository.findByENTAndEJEAndCGECODOrderByFACFREAsc(1, "E1", "C1"))
            .thenReturn(List.of(proj1, proj2, proj3));

        List<FacWithTerProjection> result = facturaSearch.searchFactura(1, "E1", "C1", "100", null, null, null, null, null);

        assertEquals(3, result.size());
    }

    @Test
    void searchFactura_isCaseInsensitive_WhenSearchingTernom() {
        FacProjectionMock proj1 = new FacProjectionMock(100, "NIF001", 100.0, 50.0, 50.0, null,
            LocalDateTime.now(), null, null, 2024, "ACME CORP");
        FacProjectionMock proj2 = new FacProjectionMock(200, "NIF002", 200.0, 100.0, 100.0, null,
            LocalDateTime.now(), null, null, 2024, "Other Corp");

        when(facRepository.findByENTAndEJEAndCGECODOrderByFACFREAsc(1, "E1", "C1"))
            .thenReturn(List.of(proj1, proj2));

        List<FacWithTerProjection> result = facturaSearch.searchFactura(1, "E1", "C1", "acme", null, null, null, null, null);

        assertEquals(1, result.size());
        assertEquals("ACME CORP", result.get(0).getTer_TERNOM());
    }
}