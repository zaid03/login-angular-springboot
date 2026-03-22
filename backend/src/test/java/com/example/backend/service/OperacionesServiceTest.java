package com.example.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class OperacionesServiceTest {

    private OperacionesService service;

    @BeforeEach
    void setUp() {
        service = new OperacionesService();
        ReflectionTestUtils.setField(service, "wsUrl", "http://test-sical-ws:8080/services/Ci?wsdl");
        ReflectionTestUtils.setField(service, "username", "testuser");
        ReflectionTestUtils.setField(service, "password", "testpass");
        ReflectionTestUtils.setField(service, "publicKey", "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...");
        ReflectionTestUtils.setField(service, "orgCode", "ORG001");
        ReflectionTestUtils.setField(service, "entidad", "1");
        ReflectionTestUtils.setField(service, "eje", "E1");
    }

    // SearchCriteria builder tests - verifiable without mocking RestTemplate
    @Test
    void searchCriteria_builder_withAllFields_constructsCorrectly() {
        OperacionesService.SearchCriteria criteria = new OperacionesService.SearchCriteria.Builder()
            .numeroOperDesde("100")
            .numeroOperHasta("200")
            .codigoOperacion("OP001")
            .organica("ORG001")
            .funcional("FUN001")
            .economica("ECO001")
            .expediente("EXP001")
            .grupoApunte("GA001")
            .oficina("OF001")
            .build();

        assertNotNull(criteria);
        assertEquals("100", criteria.numeroOperDesde);
        assertEquals("200", criteria.numeroOperHasta);
        assertEquals("OP001", criteria.codigoOperacion);
        assertEquals("ORG001", criteria.organica);
        assertEquals("FUN001", criteria.funcional);
        assertEquals("ECO001", criteria.economica);
        assertEquals("EXP001", criteria.expediente);
        assertEquals("GA001", criteria.grupoApunte);
        assertEquals("OF001", criteria.oficina);
    }

    @Test
    void searchCriteria_builder_withPartialFields_allowsNullValues() {
        OperacionesService.SearchCriteria criteria = new OperacionesService.SearchCriteria.Builder()
            .numeroOperDesde("100")
            .codigoOperacion("OP001")
            .build();

        assertNotNull(criteria);
        assertEquals("100", criteria.numeroOperDesde);
        assertEquals("OP001", criteria.codigoOperacion);
        assertNull(criteria.numeroOperHasta);
        assertNull(criteria.organica);
    }

    @Test
    void searchCriteria_builder_withNoFields_constructsWithAllNulls() {
        OperacionesService.SearchCriteria criteria = new OperacionesService.SearchCriteria.Builder()
            .build();

        assertNotNull(criteria);
        assertNull(criteria.numeroOperDesde);
        assertNull(criteria.numeroOperHasta);
        assertNull(criteria.codigoOperacion);
    }

    @Test
    void searchCriteria_builderChaining_supportsFluentApi() {
        OperacionesService.SearchCriteria criteria = new OperacionesService.SearchCriteria.Builder()
            .numeroOperDesde("100")
            .numeroOperHasta("200")
            .codigoOperacion("OP001")
            .organica("ORG001")
            .funcional("FUN001")
            .economica("ECO001")
            .expediente("EXP001")
            .grupoApunte("GA001")
            .oficina("OF001")
            .build();

        assertNotNull(criteria);
        assertEquals(9, countNonNullFields(criteria));
    }

    // Service configuration tests
    @Test
    void service_configuresPropertiesCorrectly() {
        String wsUrl = (String) ReflectionTestUtils.getField(service, "wsUrl");
        String username = (String) ReflectionTestUtils.getField(service, "username");
        String orgCode = (String) ReflectionTestUtils.getField(service, "orgCode");

        assertEquals("http://test-sical-ws:8080/services/Ci?wsdl", wsUrl);
        assertEquals("testuser", username);
        assertEquals("ORG001", orgCode);
    }

    @Test
    void service_configuresSecurityFieldsCorrectly() {
        String password = (String) ReflectionTestUtils.getField(service, "password");
        String publicKey = (String) ReflectionTestUtils.getField(service, "publicKey");
        String entidad = (String) ReflectionTestUtils.getField(service, "entidad");
        String eje = (String) ReflectionTestUtils.getField(service, "eje");

        assertEquals("testpass", password);
        assertTrue(publicKey.startsWith("-----BEGIN PUBLIC KEY-----"));
        assertEquals("1", entidad);
        assertEquals("E1", eje);
    }

    // Helper method
    private int countNonNullFields(OperacionesService.SearchCriteria criteria) {
        int count = 0;
        if (criteria.numeroOperDesde != null) count++;
        if (criteria.numeroOperHasta != null) count++;
        if (criteria.codigoOperacion != null) count++;
        if (criteria.organica != null) count++;
        if (criteria.funcional != null) count++;
        if (criteria.economica != null) count++;
        if (criteria.expediente != null) count++;
        if (criteria.grupoApunte != null) count++;
        if (criteria.oficina != null) count++;
        return count;
    }
}
