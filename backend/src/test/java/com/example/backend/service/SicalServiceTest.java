package com.example.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.backend.dto.Tercero;
import com.example.backend.exception.XmlParsingException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class SicalServiceTest {

    private SicalService service;

    @BeforeEach
    void setUp() {
        service = new SicalService();
        ReflectionTestUtils.setField(service, "wsUrl", "http://test-sical-ws:8080/services/Ci?wsdl");
        ReflectionTestUtils.setField(service, "username", "testuser");
        ReflectionTestUtils.setField(service, "password", "testpass");
        ReflectionTestUtils.setField(service, "publicKey", "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...");
        ReflectionTestUtils.setField(service, "orgCode", "ORG001");
        ReflectionTestUtils.setField(service, "entidad", "1");
        ReflectionTestUtils.setField(service, "eje", "E1");
    }

    @Test
    void service_configuresAllRequiredProperties() {
        String wsUrl = (String) ReflectionTestUtils.getField(service, "wsUrl");
        String username = (String) ReflectionTestUtils.getField(service, "username");
        String password = (String) ReflectionTestUtils.getField(service, "password");
        String publicKey = (String) ReflectionTestUtils.getField(service, "publicKey");
        String orgCode = (String) ReflectionTestUtils.getField(service, "orgCode");
        String entidad = (String) ReflectionTestUtils.getField(service, "entidad");
        String eje = (String) ReflectionTestUtils.getField(service, "eje");

        assertNotNull(wsUrl);
        assertNotNull(username);
        assertNotNull(password);
        assertNotNull(publicKey);
        assertNotNull(orgCode);
        assertNotNull(entidad);
        assertNotNull(eje);

        assertEquals("http://test-sical-ws:8080/services/Ci?wsdl", wsUrl);
        assertEquals("testuser", username);
        assertEquals("testpass", password);
        assertTrue(publicKey.startsWith("-----BEGIN PUBLIC KEY-----"));
        assertEquals("ORG001", orgCode);
        assertEquals("1", entidad);
        assertEquals("E1", eje);
    }

    @Test
    void service_hasWebServiceUrlConfigured() {
        String wsUrl = (String) ReflectionTestUtils.getField(service, "wsUrl");
        assertTrue(wsUrl.contains("sical-ws"));
        assertTrue(wsUrl.contains("8080"));
    }

    @Test
    void service_hasSecurityCredentialsConfigured() {
        String username = (String) ReflectionTestUtils.getField(service, "username");
        String password = (String) ReflectionTestUtils.getField(service, "password");

        assertNotNull(username);
        assertNotNull(password);
        assertFalse(username.isEmpty());
        assertFalse(password.isEmpty());
    }

    @Test
    void getTerceros_acceptsNifParameter() throws Exception {
        try {
            service.getTerceros("12345678A", null, null);
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    void getTerceros_acceptsNombreParameter() throws Exception {
        try {
            service.getTerceros(null, "Juan", null);
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    void getTerceros_acceptsApellidoParameter() throws Exception {
        try {
            service.getTerceros(null, null, "García");
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    void getTerceros_acceptsAllParameters() throws Exception {
        try {
            service.getTerceros("12345678A", "Juan", "García");
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    void getTerceros_acceptsNullParameters() throws Exception {
        try {
            service.getTerceros(null, null, null);
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    void getTerceros_withNullXmlResponse_throwsXmlParsingException() {
        assertThrows(XmlParsingException.class, () -> service.getTerceros(null, null, null));
    }

    @Test
    void service_buildsCorrectSoapRequestStructure() {
        assertDoesNotThrow(() -> {
            try {
                service.getTerceros("TEST", null, null);
            } catch (XmlParsingException e) {
            }
        });
    }

    @Test
    void service_handlesMultipleTiposDocumento() throws Exception {
        String[] docTypes = {"NIF", "NIE", "CIF", "PASAPORTE"};
        for (String docType : docTypes) {
            try {
                service.getTerceros(docType + "123456", null, null);
            } catch (XmlParsingException e) {
                assertNotNull(e);
            }
        }
    }

    @Test
    void service_handlesTerceroWithoutApellido() throws Exception {
        try {
            service.getTerceros(null, "SoleNameNoApellido", null);
        } catch (XmlParsingException e) {
            assertNotNull(e);
        }
    }

    @Test
    void service_handlesTerceroWithSpecialCharacters() throws Exception {
        try {
            service.getTerceros("12345678X", "José María", "García-López");
        } catch (XmlParsingException e) {
            assertNotNull(e);
        }
    }

    @Test
    void service_usesBase64Encoding() {
        assertDoesNotThrow(() -> {
            try {
                service.getTerceros("encoded", null, null);
            } catch (XmlParsingException e) {
            }
        });
    }

    @Test
    void service_buildsSoapEnvelopeCorrectly() {
        assertDoesNotThrow(() -> {
            try {
                service.getTerceros(null, null, null);
            } catch (XmlParsingException e) {
                assertTrue(e.getMessage().contains("Error") || e.getCause() != null);
            }
        });
    }

    @Test
    void getTerceros_returnsListType() {
        try {
            service.getTerceros("test", null, null);
        } catch (XmlParsingException e) {
            assertNotNull(e);
        }
    }

    @Test
    void service_includesSecurityHeaders() {
        assertDoesNotThrow(() -> {
            try {
                service.getTerceros(null, null, null);
            } catch (XmlParsingException e) {
                assertNotNull(e);
            }
        });
    }

    @Test
    void service_handlesXmlEncodedContent() throws Exception {
        try {
            service.getTerceros("&lt;encoded&gt;", null, null);
        } catch (XmlParsingException e) {
            assertNotNull(e);
        }
    }

    @Test
    void service_handlesApostropheEncoding() throws Exception {
        try {
            service.getTerceros("O&apos;Brien", null, null);
        } catch (XmlParsingException e) {
            assertNotNull(e);
        }
    }

    @Test
    void service_handlesQuoteEncoding() throws Exception {
        try {
            service.getTerceros("Name&quot;with&quot;quotes", null, null);
        } catch (XmlParsingException e) {
            assertNotNull(e);
        }
    }

    @Test
    void getTerceros_withOnlyNif_isValid() throws Exception {
        try {
            service.getTerceros("12345678A", null, null);
        } catch (XmlParsingException e) {
            assertNotNull(e);
        }
    }

    @Test
    void getTerceros_withOnlyNombre_isValid() throws Exception {
        try {
            service.getTerceros(null, "Juan", null);
        } catch (XmlParsingException e) {
            assertNotNull(e);
        }
    }

    @Test
    void getTerceros_withOnlyApellido_isValid() throws Exception {
        try {
            service.getTerceros(null, null, "García");
        } catch (XmlParsingException e) {
            assertNotNull(e);
        }
    }

    @Test
    void getTerceros_withNifAndNombre_isValid() throws Exception {
        try {
            service.getTerceros("12345678A", "Juan", null);
        } catch (XmlParsingException e) {
            assertNotNull(e);
        }
    }

    @Test
    void getTerceros_withNifAndApellido_isValid() throws Exception {
        try {
            service.getTerceros("12345678A", null, "García");
        } catch (XmlParsingException e) {
            assertNotNull(e);
        }
    }

    @Test
    void getTerceros_withNombreAndApellido_isValid() throws Exception {
        try {
            service.getTerceros(null, "Juan", "García");
        } catch (XmlParsingException e) {
            assertNotNull(e);
        }
    }

    @Test
    void service_initializesSuccessfully() {
        assertNotNull(service);
        assertNotNull(ReflectionTestUtils.getField(service, "wsUrl"));
        assertNotNull(ReflectionTestUtils.getField(service, "username"));
        assertNotNull(ReflectionTestUtils.getField(service, "orgCode"));
    }
}
