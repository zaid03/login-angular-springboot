package com.example.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.example.backend.exception.XmlParsingException;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PartidasServiceTest {

    private PartidasService service;

    @BeforeEach
    void setUp() {
        service = new PartidasService();
        ReflectionTestUtils.setField(service, "wsUrl", "http://test-sical-ws:8080/services/Ci?wsdl");
        ReflectionTestUtils.setField(service, "username", "testuser");
        ReflectionTestUtils.setField(service, "password", "testpass");
        ReflectionTestUtils.setField(service, "publicKey", "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...");
        ReflectionTestUtils.setField(service, "orgCode", "ORG001");
        ReflectionTestUtils.setField(service, "entidad", "1");
        ReflectionTestUtils.setField(service, "eje", "E1");
    }

    @Test
    void searchCriteria_builder_withAllFields_constructsCorrectly() {
        PartidasService.SearchCriteria criteria = new PartidasService.SearchCriteria.Builder()
            .cenges("CG001")
            .alias("ALIAS001")
            .clorg("ORG001")
            .clfun("FUN001")
            .cleco("ECO001")
            .clcte("CTE001")
            .clpam("PAM001")
            .usucenges("USER001")
            .build();

        assertNotNull(criteria);
        assertEquals("CG001", criteria.cenges);
        assertEquals("ALIAS001", criteria.alias);
        assertEquals("ORG001", criteria.clorg);
        assertEquals("FUN001", criteria.clfun);
        assertEquals("ECO001", criteria.cleco);
        assertEquals("CTE001", criteria.clcte);
        assertEquals("PAM001", criteria.clpam);
        assertEquals("USER001", criteria.usucenges);
    }

    @Test
    void searchCriteria_builder_withPartialFields_allowsNullValues() {
        PartidasService.SearchCriteria criteria = new PartidasService.SearchCriteria.Builder()
            .cenges("CG001")
            .clorg("ORG001")
            .build();

        assertNotNull(criteria);
        assertEquals("CG001", criteria.cenges);
        assertEquals("ORG001", criteria.clorg);
        assertNull(criteria.alias);
        assertNull(criteria.clfun);
    }

    @Test
    void searchCriteria_builder_withNoFields_constructsWithAllNulls() {
        PartidasService.SearchCriteria criteria = new PartidasService.SearchCriteria.Builder()
            .build();

        assertNotNull(criteria);
        assertNull(criteria.cenges);
        assertNull(criteria.alias);
        assertNull(criteria.clorg);
    }

    @Test
    void searchCriteria_builderChaining_supportsFluentApi() {
        PartidasService.SearchCriteria criteria = new PartidasService.SearchCriteria.Builder()
            .cenges("CG001")
            .alias("ALIAS001")
            .clorg("ORG001")
            .clfun("FUN001")
            .cleco("ECO001")
            .clcte("CTE001")
            .clpam("PAM001")
            .usucenges("USER001")
            .build();

        assertNotNull(criteria);
        assertEquals(8, countNonNullFields(criteria));
    }

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

    private int countNonNullFields(PartidasService.SearchCriteria criteria) {
        int count = 0;
        if (criteria.cenges != null) count++;
        if (criteria.alias != null) count++;
        if (criteria.clorg != null) count++;
        if (criteria.clfun != null) count++;
        if (criteria.cleco != null) count++;
        if (criteria.clcte != null) count++;
        if (criteria.clpam != null) count++;
        if (criteria.usucenges != null) count++;
        return count;
    }

    @Test
    void unescapeXmlEntities_replacesAllEntityTypes() throws Exception {
        String input = "&lt;tag&gt;&quot;value&quot;&apos;test&apos;";
        String result = invokeUnescapeXmlEntities(input);
        assertEquals("<tag>\"value\"'test'", result);
    }

    @Test
    void unescapeXmlEntities_handlesLtEntity() throws Exception {
        String result = invokeUnescapeXmlEntities("&lt;");
        assertEquals("<", result);
    }

    @Test
    void unescapeXmlEntities_handlesGtEntity() throws Exception {
        String result = invokeUnescapeXmlEntities("&gt;");
        assertEquals(">", result);
    }

    @Test
    void unescapeXmlEntities_handlesQuotEntity() throws Exception {
        String result = invokeUnescapeXmlEntities("&quot;");
        assertEquals("\"", result);
    }

    @Test
    void unescapeXmlEntities_handlesAposEntity() throws Exception {
        String result = invokeUnescapeXmlEntities("&apos;");
        assertEquals("'", result);
    }

    @Test
    void unescapeXmlEntities_handlesMixedContent() throws Exception {
        String input = "normal&lt;tag&gt;content&quot;data&quot;";
        String result = invokeUnescapeXmlEntities(input);
        assertEquals("normal<tag>content\"data\"", result);
    }

    @Test
    void extractInnerXmlContent_extractsFromServiceioReturn() throws Exception {
        String xml = "<soapenv:Body><impl:servicio><servicioReturn>extracted content</servicioReturn></impl:servicio></soapenv:Body>";
        String result = invokeExtractInnerXmlContent(xml);
        assertEquals("extracted content", result);
    }

    @Test
    void extractInnerXmlContent_returnsFullXmlWhenNoServiceioReturn() throws Exception {
        String xml = "<tag>content</tag>";
        String result = invokeExtractInnerXmlContent(xml);
        assertEquals(xml, result);
    }

    @Test
    void extractInnerXmlContent_returnsEmptyStringForNull() throws Exception {
        String result = invokeExtractInnerXmlContent(null);
        assertEquals("", result);
    }

    @Test
    void extractInnerXmlContent_handlesNestedXml() throws Exception {
        String xml = "<servicioReturn><partida><alias>TEST</alias></partida></servicioReturn>";
        String result = invokeExtractInnerXmlContent(xml);
        assertTrue(result.contains("<partida>"));
        assertTrue(result.contains("TEST"));
    }

    @Test
    void toDouble_parsesValidDouble() throws Exception {
        Double result = invokeToDouble("123.45");
        assertEquals(123.45, result);
    }

    @Test
    void toDouble_handlesPeriodAsDecimal() throws Exception {
        Double result = invokeToDouble("999.99");
        assertEquals(999.99, result);
    }

    @Test
    void toDouble_handlesNullAsZero() throws Exception {
        Double result = invokeToDouble(null);
        assertEquals(0.0, result);
    }

    @Test
    void toDouble_handlesEmptyStringAsZero() throws Exception {
        Double result = invokeToDouble("");
        assertEquals(0.0, result);
    }

    @Test
    void toDouble_handlesWhitespaceAsZero() throws Exception {
        Double result = invokeToDouble("   ");
        assertEquals(0.0, result);
    }

    @Test
    void toDouble_handlesMalformedStringAsZero() throws Exception {
        Double result = invokeToDouble("not-a-number");
        assertEquals(0.0, result);
    }

    @Test
    void toDouble_handlesLargeNumbers() throws Exception {
        Double result = invokeToDouble("999999999.99");
        assertEquals(999999999.99, result);
    }

    @Test
    void toDouble_handlesNegativeNumbers() throws Exception {
        Double result = invokeToDouble("-123.45");
        assertEquals(-123.45, result);
    }

    @Test
    void getTagValue_returnsTagContent() throws Exception {
        String xml = "<root><alias>TEST_ALIAS</alias></root>";
        Document doc = parseXmlDom(xml);
        Element root = doc.getDocumentElement();
        String result = invokeGetTagValue(root, "alias");
        assertEquals("TEST_ALIAS", result);
    }

    @Test
    void getTagValue_returnsNullWhenTagNotFound() throws Exception {
        String xml = "<root><other>value</other></root>";
        Document doc = parseXmlDom(xml);
        Element root = doc.getDocumentElement();
        String result = invokeGetTagValue(root, "missing");
        assertNull(result);
    }

    @Test
    void getTagValue_returnsFirstTagWhenMultiple() throws Exception {
        String xml = "<root><alias>first</alias><alias>second</alias></root>";
        Document doc = parseXmlDom(xml);
        Element root = doc.getDocumentElement();
        String result = invokeGetTagValue(root, "alias");
        assertEquals("first", result);
    }

    @Test
    void getTagValue_returnsEmptyStringForEmptyTag() throws Exception {
        String xml = "<root><alias></alias></root>";
        Document doc = parseXmlDom(xml);
        Element root = doc.getDocumentElement();
        String result = invokeGetTagValue(root, "alias");
        assertEquals("", result);
    }

    @Test
    void getTagValue_returnsNestedContent() throws Exception {
        String xml = "<root><partida><desc>partition description</desc></partida></root>";
        Document doc = parseXmlDom(xml);
        Element root = doc.getDocumentElement();
        String result = invokeGetTagValue(root, "desc");
        assertEquals("partition description", result);
    }

    @Test
    void validateAndThrowIfError_allowsWhenNoExitoTag() throws Exception {
        String xml = "<root><desc>Success</desc></root>";
        Document doc = parseXmlDom(xml);
        assertDoesNotThrow(() -> invokeValidateAndThrowIfError(doc));
    }

    @Test
    void validateAndThrowIfError_allowsWhenExitoIsNegativeOne() throws Exception {
        String xml = "<root><exito>-1</exito></root>";
        Document doc = parseXmlDom(xml);
        assertDoesNotThrow(() -> invokeValidateAndThrowIfError(doc));
    }

    @Test
    void validateAndThrowIfError_allowsWhenExitoIsOne() throws Exception {
        String xml = "<root><exito>1</exito></root>";
        Document doc = parseXmlDom(xml);
        assertDoesNotThrow(() -> invokeValidateAndThrowIfError(doc));
    }

    @Test
    void validateAndThrowIfError_throwsWhenExitoIsTwo() throws Exception {
        String xml = "<root><exito>2</exito><desc>Error message</desc></root>";
        Document doc = parseXmlDom(xml);
        try {
            invokeValidateAndThrowIfError(doc);
            fail("Expected SicalParseException");
        } catch (Exception ex) {
            assertTrue(ex.getCause() instanceof PartidasService.SicalParseException || ex instanceof PartidasService.SicalParseException);
        }
    }

    @Test
    void validateAndThrowIfError_includesDescriptionInError() throws Exception {
        String xml = "<root><exito>0</exito><desc>Invalid data</desc></root>";
        Document doc = parseXmlDom(xml);
        try {
            invokeValidateAndThrowIfError(doc);
            fail("Expected SicalParseException");
        } catch (Exception ex) {
            Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
            assertTrue(cause instanceof PartidasService.SicalParseException);
            assertTrue(cause.getMessage().contains("Invalid data"));
        }
    }

    @Test
    void validateAndThrowIfError_throwsWhenExitoIsZero() throws Exception {
        String xml = "<root><exito>0</exito></root>";
        Document doc = parseXmlDom(xml);
        try {
            invokeValidateAndThrowIfError(doc);
            fail("Expected SicalParseException");
        } catch (Exception ex) {
            assertTrue(ex.getCause() instanceof PartidasService.SicalParseException || ex instanceof PartidasService.SicalParseException);
        }
    }

    @Test
    void parseXmlDocument_parsesValidXml() throws Exception {
        String xml = "<root><item>value</item></root>";
        Document doc = invokeParseXmlDocument(xml);
        assertNotNull(doc);
        assertEquals("root", doc.getDocumentElement().getTagName());
    }

    @Test
    void parseXmlDocument_handlesNestedElements() throws Exception {
        String xml = "<root><parent><child>text</child></parent></root>";
        Document doc = invokeParseXmlDocument(xml);
        assertNotNull(doc);
        assertEquals("root", doc.getDocumentElement().getTagName());
    }

    @Test
    void parseXmlDocument_handlesAttributes() throws Exception {
        String xml = "<root attr=\"value\"><item id=\"1\">text</item></root>";
        Document doc = invokeParseXmlDocument(xml);
        assertNotNull(doc);
        Element root = doc.getDocumentElement();
        assertEquals("value", root.getAttribute("attr"));
    }

    @Test
    void parseXmlDocument_throwsForInvalidXml() throws Exception {
        String xml = "<root><unclosed>";
        try {
            invokeParseXmlDocument(xml);
            fail("Expected XmlParsingException");
        } catch (Exception ex) {
            assertTrue(ex.getCause() instanceof XmlParsingException || ex instanceof XmlParsingException);
        }
    }

    private String invokeUnescapeXmlEntities(String input) throws Exception {
        Method method = PartidasService.class.getDeclaredMethod("unescapeXmlEntities", String.class);
        method.setAccessible(true);
        return (String) method.invoke(service, input);
    }

    private String invokeExtractInnerXmlContent(String xml) throws Exception {
        Method method = PartidasService.class.getDeclaredMethod("extractInnerXmlContent", String.class);
        method.setAccessible(true);
        return (String) method.invoke(service, xml);
    }

    private Double invokeToDouble(String value) throws Exception {
        Method method = PartidasService.class.getDeclaredMethod("toDouble", String.class);
        method.setAccessible(true);
        return (Double) method.invoke(service, value);
    }

    private String invokeGetTagValue(Element parent, String tagName) throws Exception {
        Method method = PartidasService.class.getDeclaredMethod("getTagValue", Element.class, String.class);
        method.setAccessible(true);
        return (String) method.invoke(service, parent, tagName);
    }

    private void invokeValidateAndThrowIfError(Document doc) throws Exception {
        Method method = PartidasService.class.getDeclaredMethod("validateAndThrowIfError", Document.class);
        method.setAccessible(true);
        method.invoke(service, doc);
    }

    private Document invokeParseXmlDocument(String xml) throws Exception {
        Method method = PartidasService.class.getDeclaredMethod("parseXmlDocument", String.class);
        method.setAccessible(true);
        return (Document) method.invoke(service, xml);
    }

    private Document parseXmlDom(String xml) throws Exception {
        Method method = PartidasService.class.getDeclaredMethod("parseXmlDocument", String.class);
        method.setAccessible(true);
        return (Document) method.invoke(service, xml);
    }
}