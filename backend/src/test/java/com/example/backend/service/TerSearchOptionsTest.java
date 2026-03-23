package com.example.backend.service;

import com.example.backend.sqlserver2.model.Ter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TerSearchOptionsTest {

    @Mock
    private Root<Ter> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private Path<Object> mockPath;

    @Mock
    private Predicate mockPredicate;

    @BeforeEach
    void setUp() {
        when(root.get(anyString())).thenReturn(mockPath);
        when(cb.equal(any(), any())).thenReturn(mockPredicate);
        when(cb.notEqual(any(), any())).thenReturn(mockPredicate);
        when(cb.like(any(), anyString())).thenReturn(mockPredicate);
        when(cb.or(any(Predicate.class), any(Predicate.class))).thenReturn(mockPredicate);
        when(cb.or(any(Predicate.class), any(Predicate.class), any(Predicate.class))).thenReturn(mockPredicate);
        when(cb.and(any(Predicate.class), any(Predicate.class))).thenReturn(mockPredicate);
        when(cb.and(any(Predicate.class), any(Predicate.class), any(Predicate.class))).thenReturn(mockPredicate);
    }

    @Test
    void searchFiltered_returnsSpecification() {
        Specification<Ter> spec = TerSearchOptions.searchFiltered(1, "test");
        assertNotNull(spec);
    }

    @Test
    void searchFiltered_buildsPredicate() {
        Specification<Ter> spec = TerSearchOptions.searchFiltered(1, "test");
        assertDoesNotThrow(() -> spec.toPredicate(root, query, cb));
    }

    @Test
    void searchFiltered_filters_byEntity() {
        Specification<Ter> spec = TerSearchOptions.searchFiltered(5, "term");
        spec.toPredicate(root, query, cb);

        verify(root, atLeastOnce()).get("ENT");
        verify(cb).equal(mockPath, 5);
    }

    @Test
    void searchFiltered_searches_allFields() {
        Specification<Ter> spec = TerSearchOptions.searchFiltered(1, "test");
        spec.toPredicate(root, query, cb);

        verify(root).get("TERNIF");
        verify(root).get("TERNOM");
        verify(root).get("TERALI");
    }

    @Test
    void searchFiltered_usesLikePredicate() {
        Specification<Ter> spec = TerSearchOptions.searchFiltered(1, "searchterm");
        spec.toPredicate(root, query, cb);

        verify(cb, atLeastOnce()).like(any(), contains("searchterm"));
    }

    @Test
    void searchByTerm_returnsSpecification() {
        Specification<Ter> spec = TerSearchOptions.searchByTerm(1, "test");
        assertNotNull(spec);
    }

    @Test
    void searchByTerm_filtersBloqueado() {
        Specification<Ter> spec = TerSearchOptions.searchByTerm(1, "test");
        spec.toPredicate(root, query, cb);

        verify(root).get("TERBLO");
        verify(cb).notEqual(mockPath, 1);
    }

    @Test
    void searchByTerm_searches_allFields() {
        Specification<Ter> spec = TerSearchOptions.searchByTerm(1, "test");
        spec.toPredicate(root, query, cb);

        verify(root).get("TERNIF");
        verify(root).get("TERNOM");
        verify(root).get("TERALI");
    }

    @Test
    void searchByNomOrAli_returnsSpecification() {
        Specification<Ter> spec = TerSearchOptions.searchByNomOrAli(1, "test");
        assertNotNull(spec);
    }

    @Test
    void searchByNomOrAli_filtersBloqueadoEquals1() {
        Specification<Ter> spec = TerSearchOptions.searchByNomOrAli(1, "test");
        spec.toPredicate(root, query, cb);

        verify(root).get("TERBLO");
        verify(cb, atLeastOnce()).equal(mockPath, 1);
    }

    @Test
    void searchByNomOrAli_searchesOnlyNomAndAli() {
        Specification<Ter> spec = TerSearchOptions.searchByNomOrAli(1, "test");
        spec.toPredicate(root, query, cb);

        verify(root).get("TERNOM");
        verify(root).get("TERALI");
        verify(root, never()).get("TERNIF");
    }

    @Test
    void searchByNomOrAli_usesOrPredicate() {
        Specification<Ter> spec = TerSearchOptions.searchByNomOrAli(1, "test");
        spec.toPredicate(root, query, cb);

        verify(cb, times(1)).or(any(Predicate.class), any(Predicate.class));
    }

    @Test
    void findMatchingNomOrAli_returnsSpecification() {
        Specification<Ter> spec = TerSearchOptions.findMatchingNomOrAli(1, "test");
        assertNotNull(spec);
    }

    @Test
    void findMatchingNomOrAli_filtersBloqueadoNotEqual1() {
        Specification<Ter> spec = TerSearchOptions.findMatchingNomOrAli(1, "test");
        spec.toPredicate(root, query, cb);

        verify(root).get("TERBLO");
        verify(cb).notEqual(mockPath, 1);
    }

    @Test
    void findMatchingNomOrAli_searchesOnlyNomAndAli() {
        Specification<Ter> spec = TerSearchOptions.findMatchingNomOrAli(1, "test");
        spec.toPredicate(root, query, cb);

        verify(root).get("TERNOM");
        verify(root).get("TERALI");
        verify(root, never()).get("TERNIF");
    }

    @Test
    void searchTodos_returnsSpecification() {
        Specification<Ter> spec = TerSearchOptions.searchTodos(1, "test");
        assertNotNull(spec);
    }

    @Test
    void searchTodos_filtersEntity() {
        Specification<Ter> spec = TerSearchOptions.searchTodos(1, "test");
        spec.toPredicate(root, query, cb);

        verify(root).get("ENT");
        verify(cb).equal(mockPath, 1);
    }

    @Test
    void searchTodos_searches_allFields() {
        Specification<Ter> spec = TerSearchOptions.searchTodos(2, "term");
        spec.toPredicate(root, query, cb);

        verify(root).get("TERNIF");
        verify(root).get("TERNOM");
        verify(root).get("TERALI");
    }

    @Test
    void searchTodos_doesNotFilterBloqueado() {
        Specification<Ter> spec = TerSearchOptions.searchTodos(1, "test");
        spec.toPredicate(root, query, cb);

        verify(root, never()).get("TERBLO");
    }

    @Test
    void searchFiltered_withDifferentEntities() {
        for (int ent : new int[]{1, 5, 10, 99}) {
            Specification<Ter> spec = TerSearchOptions.searchFiltered(ent, "test");
            assertNotNull(spec);
        }
    }

    @Test
    void searchByTerm_withEmptyTerm() {
        Specification<Ter> spec = TerSearchOptions.searchByTerm(1, "");
        assertDoesNotThrow(() -> spec.toPredicate(root, query, cb));
    }

    @Test
    void searchByTerm_withSpecialCharacters() {
        Specification<Ter> spec = TerSearchOptions.searchByTerm(1, "%_*&^$");
        assertDoesNotThrow(() -> spec.toPredicate(root, query, cb));
    }

    @Test
    void searchByNomOrAli_returnsValidSpec() {
        Specification<Ter> spec = TerSearchOptions.searchByNomOrAli(3, "supplier");
        assertNotNull(spec);
        assertDoesNotThrow(() -> spec.toPredicate(root, query, cb));
    }

    @Test
    void findMatchingNomOrAli_returnsValidSpec() {
        Specification<Ter> spec = TerSearchOptions.findMatchingNomOrAli(7, "vendor");
        assertNotNull(spec);
        assertDoesNotThrow(() -> spec.toPredicate(root, query, cb));
    }

    @Test
    void searchTodos_returnsValidSpec() {
        Specification<Ter> spec = TerSearchOptions.searchTodos(1, "all");
        assertNotNull(spec);
        assertDoesNotThrow(() -> spec.toPredicate(root, query, cb));
    }

    @Test
    void searchFiltered_andPredicateCombinesAll() {
        Specification<Ter> spec = TerSearchOptions.searchFiltered(1, "test");
        spec.toPredicate(root, query, cb);

        verify(cb, atLeastOnce()).and(any(), any(), any());
    }

    @Test
    void searchByTerm_andPredicateCombinesAll() {
        Specification<Ter> spec = TerSearchOptions.searchByTerm(1, "test");
        spec.toPredicate(root, query, cb);

        verify(cb, atLeastOnce()).and(any(), any(), any());
    }

    @Test
    void searchByNomOrAli_andPredicateCombinesAll() {
        Specification<Ter> spec = TerSearchOptions.searchByNomOrAli(1, "test");
        spec.toPredicate(root, query, cb);

        verify(cb, atLeastOnce()).and(any(), any(), any());
    }

    @Test
    void findMatchingNomOrAli_andPredicateCombinesAll() {
        Specification<Ter> spec = TerSearchOptions.findMatchingNomOrAli(1, "test");
        spec.toPredicate(root, query, cb);

        verify(cb, atLeastOnce()).and(any(), any(), any());
    }

    @Test
    void searchTodos_andPredicateCombinesAll() {
        Specification<Ter> spec = TerSearchOptions.searchTodos(1, "test");
        spec.toPredicate(root, query, cb);

        verify(cb, atLeastOnce()).and(any(), any());
    }

    @Test
    void allMethods_acceptNullEntity() {
        assertDoesNotThrow(() -> TerSearchOptions.searchFiltered(null, "test"));
        assertDoesNotThrow(() -> TerSearchOptions.searchByTerm(null, "test"));
        assertDoesNotThrow(() -> TerSearchOptions.searchByNomOrAli(null, "test"));
        assertDoesNotThrow(() -> TerSearchOptions.findMatchingNomOrAli(null, "test"));
        assertDoesNotThrow(() -> TerSearchOptions.searchTodos(null, "test"));
    }

    @Test
    void allMethods_acceptNullTerm() {
        assertDoesNotThrow(() -> TerSearchOptions.searchFiltered(1, null));
        assertDoesNotThrow(() -> TerSearchOptions.searchByTerm(1, null));
        assertDoesNotThrow(() -> TerSearchOptions.searchByNomOrAli(1, null));
        assertDoesNotThrow(() -> TerSearchOptions.findMatchingNomOrAli(1, null));
        assertDoesNotThrow(() -> TerSearchOptions.searchTodos(1, null));
    }
}