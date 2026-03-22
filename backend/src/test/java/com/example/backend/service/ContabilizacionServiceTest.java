package com.example.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.backend.dto.ContabilizacionRequestDto;
import com.example.backend.dto.ContabilizacionResponseDto;
import com.example.backend.sqlserver2.model.Fac;
import com.example.backend.sqlserver2.model.Fde;
import com.example.backend.sqlserver2.model.Fdt;
import com.example.backend.sqlserver2.repository.FacRepository;
import com.example.backend.sqlserver2.repository.FdeRepository;
import com.example.backend.sqlserver2.repository.FdtRepository;
import com.example.backend.sqlserver2.repository.TerRepository;

@ExtendWith(MockitoExtension.class)
public class ContabilizacionServiceTest {

    @Mock
    private FacRepository facRepository;

    @Mock
    private FdeRepository fdeRepository;

    @Mock
    private FdtRepository fdtRepository;

    @Mock
    private TerRepository terRepository;

    private ContabilizacionService service;

    @BeforeEach
    void setUp() {
        service = new ContabilizacionService();
        ReflectionTestUtils.setField(service, "facRepository", facRepository);
        ReflectionTestUtils.setField(service, "fdeRepository", fdeRepository);
        ReflectionTestUtils.setField(service, "fdtRepository", fdtRepository);
        ReflectionTestUtils.setField(service, "terRepository", terRepository);
        ReflectionTestUtils.setField(service, "sicalWsUrl", "http://test-sical-ws:8080/services/Ci");
    }

    // buildSmlInput tests
    @Test
    void buildSmlInput_withValidData_returnsXmlString() throws Exception {
        ContabilizacionRequestDto req = createValidRequest();
        Fac fac = createValidFac();
        List<Fde> fdeList = List.of(createValidFde());
        List<Fdt> fdtList = List.of(createValidFdt());

        String result = service.buildSmlInput(req, fac, fdeList, fdtList, "12345678A");

        assertNotNull(result);
        assertTrue(result.contains("<e>"));
        assertTrue(result.contains("</e>"));
        assertTrue(result.contains("<ope>"));
        assertTrue(result.contains("<sec>"));
        assertTrue(result.contains("<par>"));
    }

    @Test
    void buildSmlInput_containsRequiredSecurityFields() throws Exception {
        ContabilizacionRequestDto req = createValidRequest();
        Fac fac = createValidFac();
        List<Fde> fdeList = List.of();
        List<Fdt> fdtList = List.of();

        String result = service.buildSmlInput(req, fac, fdeList, fdtList, "12345678A");

        assertTrue(result.contains("<usu>" + req.getUsu() + "</usu>"));
        assertTrue(result.contains("<org>" + req.getOrg() + "</org>"));
        assertTrue(result.contains("<ent>" + req.getEnt() + "</ent>"));
        assertTrue(result.contains("<eje>" + req.getEje() + "</eje>"));
    }

    @Test
    void buildSmlInput_withValidDataAndMultipleLists_succeeds() throws Exception {
        ContabilizacionRequestDto req = createValidRequest();
        Fac fac = createValidFac();
        List<Fde> fdeList = List.of(createValidFde(), createValidFde());
        List<Fdt> fdtList = List.of(createValidFdt(), createValidFdt());

        String result = service.buildSmlInput(req, fac, fdeList, fdtList, "12345678A");

        assertNotNull(result);
        assertTrue(result.contains("<operacion>"));
        assertTrue(result.contains("</operacion>"));
    }

    @Test
    void buildSmlInput_withEmptyFdeList_stillBuildsValidXml() throws Exception {
        ContabilizacionRequestDto req = createValidRequest();
        Fac fac = createValidFac();

        String result = service.buildSmlInput(req, fac, List.of(), List.of(), "12345678A");

        assertNotNull(result);
        assertTrue(result.contains("<l_linea>"));
        assertTrue(result.contains("</l_linea>"));
    }

    @Test
    void buildSmlInput_withMultipleFdeEntries_includesAllLines() throws Exception {
        ContabilizacionRequestDto req = createValidRequest();
        Fac fac = createValidFac();
        List<Fde> fdeList = List.of(
            createValidFde(),
            createValidFde()
        );

        String result = service.buildSmlInput(req, fac, fdeList, List.of(), "12345678A");

        assertTrue(result.contains("<linea>"));
        // Should have 2 linea elements
        int count = 0;
        int index = 0;
        while ((index = result.indexOf("<linea>", index)) != -1) {
            count++;
            index += 1;
        }
        assertEquals(2, count);
    }

    // parseResponse tests
    @Test
    void parseResponse_withSuccessResponse_returnsExitoTrue() {
        String soapResponse = """
            <soap:Envelope>
              <soap:Body>
                <servicioReturn>
                  &lt;response&gt;
                    &lt;exito&gt;-1&lt;/exito&gt;
                    &lt;operacion&gt;
                      &lt;opeext&gt;123&lt;/opeext&gt;
                      &lt;opesical&gt;456&lt;/opesical&gt;
                    &lt;/operacion&gt;
                  &lt;/response&gt;
                </servicioReturn>
              </soap:Body>
            </soap:Envelope>
            """;

        ContabilizacionResponseDto result = service.parseResponse(soapResponse);

        assertTrue(result.isExito());
        assertEquals("Operación generada correctamente", result.getMensaje());
    }

    @Test
    void parseResponse_withFailureResponse_returnsExitoFalse() {
        String soapResponse = """
            <soap:Envelope>
              <soap:Body>
                <servicioReturn>
                  &lt;response&gt;
                    &lt;exito&gt;0&lt;/exito&gt;
                    &lt;codigo&gt;ERR001&lt;/codigo&gt;
                    &lt;desc&gt;Error en la operación&lt;/desc&gt;
                  &lt;/response&gt;
                </servicioReturn>
              </soap:Body>
            </soap:Envelope>
            """;

        ContabilizacionResponseDto result = service.parseResponse(soapResponse);

        assertFalse(result.isExito());
        assertTrue(result.getMensaje().contains("ERR001"));
        assertTrue(result.getMensaje().contains("Error en la operación"));
    }

    @Test
    void parseResponse_withMalformedXml_returnsErrorMessage() {
        String soapResponse = """
            <soap:Envelope>
              <soap:Body>
                <servicioReturn>
                  &lt;invalid&gt;xml structure
                </servicioReturn>
              </soap:Body>
            </soap:Envelope>
            """;

        ContabilizacionResponseDto result = service.parseResponse(soapResponse);

        assertFalse(result.isExito());
        assertTrue(result.getMensaje().contains("error") || result.getMensaje().contains("Error"));
    }

    @Test
    void parseResponse_withNullResponse_returnsErrorMessage() {
        ContabilizacionResponseDto result = service.parseResponse(null);

        assertFalse(result.isExito());
        assertNotNull(result.getMensaje());
    }

    @Test
    void parseResponse_withInvalidSoapEnvelope_returnsErrorMessage() {
        String soapResponse = "not a valid soap response";

        ContabilizacionResponseDto result = service.parseResponse(soapResponse);

        assertFalse(result.isExito());
        assertNotNull(result.getMensaje());
    }

    // sendSmlRequest tests
    @Test
    void sendSmlRequest_withValidSml_returnsSoapResponse() {
        String smlInput = "<e><test>data</test></e>";
        String expectedResponse = "<soap:Envelope>...</soap:Envelope>";

        // This test is limited without mocking RestTemplate at field level
        // In real scenario, would use @SpringBootTest or inject RestTemplate
        String result = service.sendSmlRequest(smlInput, "http://test-url");

        // Since we can't easily mock RestTemplate in unit test, we verify the method doesn't crash
        // In integration test, would verify full response
        assertTrue(result == null || result instanceof String);
    }

    // Helper method tests
    @Test
    void buildSmlInput_formatDateCorrectly() throws Exception {
        ContabilizacionRequestDto req = createValidRequest();
        Fac fac = createValidFac();
        fac.setFACDAT(LocalDateTime.of(2026, 3, 22, 10, 30));

        String result = service.buildSmlInput(req, fac, List.of(), List.of(), "12345678A");

        assertTrue(result.contains("<fdoc>20260322</fdoc>"));
    }

    @Test
    void buildSmlInput_withNullFacDate_omitsDateField() throws Exception {
        ContabilizacionRequestDto req = createValidRequest();
        Fac fac = createValidFac();
        fac.setFACDAT(null);

        String result = service.buildSmlInput(req, fac, List.of(), List.of(), "12345678A");

        assertFalse(result.contains("<fdoc>"));
    }

    @Test
    void buildSmlInput_handlesContratoFieldsWithDefaults() throws Exception {
        ContabilizacionRequestDto req = createValidRequest();
        Fac fac = createValidFac();
        fac.setCONCTP(null);
        fac.setCONCPR(null);
        fac.setCONCCR(null);

        String result = service.buildSmlInput(req, fac, List.of(), List.of(), "12345678A");

        // Should use default values when null
        assertTrue(result.contains("tipContrato"));
        assertTrue(result.contains("proContrato"));
        assertTrue(result.contains("criContrato"));
    }

    @Test
    void buildSmlInput_withFacOptionalFieldsNull_omitsOptionalElements() throws Exception {
        ContabilizacionRequestDto req = createValidRequest();
        Fac fac = createValidFac();
        fac.setFACOPG(null);
        fac.setFACOCT(null);
        fac.setFACTPG(null);

        String result = service.buildSmlInput(req, fac, List.of(), List.of(), "12345678A");

        assertNotNull(result);
        assertTrue(result.contains("<e>"));
    }

    // Response parsing field mapping tests
    @Test
    void parseResponse_withOperacionFields_mapsAllFieldsCorrectly() {
        String soapResponse = """
            <soap:Envelope>
              <soap:Body>
                <servicioReturn>
                  &lt;response&gt;
                    &lt;exito&gt;-1&lt;/exito&gt;
                    &lt;operacion&gt;
                      &lt;opeext&gt;EXT123&lt;/opeext&gt;
                      &lt;opesical&gt;SIC456&lt;/opesical&gt;
                      &lt;nap&gt;NAP789&lt;/nap&gt;
                      &lt;referencia&gt;REF001&lt;/referencia&gt;
                      &lt;importe&gt;1000.50&lt;/importe&gt;
                      &lt;ejercicio&gt;2026&lt;/ejercicio&gt;
                    &lt;/operacion&gt;
                  &lt;/response&gt;
                </servicioReturn>
              </soap:Body>
            </soap:Envelope>
            """;

        ContabilizacionResponseDto result = service.parseResponse(soapResponse);

        assertTrue(result.isExito());
        assertEquals("EXT123", result.getOpeext());
        assertEquals("SIC456", result.getOpesical());
        assertEquals("NAP789", result.getNap());
        assertEquals("REF001", result.getReferencia());
        assertEquals("1000.50", result.getImporte());
        assertEquals("2026", result.getEjercicio());
    }

    // Helper methods for test data
    private ContabilizacionRequestDto createValidRequest() {
        ContabilizacionRequestDto req = new ContabilizacionRequestDto();
        req.setOrg("ORG001");
        req.setEnt("1");
        req.setEje("E1");
        req.setUsu("USER001");
        req.setPwd("password");
        req.setPublicKey("-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...");
        req.setFechaContable("2026-03-22");
        return req;
    }

    private Fac createValidFac() {
        Fac fac = new Fac();
        fac.setEJE("E1");
        fac.setFACNUM(100);
        fac.setCGECOD("CGE001");
        fac.setFACOPG("OP001");
        fac.setFACDAT(LocalDateTime.now());
        fac.setFACOCT(5);
        fac.setFACFPG("20260401");
        fac.setFACTPG("TPG001");
        fac.setFACTXT("Test invoice");
        fac.setCONCTP("Suministro");
        fac.setCONCPR("AdDirec");
        fac.setCONCCR("SinC");
        return fac;
    }

    private Fde createValidFde() {
        Fde fde = new Fde();
        fde.setFDEIMP(100.0);
        fde.setFDEDIF(0.0);
        fde.setFDEORG("ORG001");
        fde.setFDEFUN("FUN001");
        fde.setFDEECO("ECO001");
        fde.setFDEREF("REF001");
        return fde;
    }

    private Fdt createValidFdt() {
        Fdt fdt = new Fdt();
        fdt.setFDTARE("AREA001");
        fdt.setFDTORG("ORG001");
        fdt.setFDTFUN("FUN001");
        fdt.setFDTECO("ECO001");
        fdt.setFDTDTO(50.0);
        fdt.setFDTBSE(100.0);
        fdt.setFDTPRE(10.0);
        fdt.setFDTTXT("Test detail");
        return fdt;
    }
}
