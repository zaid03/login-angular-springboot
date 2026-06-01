package com.example.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.backend.dto.ContratoDto;
import com.example.backend.sqlserver2.repository.CotRepository;
import com.example.backend.service.CotContratoProjection.ConnInfo;
import com.example.backend.service.CotContratoProjection.TerInfo;

@ExtendWith(MockitoExtension.class)
public class ContratosSearchTest {

    @Mock
    private CotRepository cotRepository;

    @InjectMocks
    private ContratosSearch contratosSearch;

    // ==================== Test searchContrtos with "todos" mode ====================
    
    @Test
    void testSearchContratos_TodosMode_NullTerm() {
        // Arrange
        Integer ent = 1;
        String eje = "2024";
        String searchMode = "todos";
        String term = null;
        
        ConnInfo mockConnInfo = mock(ConnInfo.class);
        TerInfo mockTerInfo = mock(TerInfo.class);
        CotContratoProjection mockProjection = mock(CotContratoProjection.class);
        
        when(mockProjection.getConn()).thenReturn(mockConnInfo);
        when(mockProjection.getTer()).thenReturn(mockTerInfo);
        
        List<CotContratoProjection> mockResults = new ArrayList<>();
        mockResults.add(mockProjection);
        
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJE(3, ent, eje))
            .thenReturn(mockResults);
        
        when(mockConnInfo.getCONCOD()).thenReturn(100);
        when(mockConnInfo.getCONLOT()).thenReturn("LOT001");
        when(mockConnInfo.getCONDES()).thenReturn("Description");
        when(mockConnInfo.getCONFIN()).thenReturn(LocalDateTime.of(2024, 1, 1, 0, 0, 0));
        when(mockConnInfo.getCONFFI()).thenReturn(LocalDateTime.of(2024, 12, 31, 0, 0, 0));
        when(mockConnInfo.getCONBLO()).thenReturn(0);
        
        when(mockTerInfo.getTERCOD()).thenReturn(5);
        when(mockTerInfo.getTERNOM()).thenReturn("Supplier");
        
        // Act
        List<ContratoDto> result = contratosSearch.searchContrtos(ent, eje, searchMode, term);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(100, result.get(0).getConcod());
        assertEquals("LOT001", result.get(0).getConlot());
        verify(cotRepository, times(1)).findAllProjectedByConnCONTIPAndConnENTAndConnEJE(3, ent, eje);
    }

    @Test
    void testSearchContratos_TodosMode_EmptyTerm() {
        // Arrange
        Integer ent = 1;
        String eje = "2024";
        String searchMode = "todos";
        String term = "";
        
        ConnInfo mockConnInfo = mock(ConnInfo.class);
        TerInfo mockTerInfo = mock(TerInfo.class);
        CotContratoProjection mockProjection = mock(CotContratoProjection.class);
        
        when(mockProjection.getConn()).thenReturn(mockConnInfo);
        when(mockProjection.getTer()).thenReturn(mockTerInfo);
        
        List<CotContratoProjection> mockResults = new ArrayList<>();
        mockResults.add(mockProjection);
        
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJE(3, ent, eje))
            .thenReturn(mockResults);
        
        when(mockConnInfo.getCONCOD()).thenReturn(200);
        when(mockConnInfo.getCONLOT()).thenReturn("LOT002");
        when(mockConnInfo.getCONDES()).thenReturn("Desc");
        when(mockConnInfo.getCONFIN()).thenReturn(LocalDateTime.of(2024, 1, 1, 0, 0, 0));
        when(mockConnInfo.getCONFFI()).thenReturn(LocalDateTime.of(2024, 12, 31, 0, 0, 0));
        when(mockConnInfo.getCONBLO()).thenReturn(0);
        
        when(mockTerInfo.getTERCOD()).thenReturn(10);
        when(mockTerInfo.getTERNOM()).thenReturn("Provider");
        
        // Act
        List<ContratoDto> result = contratosSearch.searchContrtos(ent, eje, searchMode, term);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(cotRepository, times(1)).findAllProjectedByConnCONTIPAndConnENTAndConnEJE(3, ent, eje);
    }

    @Test
    void testSearchContratos_TodosMode_NumericTerm() {
        // Arrange
        Integer ent = 1;
        String eje = "2024";
        String searchMode = "todos";
        String term = "12345";
        
        ConnInfo mockConnInfo = mock(ConnInfo.class);
        TerInfo mockTerInfo = mock(TerInfo.class);
        CotContratoProjection mockProjection = mock(CotContratoProjection.class);
        
        when(mockProjection.getConn()).thenReturn(mockConnInfo);
        when(mockProjection.getTer()).thenReturn(mockTerInfo);
        
        List<CotContratoProjection> mockResults = new ArrayList<>();
        mockResults.add(mockProjection);
        
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONCOD(3, ent, eje, 12345))
            .thenReturn(mockResults);
        
        when(mockConnInfo.getCONCOD()).thenReturn(12345);
        when(mockConnInfo.getCONLOT()).thenReturn("LOT003");
        when(mockConnInfo.getCONDES()).thenReturn("Numeric search");
        when(mockConnInfo.getCONFIN()).thenReturn(LocalDateTime.of(2024, 1, 1, 0, 0, 0));
        when(mockConnInfo.getCONFFI()).thenReturn(LocalDateTime.of(2024, 12, 31, 0, 0, 0));
        when(mockConnInfo.getCONBLO()).thenReturn(0);
        
        when(mockTerInfo.getTERCOD()).thenReturn(15);
        when(mockTerInfo.getTERNOM()).thenReturn("Vendor");
        
        // Act
        List<ContratoDto> result = contratosSearch.searchContrtos(ent, eje, searchMode, term);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(12345, result.get(0).getConcod());
        verify(cotRepository, times(1)).findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONCOD(3, ent, eje, 12345);
    }

    @Test
    void testSearchContratos_TodosMode_TextTerm() {
        // Arrange
        Integer ent = 1;
        String eje = "2024";
        String searchMode = "todos";
        String term = "SearchText";
        
        ConnInfo mockConnInfo = mock(ConnInfo.class);
        TerInfo mockTerInfo = mock(TerInfo.class);
        CotContratoProjection mockProjection = mock(CotContratoProjection.class);
        
        when(mockProjection.getConn()).thenReturn(mockConnInfo);
        when(mockProjection.getTer()).thenReturn(mockTerInfo);
        
        List<CotContratoProjection> mockResults = new ArrayList<>();
        mockResults.add(mockProjection);
        
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONDES(3, ent, eje, term))
            .thenReturn(mockResults);
        
        when(mockConnInfo.getCONCOD()).thenReturn(300);
        when(mockConnInfo.getCONLOT()).thenReturn("LOT004");
        when(mockConnInfo.getCONDES()).thenReturn("SearchText");
        when(mockConnInfo.getCONFIN()).thenReturn(LocalDateTime.of(2024, 1, 1, 0, 0, 0));
        when(mockConnInfo.getCONFFI()).thenReturn(LocalDateTime.of(2024, 12, 31, 0, 0, 0));
        when(mockConnInfo.getCONBLO()).thenReturn(0);
        
        when(mockTerInfo.getTERCOD()).thenReturn(20);
        when(mockTerInfo.getTERNOM()).thenReturn("Company");
        
        // Act
        List<ContratoDto> result = contratosSearch.searchContrtos(ent, eje, searchMode, term);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("SearchText", result.get(0).getCondes());
        verify(cotRepository, times(1)).findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONDES(3, ent, eje, term);
    }

    // ==================== Test searchContrtos with "noBloque" mode ====================
    
    @Test
    void testSearchContratos_NoBloqueMode_NullTerm() {
        // Arrange
        Integer ent = 1;
        String eje = "2024";
        String searchMode = "noBloque";
        String term = null;
        
        ConnInfo mockConnInfo = mock(ConnInfo.class);
        TerInfo mockTerInfo = mock(TerInfo.class);
        CotContratoProjection mockProjection = mock(CotContratoProjection.class);
        
        when(mockProjection.getConn()).thenReturn(mockConnInfo);
        when(mockProjection.getTer()).thenReturn(mockTerInfo);
        
        List<CotContratoProjection> mockResults = new ArrayList<>();
        mockResults.add(mockProjection);
        
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONBLONot(3, ent, eje, 0))
            .thenReturn(mockResults);
        
        when(mockConnInfo.getCONCOD()).thenReturn(400);
        when(mockConnInfo.getCONLOT()).thenReturn("LOT005");
        when(mockConnInfo.getCONDES()).thenReturn("NoBloque Null");
        when(mockConnInfo.getCONFIN()).thenReturn(LocalDateTime.of(2024, 1, 1, 0, 0, 0));
        when(mockConnInfo.getCONFFI()).thenReturn(LocalDateTime.of(2024, 12, 31, 0, 0, 0));
        when(mockConnInfo.getCONBLO()).thenReturn(0);
        
        when(mockTerInfo.getTERCOD()).thenReturn(25);
        when(mockTerInfo.getTERNOM()).thenReturn("Supplier1");
        
        // Act
        List<ContratoDto> result = contratosSearch.searchContrtos(ent, eje, searchMode, term);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(cotRepository, times(1)).findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONBLONot(3, ent, eje, 0);
    }

    @Test
    void testSearchContratos_NoBloqueMode_EmptyTerm() {
        // Arrange
        Integer ent = 1;
        String eje = "2024";
        String searchMode = "noBloque";
        String term = "";
        
        ConnInfo mockConnInfo = mock(ConnInfo.class);
        TerInfo mockTerInfo = mock(TerInfo.class);
        CotContratoProjection mockProjection = mock(CotContratoProjection.class);
        
        when(mockProjection.getConn()).thenReturn(mockConnInfo);
        when(mockProjection.getTer()).thenReturn(mockTerInfo);
        
        List<CotContratoProjection> mockResults = new ArrayList<>();
        mockResults.add(mockProjection);
        
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONBLONot(3, ent, eje, 0))
            .thenReturn(mockResults);
        
        when(mockConnInfo.getCONCOD()).thenReturn(500);
        when(mockConnInfo.getCONLOT()).thenReturn("LOT006");
        when(mockConnInfo.getCONDES()).thenReturn("NoBloque Empty");
        when(mockConnInfo.getCONFIN()).thenReturn(LocalDateTime.of(2024, 1, 1, 0, 0, 0));
        when(mockConnInfo.getCONFFI()).thenReturn(LocalDateTime.of(2024, 12, 31, 0, 0, 0));
        when(mockConnInfo.getCONBLO()).thenReturn(0);
        
        when(mockTerInfo.getTERCOD()).thenReturn(30);
        when(mockTerInfo.getTERNOM()).thenReturn("Supplier2");
        
        // Act
        List<ContratoDto> result = contratosSearch.searchContrtos(ent, eje, searchMode, term);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(cotRepository, times(1)).findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONBLONot(3, ent, eje, 0);
    }

    @Test
    void testSearchContratos_NoBloqueMode_NumericTerm() {
        // Arrange
        Integer ent = 1;
        String eje = "2024";
        String searchMode = "noBloque";
        String term = "54321";
        
        ConnInfo mockConnInfo = mock(ConnInfo.class);
        TerInfo mockTerInfo = mock(TerInfo.class);
        CotContratoProjection mockProjection = mock(CotContratoProjection.class);
        
        when(mockProjection.getConn()).thenReturn(mockConnInfo);
        when(mockProjection.getTer()).thenReturn(mockTerInfo);
        
        List<CotContratoProjection> mockResults = new ArrayList<>();
        mockResults.add(mockProjection);
        
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONCODAndConnCONBLONot(3, ent, eje, 54321, 0))
            .thenReturn(mockResults);
        
        when(mockConnInfo.getCONCOD()).thenReturn(54321);
        when(mockConnInfo.getCONLOT()).thenReturn("LOT007");
        when(mockConnInfo.getCONDES()).thenReturn("NoBloque Numeric");
        when(mockConnInfo.getCONFIN()).thenReturn(LocalDateTime.of(2024, 1, 1, 0, 0, 0));
        when(mockConnInfo.getCONFFI()).thenReturn(LocalDateTime.of(2024, 12, 31, 0, 0, 0));
        when(mockConnInfo.getCONBLO()).thenReturn(0);
        
        when(mockTerInfo.getTERCOD()).thenReturn(35);
        when(mockTerInfo.getTERNOM()).thenReturn("Supplier3");
        
        // Act
        List<ContratoDto> result = contratosSearch.searchContrtos(ent, eje, searchMode, term);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(cotRepository, times(1)).findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONCODAndConnCONBLONot(3, ent, eje, 54321, 0);
    }

    @Test
    void testSearchContratos_NoBloqueMode_TextTerm() {
        // Arrange
        Integer ent = 1;
        String eje = "2024";
        String searchMode = "noBloque";
        String term = "TextSearch";
        
        ConnInfo mockConnInfo = mock(ConnInfo.class);
        TerInfo mockTerInfo = mock(TerInfo.class);
        CotContratoProjection mockProjection = mock(CotContratoProjection.class);
        
        when(mockProjection.getConn()).thenReturn(mockConnInfo);
        when(mockProjection.getTer()).thenReturn(mockTerInfo);
        
        List<CotContratoProjection> mockResults = new ArrayList<>();
        mockResults.add(mockProjection);
        
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONDESAndConnCONBLONot(3, ent, eje, term, 0))
            .thenReturn(mockResults);
        
        when(mockConnInfo.getCONCOD()).thenReturn(600);
        when(mockConnInfo.getCONLOT()).thenReturn("LOT008");
        when(mockConnInfo.getCONDES()).thenReturn("TextSearch");
        when(mockConnInfo.getCONFIN()).thenReturn(LocalDateTime.of(2024, 1, 1, 0, 0, 0));
        when(mockConnInfo.getCONFFI()).thenReturn(LocalDateTime.of(2024, 12, 31, 0, 0, 0));
        when(mockConnInfo.getCONBLO()).thenReturn(0);
        
        when(mockTerInfo.getTERCOD()).thenReturn(40);
        when(mockTerInfo.getTERNOM()).thenReturn("Supplier4");
        
        // Act
        List<ContratoDto> result = contratosSearch.searchContrtos(ent, eje, searchMode, term);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(cotRepository, times(1)).findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONDESAndConnCONBLONot(3, ent, eje, term, 0);
    }

    // ==================== Test searchContrtos with "bloque" mode ====================
    
    @Test
    void testSearchContratos_BloqueMode_NullTerm() {
        // Arrange
        Integer ent = 1;
        String eje = "2024";
        String searchMode = "bloque";
        String term = null;
        
        ConnInfo mockConnInfo = mock(ConnInfo.class);
        TerInfo mockTerInfo = mock(TerInfo.class);
        CotContratoProjection mockProjection = mock(CotContratoProjection.class);
        
        when(mockProjection.getConn()).thenReturn(mockConnInfo);
        when(mockProjection.getTer()).thenReturn(mockTerInfo);
        
        List<CotContratoProjection> mockResults = new ArrayList<>();
        mockResults.add(mockProjection);
        
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONBLO(3, ent, eje, 0))
            .thenReturn(mockResults);
        
        when(mockConnInfo.getCONCOD()).thenReturn(700);
        when(mockConnInfo.getCONLOT()).thenReturn("LOT009");
        when(mockConnInfo.getCONDES()).thenReturn("Bloque Null");
        when(mockConnInfo.getCONFIN()).thenReturn(LocalDateTime.of(2024, 1, 1, 0, 0, 0));
        when(mockConnInfo.getCONFFI()).thenReturn(LocalDateTime.of(2024, 12, 31, 0, 0, 0));
        when(mockConnInfo.getCONBLO()).thenReturn(0);
        
        when(mockTerInfo.getTERCOD()).thenReturn(45);
        when(mockTerInfo.getTERNOM()).thenReturn("Supplier5");
        
        // Act
        List<ContratoDto> result = contratosSearch.searchContrtos(ent, eje, searchMode, term);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(cotRepository, times(1)).findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONBLO(3, ent, eje, 0);
    }

    @Test
    void testSearchContratos_BloqueMode_EmptyTerm() {
        // Arrange
        Integer ent = 1;
        String eje = "2024";
        String searchMode = "bloque";
        String term = "";
        
        ConnInfo mockConnInfo = mock(ConnInfo.class);
        TerInfo mockTerInfo = mock(TerInfo.class);
        CotContratoProjection mockProjection = mock(CotContratoProjection.class);
        
        when(mockProjection.getConn()).thenReturn(mockConnInfo);
        when(mockProjection.getTer()).thenReturn(mockTerInfo);
        
        List<CotContratoProjection> mockResults = new ArrayList<>();
        mockResults.add(mockProjection);
        
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONBLO(3, ent, eje, 0))
            .thenReturn(mockResults);
        
        when(mockConnInfo.getCONCOD()).thenReturn(800);
        when(mockConnInfo.getCONLOT()).thenReturn("LOT010");
        when(mockConnInfo.getCONDES()).thenReturn("Bloque Empty");
        when(mockConnInfo.getCONFIN()).thenReturn(LocalDateTime.of(2024, 1, 1, 0, 0, 0));
        when(mockConnInfo.getCONFFI()).thenReturn(LocalDateTime.of(2024, 12, 31, 0, 0, 0));
        when(mockConnInfo.getCONBLO()).thenReturn(0);
        
        when(mockTerInfo.getTERCOD()).thenReturn(50);
        when(mockTerInfo.getTERNOM()).thenReturn("Supplier6");
        
        // Act
        List<ContratoDto> result = contratosSearch.searchContrtos(ent, eje, searchMode, term);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(cotRepository, times(1)).findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONBLO(3, ent, eje, 0);
    }

    @Test
    void testSearchContratos_BloqueMode_NumericTerm() {
        // Arrange
        Integer ent = 1;
        String eje = "2024";
        String searchMode = "bloque";
        String term = "99999";
        
        ConnInfo mockConnInfo = mock(ConnInfo.class);
        TerInfo mockTerInfo = mock(TerInfo.class);
        CotContratoProjection mockProjection = mock(CotContratoProjection.class);
        
        when(mockProjection.getConn()).thenReturn(mockConnInfo);
        when(mockProjection.getTer()).thenReturn(mockTerInfo);
        
        List<CotContratoProjection> mockResults = new ArrayList<>();
        mockResults.add(mockProjection);
        
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONCODAndConnCONBLO(3, ent, eje, 99999, 0))
            .thenReturn(mockResults);
        
        when(mockConnInfo.getCONCOD()).thenReturn(99999);
        when(mockConnInfo.getCONLOT()).thenReturn("LOT011");
        when(mockConnInfo.getCONDES()).thenReturn("Bloque Numeric");
        when(mockConnInfo.getCONFIN()).thenReturn(LocalDateTime.of(2024, 1, 1, 0, 0, 0));
        when(mockConnInfo.getCONFFI()).thenReturn(LocalDateTime.of(2024, 12, 31, 0, 0, 0));
        when(mockConnInfo.getCONBLO()).thenReturn(0);
        
        when(mockTerInfo.getTERCOD()).thenReturn(55);
        when(mockTerInfo.getTERNOM()).thenReturn("Supplier7");
        
        // Act
        List<ContratoDto> result = contratosSearch.searchContrtos(ent, eje, searchMode, term);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(cotRepository, times(1)).findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONCODAndConnCONBLO(3, ent, eje, 99999, 0);
    }

    @Test
    void testSearchContratos_BloqueMode_TextTerm() {
        // Arrange
        Integer ent = 1;
        String eje = "2024";
        String searchMode = "bloque";
        String term = "BloqueText";
        
        ConnInfo mockConnInfo = mock(ConnInfo.class);
        TerInfo mockTerInfo = mock(TerInfo.class);
        CotContratoProjection mockProjection = mock(CotContratoProjection.class);
        
        when(mockProjection.getConn()).thenReturn(mockConnInfo);
        when(mockProjection.getTer()).thenReturn(mockTerInfo);
        
        List<CotContratoProjection> mockResults = new ArrayList<>();
        mockResults.add(mockProjection);
        
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONDESAndConnCONBLO(3, ent, eje, term, 0))
            .thenReturn(mockResults);
        
        when(mockConnInfo.getCONCOD()).thenReturn(900);
        when(mockConnInfo.getCONLOT()).thenReturn("LOT012");
        when(mockConnInfo.getCONDES()).thenReturn("BloqueText");
        when(mockConnInfo.getCONFIN()).thenReturn(LocalDateTime.of(2024, 1, 1, 0, 0, 0));
        when(mockConnInfo.getCONFFI()).thenReturn(LocalDateTime.of(2024, 12, 31, 0, 0, 0));
        when(mockConnInfo.getCONBLO()).thenReturn(0);
        
        when(mockTerInfo.getTERCOD()).thenReturn(60);
        when(mockTerInfo.getTERNOM()).thenReturn("Supplier8");
        
        // Act
        List<ContratoDto> result = contratosSearch.searchContrtos(ent, eje, searchMode, term);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(cotRepository, times(1)).findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONDESAndConnCONBLO(3, ent, eje, term, 0);
    }

    // ==================== Test empty results ====================
    
    @Test
    void testSearchContratos_EmptyResult() {
        // Arrange
        Integer ent = 1;
        String eje = "2024";
        String searchMode = "todos";
        String term = null;
        
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJE(3, ent, eje))
            .thenReturn(new ArrayList<>());
        
        // Act
        List<ContratoDto> result = contratosSearch.searchContrtos(ent, eje, searchMode, term);
        
        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testSearchContratos_NullRepositoryResult() {
        // Arrange
        Integer ent = 1;
        String eje = "2024";
        String searchMode = "todos";
        String term = null;
        
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJE(3, ent, eje))
            .thenReturn(null);
        
        // Act
        List<ContratoDto> result = contratosSearch.searchContrtos(ent, eje, searchMode, term);
        
        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    // ==================== Test multiple results ====================
    
    @Test
    void testSearchContratos_MultipleResults() {
        // Arrange
        Integer ent = 1;
        String eje = "2024";
        String searchMode = "todos";
        String term = null;
        
        CotContratoProjection projection1 = mock(CotContratoProjection.class);
        CotContratoProjection projection2 = mock(CotContratoProjection.class);
        
        ConnInfo conn1 = mock(ConnInfo.class);
        ConnInfo conn2 = mock(ConnInfo.class);
        TerInfo ter1 = mock(TerInfo.class);
        TerInfo ter2 = mock(TerInfo.class);
        
        when(projection1.getConn()).thenReturn(conn1);
        when(projection1.getTer()).thenReturn(ter1);
        when(projection2.getConn()).thenReturn(conn2);
        when(projection2.getTer()).thenReturn(ter2);
        
        when(conn1.getCONCOD()).thenReturn(1000);
        when(conn1.getCONLOT()).thenReturn("LOT_A");
        when(conn1.getCONDES()).thenReturn("ContA");
        when(conn1.getCONFIN()).thenReturn(LocalDateTime.of(2024, 1, 1, 0, 0, 0));
        when(conn1.getCONFFI()).thenReturn(LocalDateTime.of(2024, 12, 31, 0, 0, 0));
        when(conn1.getCONBLO()).thenReturn(0);
        
        when(conn2.getCONCOD()).thenReturn(2000);
        when(conn2.getCONLOT()).thenReturn("LOT_B");
        when(conn2.getCONDES()).thenReturn("ContB");
        when(conn2.getCONFIN()).thenReturn(LocalDateTime.of(2024, 2, 1, 0, 0, 0));
        when(conn2.getCONFFI()).thenReturn(LocalDateTime.of(2024, 11, 30, 0, 0, 0));
        when(conn2.getCONBLO()).thenReturn(1);
        
        when(ter1.getTERCOD()).thenReturn(100);
        when(ter1.getTERNOM()).thenReturn("Vendor A");
        when(ter2.getTERCOD()).thenReturn(200);
        when(ter2.getTERNOM()).thenReturn("Vendor B");
        
        List<CotContratoProjection> mockResults = List.of(projection1, projection2);
        
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJE(3, ent, eje))
            .thenReturn(mockResults);
        
        // Act
        List<ContratoDto> result = contratosSearch.searchContrtos(ent, eje, searchMode, term);
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1000, result.get(0).getConcod());
        assertEquals(2000, result.get(1).getConcod());
        assertEquals("Vendor A", result.get(0).getTernom());
        assertEquals("Vendor B", result.get(1).getTernom());
    }

    // ==================== Test isNumbersOnly private method (via search) ====================
    
    @Test
    void testIsNumbersOnly_ValidNumericString() {
        // Arrange
        Integer ent = 1;
        String eje = "2024";
        String searchMode = "todos";
        String term = "123456";
        
        ConnInfo mockConnInfo = mock(ConnInfo.class);
        TerInfo mockTerInfo = mock(TerInfo.class);
        CotContratoProjection mockProjection = mock(CotContratoProjection.class);
        
        when(mockProjection.getConn()).thenReturn(mockConnInfo);
        when(mockProjection.getTer()).thenReturn(mockTerInfo);
        
        List<CotContratoProjection> mockResults = new ArrayList<>();
        mockResults.add(mockProjection);
        
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONCOD(3, ent, eje, 123456))
            .thenReturn(mockResults);
        
        when(mockConnInfo.getCONCOD()).thenReturn(123456);
        when(mockConnInfo.getCONLOT()).thenReturn("LOT");
        when(mockConnInfo.getCONDES()).thenReturn("Desc");
        when(mockConnInfo.getCONFIN()).thenReturn(LocalDateTime.of(2024, 1, 1, 0, 0, 0));
        when(mockConnInfo.getCONFFI()).thenReturn(LocalDateTime.of(2024, 12, 31, 0, 0, 0));
        when(mockConnInfo.getCONBLO()).thenReturn(0);
        
        when(mockTerInfo.getTERCOD()).thenReturn(1);
        when(mockTerInfo.getTERNOM()).thenReturn("Supplier");
        
        // Act - should call numeric search
        List<ContratoDto> result = contratosSearch.searchContrtos(ent, eje, searchMode, term);
        
        // Assert - verify numeric repository method was called (proving isNumbersOnly returned true)
        verify(cotRepository, times(1)).findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONCOD(3, ent, eje, 123456);
    }

    @Test
    void testIsNumbersOnly_StringWithLetters() {
        // Arrange
        Integer ent = 1;
        String eje = "2024";
        String searchMode = "todos";
        String term = "12AB34";
        
        ConnInfo mockConnInfo = mock(ConnInfo.class);
        TerInfo mockTerInfo = mock(TerInfo.class);
        CotContratoProjection mockProjection = mock(CotContratoProjection.class);
        
        when(mockProjection.getConn()).thenReturn(mockConnInfo);
        when(mockProjection.getTer()).thenReturn(mockTerInfo);
        
        List<CotContratoProjection> mockResults = new ArrayList<>();
        mockResults.add(mockProjection);
        
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONDES(3, ent, eje, term))
            .thenReturn(mockResults);
        
        when(mockConnInfo.getCONCOD()).thenReturn(1);
        when(mockConnInfo.getCONLOT()).thenReturn("LOT");
        when(mockConnInfo.getCONDES()).thenReturn("12AB34");
        when(mockConnInfo.getCONFIN()).thenReturn(LocalDateTime.of(2024, 1, 1, 0, 0, 0));
        when(mockConnInfo.getCONFFI()).thenReturn(LocalDateTime.of(2024, 12, 31, 0, 0, 0));
        when(mockConnInfo.getCONBLO()).thenReturn(0);
        
        when(mockTerInfo.getTERCOD()).thenReturn(1);
        when(mockTerInfo.getTERNOM()).thenReturn("Supplier");
        
        // Act - should call text search
        List<ContratoDto> result = contratosSearch.searchContrtos(ent, eje, searchMode, term);
        
        // Assert - verify text repository method was called (proving isNumbersOnly returned false)
        verify(cotRepository, times(1)).findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONDES(3, ent, eje, term);
    }

    @Test
    void testIsNumbersOnly_StringWithSpecialChars() {
        // Arrange
        Integer ent = 1;
        String eje = "2024";
        String searchMode = "todos";
        String term = "123@456";
        
        ConnInfo mockConnInfo = mock(ConnInfo.class);
        TerInfo mockTerInfo = mock(TerInfo.class);
        CotContratoProjection mockProjection = mock(CotContratoProjection.class);
        
        when(mockProjection.getConn()).thenReturn(mockConnInfo);
        when(mockProjection.getTer()).thenReturn(mockTerInfo);
        
        List<CotContratoProjection> mockResults = new ArrayList<>();
        mockResults.add(mockProjection);
        
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONDES(3, ent, eje, term))
            .thenReturn(mockResults);
        
        when(mockConnInfo.getCONCOD()).thenReturn(1);
        when(mockConnInfo.getCONLOT()).thenReturn("LOT");
        when(mockConnInfo.getCONDES()).thenReturn("123@456");
        when(mockConnInfo.getCONFIN()).thenReturn(LocalDateTime.of(2024, 1, 1, 0, 0, 0));
        when(mockConnInfo.getCONFFI()).thenReturn(LocalDateTime.of(2024, 12, 31, 0, 0, 0));
        when(mockConnInfo.getCONBLO()).thenReturn(0);
        
        when(mockTerInfo.getTERCOD()).thenReturn(1);
        when(mockTerInfo.getTERNOM()).thenReturn("Supplier");
        
        // Act
        List<ContratoDto> result = contratosSearch.searchContrtos(ent, eje, searchMode, term);
        
        // Assert
        verify(cotRepository, times(1)).findAllProjectedByConnCONTIPAndConnENTAndConnEJEAndConnCONDES(3, ent, eje, term);
    }

    @Test
    void testIsNumbersOnly_EmptyString() {
        // Arrange - empty string is treated as no search term
        Integer ent = 1;
        String eje = "2024";
        String searchMode = "todos";
        String term = "";
        
        ConnInfo mockConnInfo = mock(ConnInfo.class);
        TerInfo mockTerInfo = mock(TerInfo.class);
        CotContratoProjection mockProjection = mock(CotContratoProjection.class);
        
        when(mockProjection.getConn()).thenReturn(mockConnInfo);
        when(mockProjection.getTer()).thenReturn(mockTerInfo);
        
        List<CotContratoProjection> mockResults = new ArrayList<>();
        mockResults.add(mockProjection);
        
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJE(3, ent, eje))
            .thenReturn(mockResults);
        
        when(mockConnInfo.getCONCOD()).thenReturn(1);
        when(mockConnInfo.getCONLOT()).thenReturn("LOT");
        when(mockConnInfo.getCONDES()).thenReturn("Desc");
        when(mockConnInfo.getCONFIN()).thenReturn(LocalDateTime.of(2024, 1, 1, 0, 0, 0));
        when(mockConnInfo.getCONFFI()).thenReturn(LocalDateTime.of(2024, 12, 31, 0, 0, 0));
        when(mockConnInfo.getCONBLO()).thenReturn(0);
        
        when(mockTerInfo.getTERCOD()).thenReturn(1);
        when(mockTerInfo.getTERNOM()).thenReturn("Supplier");
        
        // Act
        List<ContratoDto> result = contratosSearch.searchContrtos(ent, eje, searchMode, term);
        
        // Assert - should not parse as number
        verify(cotRepository, times(1)).findAllProjectedByConnCONTIPAndConnENTAndConnEJE(3, ent, eje);
    }

    // ==================== Test buildContratoDto with edge cases ====================
    
    @Test
    void testBuildContratoDto_AllFieldsPopulated() {
        // Arrange
        Integer ent = 1;
        String eje = "2024";
        String searchMode = "todos";
        String term = null;
        
        ConnInfo mockConnInfo = mock(ConnInfo.class);
        TerInfo mockTerInfo = mock(TerInfo.class);
        CotContratoProjection mockProjection = mock(CotContratoProjection.class);
        
        when(mockProjection.getConn()).thenReturn(mockConnInfo);
        when(mockProjection.getTer()).thenReturn(mockTerInfo);
        
        List<CotContratoProjection> mockResults = new ArrayList<>();
        mockResults.add(mockProjection);
        
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJE(3, ent, eje))
            .thenReturn(mockResults);
        
        LocalDateTime startDate = LocalDateTime.of(2024, 3, 15, 10, 30, 45);
        LocalDateTime endDate = LocalDateTime.of(2024, 9, 20, 14, 45, 30);
        
        when(mockConnInfo.getCONCOD()).thenReturn(5555);
        when(mockConnInfo.getCONLOT()).thenReturn("ECONOMIC_CODE");
        when(mockConnInfo.getCONDES()).thenReturn("Comprehensive Description");
        when(mockConnInfo.getCONFIN()).thenReturn(startDate);
        when(mockConnInfo.getCONFFI()).thenReturn(endDate);
        when(mockConnInfo.getCONBLO()).thenReturn(1);
        
        when(mockTerInfo.getTERCOD()).thenReturn(777);
        when(mockTerInfo.getTERNOM()).thenReturn("Complete Supplier Name");
        
        // Act
        List<ContratoDto> result = contratosSearch.searchContrtos(ent, eje, searchMode, term);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        
        ContratoDto dto = result.get(0);
        assertEquals(5555, dto.getConcod());
        assertEquals("ECONOMIC_CODE", dto.getConlot());
        assertEquals("Comprehensive Description", dto.getCondes());
        assertEquals(startDate, dto.getConfin());
        assertEquals(endDate, dto.getConffi());
        assertEquals(1, dto.getConblo());
        assertEquals(777, dto.getTercod());
        assertEquals("Complete Supplier Name", dto.getTernom());
    }

    @Test
    void testBuildContratoDto_WithZeroValues() {
        // Arrange
        Integer ent = 1;
        String eje = "2024";
        String searchMode = "todos";
        String term = null;
        
        ConnInfo mockConnInfo = mock(ConnInfo.class);
        TerInfo mockTerInfo = mock(TerInfo.class);
        CotContratoProjection mockProjection = mock(CotContratoProjection.class);
        
        when(mockProjection.getConn()).thenReturn(mockConnInfo);
        when(mockProjection.getTer()).thenReturn(mockTerInfo);
        
        List<CotContratoProjection> mockResults = new ArrayList<>();
        mockResults.add(mockProjection);
        
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJE(3, ent, eje))
            .thenReturn(mockResults);
        
        when(mockConnInfo.getCONCOD()).thenReturn(0);
        when(mockConnInfo.getCONLOT()).thenReturn("0");
        when(mockConnInfo.getCONDES()).thenReturn("");
        when(mockConnInfo.getCONFIN()).thenReturn(LocalDateTime.of(2024, 1, 1, 0, 0, 0));
        when(mockConnInfo.getCONFFI()).thenReturn(LocalDateTime.of(2024, 1, 1, 0, 0, 0));
        when(mockConnInfo.getCONBLO()).thenReturn(0);
        
        when(mockTerInfo.getTERCOD()).thenReturn(0);
        when(mockTerInfo.getTERNOM()).thenReturn("");
        
        // Act
        List<ContratoDto> result = contratosSearch.searchContrtos(ent, eje, searchMode, term);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        
        ContratoDto dto = result.get(0);
        assertEquals(0, dto.getConcod());
        assertEquals("0", dto.getConlot());
        assertEquals("", dto.getCondes());
        assertEquals(0, dto.getConblo());
        assertEquals(0, dto.getTercod());
        assertEquals("", dto.getTernom());
    }

    // ==================== Test with different entity and exercise combinations ====================
    
    @Test
    void testSearchContratos_DifferentEntity() {
        // Arrange
        Integer ent = 999;
        String eje = "2025";
        String searchMode = "todos";
        String term = null;
        
        ConnInfo mockConnInfo = mock(ConnInfo.class);
        TerInfo mockTerInfo = mock(TerInfo.class);
        CotContratoProjection mockProjection = mock(CotContratoProjection.class);
        
        when(mockProjection.getConn()).thenReturn(mockConnInfo);
        when(mockProjection.getTer()).thenReturn(mockTerInfo);
        
        List<CotContratoProjection> mockResults = new ArrayList<>();
        mockResults.add(mockProjection);
        
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJE(3, ent, eje))
            .thenReturn(mockResults);
        
        when(mockConnInfo.getCONCOD()).thenReturn(1);
        when(mockConnInfo.getCONLOT()).thenReturn("LOT");
        when(mockConnInfo.getCONDES()).thenReturn("Desc");
        when(mockConnInfo.getCONFIN()).thenReturn(LocalDateTime.of(2025, 1, 1, 0, 0, 0));
        when(mockConnInfo.getCONFFI()).thenReturn(LocalDateTime.of(2025, 12, 31, 0, 0, 0));
        when(mockConnInfo.getCONBLO()).thenReturn(0);
        
        when(mockTerInfo.getTERCOD()).thenReturn(1);
        when(mockTerInfo.getTERNOM()).thenReturn("Supplier");
        
        // Act
        List<ContratoDto> result = contratosSearch.searchContrtos(ent, eje, searchMode, term);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(cotRepository, times(1)).findAllProjectedByConnCONTIPAndConnENTAndConnEJE(3, ent, eje);
    }

    // ==================== Test with large result sets ====================
    
    @Test
    void testSearchContratos_LargeResultSet() {
        // Arrange
        Integer ent = 1;
        String eje = "2024";
        String searchMode = "todos";
        String term = null;
        
        List<CotContratoProjection> mockResults = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            CotContratoProjection projection = mock(CotContratoProjection.class);
            ConnInfo conn = mock(ConnInfo.class);
            TerInfo ter = mock(TerInfo.class);
            
            when(projection.getConn()).thenReturn(conn);
            when(projection.getTer()).thenReturn(ter);
            
            when(conn.getCONCOD()).thenReturn(i);
            when(conn.getCONLOT()).thenReturn("LOT_" + i);
            when(conn.getCONDES()).thenReturn("Desc_" + i);
            when(conn.getCONFIN()).thenReturn(LocalDateTime.of(2024, 1, 1, 0, 0, 0));
            when(conn.getCONFFI()).thenReturn(LocalDateTime.of(2024, 12, 31, 0, 0, 0));
            when(conn.getCONBLO()).thenReturn(0);
            
            when(ter.getTERCOD()).thenReturn(i);
            when(ter.getTERNOM()).thenReturn("Supplier_" + i);
            
            mockResults.add(projection);
        }
        
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJE(3, ent, eje))
            .thenReturn(mockResults);
        
        // Act
        List<ContratoDto> result = contratosSearch.searchContrtos(ent, eje, searchMode, term);
        
        // Assert
        assertNotNull(result);
        assertEquals(1000, result.size());
        assertEquals(0, result.get(0).getConcod());
        assertEquals(999, result.get(999).getConcod());
    }

    @Test
    void testSearchContratos_RepositoryCalledWithCorrectParameters() {
        // Arrange - This test verifies the exact parameters passed to repository
        Integer ent = 42;
        String eje = "2024";
        String searchMode = "todos";
        String term = null;
        
        when(cotRepository.findAllProjectedByConnCONTIPAndConnENTAndConnEJE(3, ent, eje))
            .thenReturn(new ArrayList<>());
        
        // Act
        contratosSearch.searchContrtos(ent, eje, searchMode, term);
        
        // Assert - verify CONTIP is always 3
        verify(cotRepository).findAllProjectedByConnCONTIPAndConnENTAndConnEJE(3, ent, eje);
    }
}