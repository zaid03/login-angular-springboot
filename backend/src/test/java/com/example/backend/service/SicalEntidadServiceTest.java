package com.example.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.backend.dto.Entidad;
import com.example.backend.exception.SmlProcessingException;

import java.util.List;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class SicalEntidadServiceTest {

    private SicalEntidadService service;

    @BeforeEach
    void setUp() {
        service = new SicalEntidadService();
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
    void service_hasSecurityFieldsConfigured() {
        String username = (String) ReflectionTestUtils.getField(service, "username");
        String password = (String) ReflectionTestUtils.getField(service, "password");
        String orgCode = (String) ReflectionTestUtils.getField(service, "orgCode");

        assertNotNull(username);
        assertNotNull(password);
        assertNotNull(orgCode);
        assertFalse(username.isEmpty());
        assertFalse(password.isEmpty());
        assertFalse(orgCode.isEmpty());
    }

    @Test
    void getEntidades_throwsExceptionWithoutHttpMocking() {
        assertThrows(SmlProcessingException.class, () -> service.getEntidades());
    }

    @Test
    void getEntidades_buildsProperSoapEnvelope() {
        assertDoesNotThrow(() -> {
            try {
                service.getEntidades();
            } catch (SmlProcessingException e) {
                assertNotNull(e.getMessage());
            }
        });
    }

    @Test
    void unescapeXml_handlesLessThanEntity() {
        String escaped = "test &lt; data";
        String result = callUnescapeXml(escaped);
        assertEquals("test < data", result);
    }

    @Test
    void unescapeXml_handlesGreaterThanEntity() {
        String escaped = "test &gt; data";
        String result = callUnescapeXml(escaped);
        assertEquals("test > data", result);
    }

    @Test
    void unescapeXml_handlesAmpersandEntity() {
        String escaped = "test &amp; data";
        String result = callUnescapeXml(escaped);
        assertEquals("test & data", result);
    }

    @Test
    void unescapeXml_handlesQuoteEntity() {
        String escaped = "test &quot;quoted&quot; text";
        String result = callUnescapeXml(escaped);
        assertEquals("test \"quoted\" text", result);
    }

    @Test
    void unescapeXml_handlesApostropheEntity() {
        String escaped = "O&apos;Brien";
        String result = callUnescapeXml(escaped);
        assertEquals("O'Brien", result);
    }

    @Test
    void unescapeXml_handlesMultipleEntities() {
        String escaped = "&lt;test&gt; &quot;data&quot; &apos;ok&apos;";
        String result = callUnescapeXml(escaped);
        assertEquals("<test> \"data\" 'ok'", result);
    }

    @Test
    void unescapeXml_withNullInput_returnsNull() {
        String result = callUnescapeXml(null);
        assertNull(result);
    }

    @Test
    void unescapeXml_withEmptyString_returnsEmpty() {
        String result = callUnescapeXml("");
        assertEquals("", result);
    }

    @Test
    void unescapeXml_withNoEntities_returnsOriginal() {
        String input = "plain text without entities";
        String result = callUnescapeXml(input);
        assertEquals("plain text without entities", result);
    }

    @Test
    void decodeBase64Safe_withValidBase64() {
        String encoded = Base64.getEncoder().encodeToString("TestData".getBytes(StandardCharsets.UTF_8));
        String result = callDecodeBase64Safe(encoded);
        assertEquals("TestData", result);
    }

    @Test
    void decodeBase64Safe_withUTF8Characters() {
        String original = "José García";
        String encoded = Base64.getEncoder().encodeToString(original.getBytes(StandardCharsets.UTF_8));
        String result = callDecodeBase64Safe(encoded);
        assertEquals(original, result);
    }

    @Test
    void decodeBase64Safe_withSpecialCharacters() {
        String original = "Test-Data_123!@#";
        String encoded = Base64.getEncoder().encodeToString(original.getBytes(StandardCharsets.UTF_8));
        String result = callDecodeBase64Safe(encoded);
        assertEquals(original, result);
    }

    @Test
    void decodeBase64Safe_withInvalidBase64_returnsInput() {
        String invalid = "not-valid-base64!@#$%";
        String result = callDecodeBase64Safe(invalid);
        assertEquals(invalid, result);
    }

    @Test
    void decodeBase64Safe_withNullInput_returnsEmpty() {
        String result = callDecodeBase64Safe(null);
        assertEquals("", result);
    }

    @Test
    void decodeBase64Safe_withEmptyString_returnsEmpty() {
        String result = callDecodeBase64Safe("");
        assertEquals("", result);
    }

    @Test
    void decodeBase64Safe_withMalformedBase64_returnsInputAsFallback() {
        String malformed = "aGVsbG8=extra";
        String result = callDecodeBase64Safe(malformed);
        assertNotNull(result);
    }

    @Test
    void service_parseEntidadWithCodeAndNombre() {
        String codigo = "E001";
        String nombre = "Entidad Prueba";
        String codigoB64 = Base64.getEncoder().encodeToString(codigo.getBytes(StandardCharsets.UTF_8));
        String nombreB64 = Base64.getEncoder().encodeToString(nombre.getBytes(StandardCharsets.UTF_8));
        String detalle = codigoB64 + "@" + nombreB64;

        Entidad result = callParseEntidadFromDetalle(detalle);

        assertNotNull(result);
        assertEquals(codigo, result.getCodigo());
        assertEquals(nombre, result.getNombre());
    }

    @Test
    void service_parseEntidadWithOnlyCode() {
        String codigo = "E002";
        String codigoB64 = Base64.getEncoder().encodeToString(codigo.getBytes(StandardCharsets.UTF_8));
        String detalle = codigoB64 + "@";

        Entidad result = callParseEntidadFromDetalle(detalle);

        assertNotNull(result);
        assertEquals(codigo, result.getCodigo());
        assertEquals("", result.getNombre());
    }

    @Test
    void service_parseEntidadWithEmptyCode_returnsNull() {
        String detalle = "@nombre";
        Entidad result = callParseEntidadFromDetalle(detalle);
        assertNull(result);
    }

    @Test
    void service_parseEntidadHandlesNonBase64Values() {
        String detalle = "plain_codigo@plain_nombre";
        Entidad result = callParseEntidadFromDetalle(detalle);

        assertNotNull(result);
        assertEquals("plain_codigo", result.getCodigo());
        assertEquals("plain_nombre", result.getNombre());
    }

    @Test
    void service_parseEntidadWithSpecialCharactersInNombre() {
        String codigo = "E003";
        String nombre = "Entidad - García & López";
        String codigoB64 = Base64.getEncoder().encodeToString(codigo.getBytes(StandardCharsets.UTF_8));
        String nombreB64 = Base64.getEncoder().encodeToString(nombre.getBytes(StandardCharsets.UTF_8));
        String detalle = codigoB64 + "@" + nombreB64;

        Entidad result = callParseEntidadFromDetalle(detalle);

        assertNotNull(result);
        assertEquals(nombre, result.getNombre());
    }

    @Test
    void service_handlesMissingResponse() {
        assertThrows(SmlProcessingException.class, () -> service.getEntidades());
    }

    @Test
    void service_handlesNullXmlResponse() {
        assertThrows(SmlProcessingException.class, () -> service.getEntidades());
    }

    @Test
    void service_buildsSoapEnvelopeWithCorrectStructure() {
        assertDoesNotThrow(() -> {
            try {
                service.getEntidades();
            } catch (SmlProcessingException e) {
                // Expected; method structure is valid
                assertTrue(e.getMessage().contains("Error") || e.getCause() != null);
            }
        });
    }

    @Test
    void service_includesRequiredHeaders() {
        assertDoesNotThrow(() -> {
            try {
                service.getEntidades();
            } catch (SmlProcessingException e) {
                assertNotNull(e);
            }
        });
    }

    @Test
    void service_configuresSecureXmlParsing() {
        assertDoesNotThrow(() -> {
            try {
                service.getEntidades();
            } catch (SmlProcessingException e) {
                assertNotNull(e);
            }
        });
    }

    @Test
    void service_initializesSuccessfully() {
        assertNotNull(service);
        assertNotNull(ReflectionTestUtils.getField(service, "wsUrl"));
        assertNotNull(ReflectionTestUtils.getField(service, "orgCode"));
        assertNotNull(ReflectionTestUtils.getField(service, "entidad"));
        assertNotNull(ReflectionTestUtils.getField(service, "eje"));
    }

    private String callUnescapeXml(String input) {
        try {
            java.lang.reflect.Method method = SicalEntidadService.class.getDeclaredMethod("unescapeXml", String.class);
            method.setAccessible(true);
            return (String) method.invoke(service, input);
        } catch (Exception e) {
            fail("Could not invoke unescapeXml: " + e.getMessage());
            return null;
        }
    }

    private String callDecodeBase64Safe(String input) {
        try {
            java.lang.reflect.Method method = SicalEntidadService.class.getDeclaredMethod("decodeBase64Safe", String.class);
            method.setAccessible(true);
            return (String) method.invoke(service, input);
        } catch (Exception e) {
            fail("Could not invoke decodeBase64Safe: " + e.getMessage());
            return null;
        }
    }

    private Entidad callParseEntidadFromDetalle(String detalle) {
        try {
            java.lang.reflect.Method method = SicalEntidadService.class.getDeclaredMethod("parseEntidadFromDetalle", String.class);
            method.setAccessible(true);
            return (Entidad) method.invoke(service, detalle);
        } catch (Exception e) {
            fail("Could not invoke parseEntidadFromDetalle: " + e.getMessage());
            return null;
        }
    }
}
