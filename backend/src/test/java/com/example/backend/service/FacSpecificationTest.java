package com.example.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import com.example.backend.sqlserver2.model.Fac;
import com.example.backend.sqlserver2.model.Ter;
import com.example.backend.service.FacSpecification.SearchCriteria;

import jakarta.persistence.criteria.*;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
public class FacSpecificationTest {

    @Mock
    private Root<Fac> root;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private CriteriaQuery<Fac> query;

    @Mock
    private Join<Object, Object> terJoin;

    @Mock
    private Path<Integer> entPath;

    @Mock
    private Path<String> ejePath;

    @Mock
    private Path<String> cgeccodPath;

    @Mock
    private Predicate predicate;

    @BeforeEach
    void setUp() {
        lenient().when(cb.conjunction()).thenReturn(predicate);
        lenient().when(cb.and(any(), any())).thenReturn(predicate);
        lenient().when(cb.or(any(), any())).thenReturn(predicate);
        lenient().when(cb.or(any(), any(), any())).thenReturn(predicate);
        lenient().when(cb.equal(any(), any())).thenReturn(predicate);
        lenient().when(cb.notEqual(any(), any())).thenReturn(predicate);
        lenient().when(cb.isNull(any())).thenReturn(predicate);
        lenient().when(cb.isNotNull(any())).thenReturn(predicate);
        lenient().when(cb.disjunction()).thenReturn(predicate);
        
        lenient().when(cb.upper(any())).thenAnswer(i -> mock(Expression.class));
        lenient().when(cb.coalesce(any(), any())).thenAnswer(i -> mock(Expression.class));
        lenient().when(cb.function(anyString(), any(), any(), any())).thenAnswer(i -> mock(Expression.class));
        lenient().when(cb.literal(any())).thenAnswer(i -> mock(Expression.class));
        
        lenient().when(root.get(anyString())).thenReturn(mock(Path.class));
        lenient().when(root.join(anyString(), any(JoinType.class))).thenReturn(terJoin);
    }

    @Test
    void searchCriteria_builderCreatesValidCriteria() {
        SearchCriteria criteria = new SearchCriteria.Builder()
            .ent(1)
            .eje("E1")
            .cgecod("CGE001")
            .estado("CONT")
            .dateType("FACTURA")
            .fromDate(LocalDate.of(2024, 1, 1))
            .toDate(LocalDate.of(2024, 12, 31))
            .facannMode("VALUE")
            .facann("2024")
            .search("test")
            .searchType("NIF")
            .build();

        assertEquals(1, criteria.ent);
        assertEquals("E1", criteria.eje);
        assertEquals("CGE001", criteria.cgecod);
        assertEquals("CONT", criteria.estado);
        assertEquals("FACTURA", criteria.dateType);
        assertEquals(LocalDate.of(2024, 1, 1), criteria.fromDate);
        assertEquals(LocalDate.of(2024, 12, 31), criteria.toDate);
        assertEquals("VALUE", criteria.facannMode);
        assertEquals("2024", criteria.facann);
        assertEquals("test", criteria.search);
        assertEquals("NIF", criteria.searchType);
    }

    @Test
    void searchCriteria_builderPartiallyPopulated() {
        SearchCriteria criteria = new SearchCriteria.Builder()
            .ent(1)
            .eje("E1")
            .build();

        assertEquals(1, criteria.ent);
        assertEquals("E1", criteria.eje);
        assertNull(criteria.cgecod);
        assertNull(criteria.estado);
        assertNull(criteria.dateType);
    }

    @Test
    void searchCriteria_builderWithNullValues() {
        SearchCriteria criteria = new SearchCriteria.Builder()
            .ent(null)
            .search(null)
            .build();

        assertNull(criteria.ent);
        assertNull(criteria.search);
    }

    @Test
    void searchCriteria_builderWithAllNullValues() {
        SearchCriteria criteria = new SearchCriteria.Builder()
            .build();

        assertNull(criteria.ent);
        assertNull(criteria.eje);
        assertNull(criteria.cgecod);
        assertNull(criteria.estado);
        assertNull(criteria.dateType);
        assertNull(criteria.fromDate);
        assertNull(criteria.toDate);
        assertNull(criteria.facannMode);
        assertNull(criteria.facann);
        assertNull(criteria.search);
        assertNull(criteria.searchType);
    }

    @Test
    void searchFacturas_returnsSpecification() {
        SearchCriteria criteria = new SearchCriteria.Builder()
            .ent(1)
            .eje("E1")
            .cgecod("CGE001")
            .build();

        Specification<Fac> spec = FacSpecification.searchFacturas(criteria);

        assertNotNull(spec);
        assertTrue(spec instanceof Specification);
    }

    @Test
    void searchFacturas_withBasicFilters() {
        SearchCriteria criteria = new SearchCriteria.Builder()
            .ent(1)
            .eje("E1")
            .cgecod("CGE001")
            .build();

        Specification<Fac> spec = FacSpecification.searchFacturas(criteria);
        
        assertNotNull(spec);
        Predicate result = spec.toPredicate(root, query, cb);
        assertNotNull(result);
    }

    @Test
    void searchFacturas_withDateFilters() {
        SearchCriteria criteria = new SearchCriteria.Builder()
            .ent(1)
            .eje("E1")
            .cgecod("CGE001")
            .dateType("FACTURA")
            .fromDate(LocalDate.of(2024, 1, 1))
            .toDate(LocalDate.of(2024, 12, 31))
            .build();

        Specification<Fac> spec = FacSpecification.searchFacturas(criteria);

        assertNotNull(spec);
        Predicate result = spec.toPredicate(root, query, cb);
        assertNotNull(result);
    }

    @Test
    void searchFacturas_withEstadoContable() {
        SearchCriteria criteria = new SearchCriteria.Builder()
            .ent(1)
            .eje("E1")
            .cgecod("CGE001")
            .estado("CONT")
            .build();

        Specification<Fac> spec = FacSpecification.searchFacturas(criteria);

        assertNotNull(spec);
        Predicate result = spec.toPredicate(root, query, cb);
        assertNotNull(result);
    }

    @Test
    void searchFacturas_withEstadoNoContable() {
        SearchCriteria criteria = new SearchCriteria.Builder()
            .ent(1)
            .eje("E1")
            .cgecod("CGE001")
            .estado("NO_CONT")
            .build();

        Specification<Fac> spec = FacSpecification.searchFacturas(criteria);

        assertNotNull(spec);
        Predicate result = spec.toPredicate(root, query, cb);
        assertNotNull(result);
    }

    @Test
    void searchFacturas_withEstadoTodas() {
        SearchCriteria criteria = new SearchCriteria.Builder()
            .ent(1)
            .eje("E1")
            .cgecod("CGE001")
            .estado("TODAS")
            .build();

        Specification<Fac> spec = FacSpecification.searchFacturas(criteria);

        assertNotNull(spec);
        Predicate result = spec.toPredicate(root, query, cb);
        assertNotNull(result);
    }

    @Test
    void searchFacturas_withEstadoPteApl() {
        SearchCriteria criteria = new SearchCriteria.Builder()
            .ent(1)
            .eje("E1")
            .cgecod("CGE001")
            .estado("PTE_APL")
            .build();

        Specification<Fac> spec = FacSpecification.searchFacturas(criteria);

        assertNotNull(spec);
        Predicate result = spec.toPredicate(root, query, cb);
        assertNotNull(result);
    }

    @Test
    void searchFacturas_withEstadoPteSin() {
        SearchCriteria criteria = new SearchCriteria.Builder()
            .ent(1)
            .eje("E1")
            .cgecod("CGE001")
            .estado("PTE_SIN")
            .build();

        Specification<Fac> spec = FacSpecification.searchFacturas(criteria);

        assertNotNull(spec);
        Predicate result = spec.toPredicate(root, query, cb);
        assertNotNull(result);
    }

    @Test
    void searchFacturas_withFacannModeNull() {
        SearchCriteria criteria = new SearchCriteria.Builder()
            .ent(1)
            .eje("E1")
            .cgecod("CGE001")
            .facannMode("NULL")
            .build();

        Specification<Fac> spec = FacSpecification.searchFacturas(criteria);

        assertNotNull(spec);
        Predicate result = spec.toPredicate(root, query, cb);
        assertNotNull(result);
    }

    @Test
    void searchFacturas_withFacannModeNotNull() {
        SearchCriteria criteria = new SearchCriteria.Builder()
            .ent(1)
            .eje("E1")
            .cgecod("CGE001")
            .facannMode("NOT_NULL")
            .build();

        Specification<Fac> spec = FacSpecification.searchFacturas(criteria);

        assertNotNull(spec);
        Predicate result = spec.toPredicate(root, query, cb);
        assertNotNull(result);
    }

    @Test
    void searchFacturas_withFacannModeValue() {
        SearchCriteria criteria = new SearchCriteria.Builder()
            .ent(1)
            .eje("E1")
            .cgecod("CGE001")
            .facannMode("VALUE")
            .facann("2024")
            .build();

        Specification<Fac> spec = FacSpecification.searchFacturas(criteria);

        assertNotNull(spec);
        Predicate result = spec.toPredicate(root, query, cb);
        assertNotNull(result);
    }

    @Test
    void searchFacturas_withSearchTypeNif() {
        SearchCriteria criteria = new SearchCriteria.Builder()
            .ent(1)
            .eje("E1")
            .cgecod("CGE001")
            .search("12345678A")
            .searchType("NIF")
            .build();

        Specification<Fac> spec = FacSpecification.searchFacturas(criteria);

        assertNotNull(spec);
        Predicate result = spec.toPredicate(root, query, cb);
        assertNotNull(result);
    }

    @Test
    void searchFacturas_withSearchTypeNifLetters() {
        SearchCriteria criteria = new SearchCriteria.Builder()
            .ent(1)
            .eje("E1")
            .cgecod("CGE001")
            .search("Juan")
            .searchType("NIF_LETTERS")
            .build();

        Specification<Fac> spec = FacSpecification.searchFacturas(criteria);

        assertNotNull(spec);
        Predicate result = spec.toPredicate(root, query, cb);
        assertNotNull(result);
    }

    @Test
    void searchFacturas_withSearchTypeOtros() {
        SearchCriteria criteria = new SearchCriteria.Builder()
            .ent(1)
            .eje("E1")
            .cgecod("CGE001")
            .search("test")
            .searchType("OTROS")
            .build();

        Specification<Fac> spec = FacSpecification.searchFacturas(criteria);

        assertNotNull(spec);
        Predicate result = spec.toPredicate(root, query, cb);
        assertNotNull(result);
    }

    @Test
    void searchFacturas_withSearchTypeTercod() {
        SearchCriteria criteria = new SearchCriteria.Builder()
            .ent(1)
            .eje("E1")
            .cgecod("CGE001")
            .search("1001")
            .searchType("TERCOD")
            .build();

        Specification<Fac> spec = FacSpecification.searchFacturas(criteria);

        assertNotNull(spec);
        Predicate result = spec.toPredicate(root, query, cb);
        assertNotNull(result);
    }

    @Test
    void searchFacturas_withSearchTypeTercodInvalidNumber() {
        SearchCriteria criteria = new SearchCriteria.Builder()
            .ent(1)
            .eje("E1")
            .cgecod("CGE001")
            .search("invalid")
            .searchType("TERCOD")
            .build();

        Specification<Fac> spec = FacSpecification.searchFacturas(criteria);

        assertNotNull(spec);
        Predicate result = spec.toPredicate(root, query, cb);
        assertNotNull(result);
    }

    @Test
    void searchFacturas_withDateTypeContable() {
        SearchCriteria criteria = new SearchCriteria.Builder()
            .ent(1)
            .eje("E1")
            .cgecod("CGE001")
            .dateType("CONTABLE")
            .fromDate(LocalDate.of(2024, 1, 1))
            .toDate(LocalDate.of(2024, 12, 31))
            .build();

        Specification<Fac> spec = FacSpecification.searchFacturas(criteria);

        assertNotNull(spec);
        Predicate result = spec.toPredicate(root, query, cb);
        assertNotNull(result);
    }

    @Test
    void searchFacturas_withDateTypeRecepcion() {
        SearchCriteria criteria = new SearchCriteria.Builder()
            .ent(1)
            .eje("E1")
            .cgecod("CGE001")
            .dateType("RECEPCION")
            .fromDate(LocalDate.of(2024, 1, 1))
            .toDate(LocalDate.of(2024, 12, 31))
            .build();

        Specification<Fac> spec = FacSpecification.searchFacturas(criteria);

        assertNotNull(spec);
        Predicate result = spec.toPredicate(root, query, cb);
        assertNotNull(result);
    }

    @Test
    void searchFacturas_withFromDateOnly() {
        SearchCriteria criteria = new SearchCriteria.Builder()
            .ent(1)
            .eje("E1")
            .cgecod("CGE001")
            .dateType("FACTURA")
            .fromDate(LocalDate.of(2024, 1, 1))
            .build();

        Specification<Fac> spec = FacSpecification.searchFacturas(criteria);

        assertNotNull(spec);
        Predicate result = spec.toPredicate(root, query, cb);
        assertNotNull(result);
    }

    @Test
    void searchFacturas_withToDateOnly() {
        SearchCriteria criteria = new SearchCriteria.Builder()
            .ent(1)
            .eje("E1")
            .cgecod("CGE001")
            .dateType("FACTURA")
            .toDate(LocalDate.of(2024, 12, 31))
            .build();

        Specification<Fac> spec = FacSpecification.searchFacturas(criteria);

        assertNotNull(spec);
        Predicate result = spec.toPredicate(root, query, cb);
        assertNotNull(result);
    }

    @Test
    void searchFacturas_withEmptySearch() {
        SearchCriteria criteria = new SearchCriteria.Builder()
            .ent(1)
            .eje("E1")
            .cgecod("CGE001")
            .search("")
            .searchType("NIF")
            .build();

        Specification<Fac> spec = FacSpecification.searchFacturas(criteria);

        assertNotNull(spec);
        Predicate result = spec.toPredicate(root, query, cb);
        assertNotNull(result);
    }

    @Test
    void searchFacturas_withNullSearch() {
        SearchCriteria criteria = new SearchCriteria.Builder()
            .ent(1)
            .eje("E1")
            .cgecod("CGE001")
            .search(null)
            .searchType("NIF")
            .build();

        Specification<Fac> spec = FacSpecification.searchFacturas(criteria);

        assertNotNull(spec);
        Predicate result = spec.toPredicate(root, query, cb);
        assertNotNull(result);
    }

    @Test
    void searchFacturas_withFacannModeAny() {
        SearchCriteria criteria = new SearchCriteria.Builder()
            .ent(1)
            .eje("E1")
            .cgecod("CGE001")
            .facannMode("ANY")
            .build();

        Specification<Fac> spec = FacSpecification.searchFacturas(criteria);

        assertNotNull(spec);
        Predicate result = spec.toPredicate(root, query, cb);
        assertNotNull(result);
    }

    @Test
    void searchFacturas_multipleSearches() {
        SearchCriteria criteria1 = new SearchCriteria.Builder()
            .ent(1)
            .eje("E1")
            .cgecod("CGE001")
            .build();

        SearchCriteria criteria2 = new SearchCriteria.Builder()
            .ent(2)
            .eje("E2")
            .cgecod("CGE002")
            .estado("CONT")
            .build();

        Specification<Fac> spec1 = FacSpecification.searchFacturas(criteria1);
        Specification<Fac> spec2 = FacSpecification.searchFacturas(criteria2);

        assertNotNull(spec1);
        assertNotNull(spec2);
        assertTrue(spec1 instanceof Specification);
        assertTrue(spec2 instanceof Specification);
    }

    @Test
    void searchCriteria_builderChaining() {
        SearchCriteria criteria = new SearchCriteria.Builder()
            .ent(1)
            .eje("E1")
            .cgecod("CGE001")
            .estado("CONT")
            .dateType("FACTURA")
            .fromDate(LocalDate.of(2024, 1, 1))
            .toDate(LocalDate.of(2024, 12, 31))
            .facannMode("VALUE")
            .facann("2024")
            .search("test")
            .searchType("NIF")
            .build();

        assertNotNull(criteria);
        assertAll(
            () -> assertEquals(1, criteria.ent),
            () -> assertEquals("E1", criteria.eje),
            () -> assertEquals("CGE001", criteria.cgecod),
            () -> assertEquals("CONT", criteria.estado),
            () -> assertEquals("FACTURA", criteria.dateType),
            () -> assertEquals(LocalDate.of(2024, 1, 1), criteria.fromDate),
            () -> assertEquals(LocalDate.of(2024, 12, 31), criteria.toDate),
            () -> assertEquals("VALUE", criteria.facannMode),
            () -> assertEquals("2024", criteria.facann),
            () -> assertEquals("test", criteria.search),
            () -> assertEquals("NIF", criteria.searchType)
        );
    }

    @Test
    void searchFacturas_withComplexCriteria() {
        SearchCriteria criteria = new SearchCriteria.Builder()
            .ent(1)
            .eje("E1")
            .cgecod("CGE001")
            .estado("CONT")
            .dateType("FACTURA")
            .fromDate(LocalDate.of(2024, 1, 1))
            .toDate(LocalDate.of(2024, 12, 31))
            .facannMode("NOT_NULL")
            .search("12345678A")
            .searchType("NIF")
            .build();

        Specification<Fac> spec = FacSpecification.searchFacturas(criteria);

        assertNotNull(spec);
        Predicate result = spec.toPredicate(root, query, cb);
        assertNotNull(result);
    }

    @Test
    void searchFacturas_withSpecialCharactersInSearch() {
        SearchCriteria criteria = new SearchCriteria.Builder()
            .ent(1)
            .eje("E1")
            .cgecod("CGE001")
            .search("García-López")
            .searchType("OTROS")
            .build();

        Specification<Fac> spec = FacSpecification.searchFacturas(criteria);

        assertNotNull(spec);
        Predicate result = spec.toPredicate(root, query, cb);
        assertNotNull(result);
    }

    @Test
    void searchFacturas_withLargeAmount() {
        SearchCriteria criteria = new SearchCriteria.Builder()
            .ent(999)
            .eje("EJE_LONG_NAME")
            .cgecod("CGE_VERY_LONG_CODE_9999")
            .build();

        Specification<Fac> spec = FacSpecification.searchFacturas(criteria);

        assertNotNull(spec);
        Predicate result = spec.toPredicate(root, query, cb);
        assertNotNull(result);
    }
}