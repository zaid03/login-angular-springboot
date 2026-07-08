package com.example.backend.service;

import com.example.backend.dto.Operaciones;
import com.example.sical.CryptoSical;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OperacionesServiceTest {

    private OperacionesService service;

    @BeforeEach
    void setup() {

        service = new OperacionesService();

        ReflectionTestUtils.setField(service, "wsUrl", "http://localhost/service");
        ReflectionTestUtils.setField(service, "username", "user");
        ReflectionTestUtils.setField(service, "password", "pass");
        ReflectionTestUtils.setField(service, "publicKey", "PUBLICKEY");
        ReflectionTestUtils.setField(service, "orgCode", "ORG");
        ReflectionTestUtils.setField(service, "entidad", "ENT");
        ReflectionTestUtils.setField(service, "eje", "2025");
    }

    @SuppressWarnings("unchecked")
    private List<Operaciones> parse(String xml) throws Exception {

        Method method = OperacionesService.class
                .getDeclaredMethod("parseOperaciones", String.class);

        method.setAccessible(true);

        return (List<Operaciones>) method.invoke(service, xml);
    }

    private Object invoke(String methodName,
                          Class<?> type,
                          Object value) throws Exception {

        Method method = OperacionesService.class
                .getDeclaredMethod(methodName, type);

        method.setAccessible(true);

        return method.invoke(service, value);
    }

    @Test
    void toDouble_shouldParseDecimalPoint() throws Exception {

        Double value = (Double) invoke(
                "toDouble",
                String.class,
                "15.25");

        assertEquals(15.25, value);
    }

    @Test
    void toDouble_shouldParseComma() throws Exception {

        Double value = (Double) invoke(
                "toDouble",
                String.class,
                "15,25");

        assertEquals(15.25, value);
    }

    @Test
    void toDouble_shouldReturnZero_whenInvalid() throws Exception {

        Double value = (Double) invoke(
                "toDouble",
                String.class,
                "abc");

        assertEquals(0.0, value);
    }

    @Test
    void toDouble_shouldReturnZero_whenNull() throws Exception {

        Double value = (Double) invoke(
                "toDouble",
                String.class,
                null);

        assertEquals(0.0, value);
    }

    @Test
    void toLong_shouldParseValue() throws Exception {

        Long value = (Long) invoke(
                "toLong",
                String.class,
                "123");

        assertEquals(123L, value);
    }

    @Test
    void toLong_shouldReturnNull_whenInvalid() throws Exception {

        Long value = (Long) invoke(
                "toLong",
                String.class,
                "abc");

        assertNull(value);
    }

    @Test
    void toInteger_shouldParseValue() throws Exception {

        Integer value = (Integer) invoke(
                "toInteger",
                String.class,
                "55");

        assertEquals(55, value);
    }

    @Test
    void toInteger_shouldReturnNull_whenInvalid() throws Exception {

        Integer value = (Integer) invoke(
                "toInteger",
                String.class,
                "hello");

        assertNull(value);
    }

    @Test
    void decodeOrNull_shouldDecodeBase64() throws Exception {

        String encoded =
                Base64.getEncoder().encodeToString("hello".getBytes());

        String value = (String) invoke(
                "decodeOrNull",
                String.class,
                encoded);

        assertEquals("hello", value);
    }

    @Test
    void decodeOrNull_shouldReturnOriginal_whenNotBase64() throws Exception {

        String value = (String) invoke(
                "decodeOrNull",
                String.class,
                "%%%%");

        assertEquals("%%%%", value);
    }

    @Test
    void decodeOrNull_shouldReturnNull_whenNull() throws Exception {

        String value = (String) invoke(
                "decodeOrNull",
                String.class,
                null);

        assertNull(value);
    }

        @Test
    void normalizeTagName_shouldRemoveNamespace() throws Exception {

        Method method = OperacionesService.class
                .getDeclaredMethod("normalizeTagName", String.class);

        method.setAccessible(true);

        assertEquals("operacion", method.invoke(service, "ns:operacion"));
        assertEquals("operacion", method.invoke(service, "OPERACION"));
        assertEquals("", method.invoke(service, (Object) null));
    }

    @Test
    void parseOperaciones_shouldReturnEmptyList_whenNoOperaciones() throws Exception {

        String xml = """
                <respuesta>
                    <exito>1</exito>
                </respuesta>
                """;

        List<Operaciones> list = parse(xml);

        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
    void parseOperaciones_shouldAcceptMinusOneSuccess() throws Exception {

        String xml = """
                <respuesta>
                    <exito>-1</exito>
                </respuesta>
                """;

        List<Operaciones> list = parse(xml);

        assertNotNull(list);
        assertEquals(0, list.size());
    }

    @Test
    void parseOperaciones_shouldReadSoapWrapper() throws Exception {

        String xml = """
                <soap>
                    <servicioReturn>
                        &lt;respuesta&gt;
                            &lt;exito&gt;1&lt;/exito&gt;
                        &lt;/respuesta&gt;
                    </servicioReturn>
                </soap>
                """;

        List<Operaciones> list = parse(xml);

        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
    void parseOperaciones_shouldDecodeEntities() throws Exception {

        String xml = """
                <soap>
                    <servicioReturn>
                        &lt;respuesta&gt;
                            &lt;exito&gt;1&lt;/exito&gt;
                            &lt;operacion&gt;
                                &lt;numope&gt;15&lt;/numope&gt;
                                &lt;texto&gt;SGVsbG8=&lt;/texto&gt;
                            &lt;/operacion&gt;
                        &lt;/respuesta&gt;
                    </servicioReturn>
                </soap>
                """;

        List<Operaciones> list = parse(xml);

        assertEquals(1, list.size());

        Operaciones op = list.get(0);

        assertEquals(15L, op.getNumope());
        assertEquals("Hello", op.getTexto());
    }

    @Test
    void parseOperaciones_shouldReadBasicFields() throws Exception {

        String xml = """
                <respuesta>
                    <exito>1</exito>
                    <operacion>
                        <numope>99</numope>
                        <importe>12.50</importe>
                        <impiva>2.50</impiva>
                        <impdto>1.25</impdto>
                        <texto>VGVzdA==</texto>
                        <codope>Q09ERQ==</codope>
                        <signo>Kw==</signo>
                    </operacion>
                </respuesta>
                """;

        List<Operaciones> list = parse(xml);

        assertEquals(1, list.size());

        Operaciones op = list.get(0);

        assertEquals(99L, op.getNumope());
        assertEquals(12.50, op.getImporte());
        assertEquals(2.50, op.getImpiva());
        assertEquals(1.25, op.getImpdto());
        assertEquals("Test", op.getTexto());
        assertEquals("CODE", op.getCodope());
        assertEquals("+", op.getSigno());
    }

    @Test
    void parseOperaciones_shouldHandleInvalidNumbers() throws Exception {

        String xml = """
                <respuesta>
                    <exito>1</exito>
                    <operacion>
                        <numope>abc</numope>
                        <importe>xyz</importe>
                    </operacion>
                </respuesta>
                """;

        List<Operaciones> list = parse(xml);

        Operaciones op = list.getFirst();

        assertNull(op.getNumope());
        assertEquals(0.0, op.getImporte());
    }
}