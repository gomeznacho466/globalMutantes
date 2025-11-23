package com.mercadolibre.mutant.domain.detector;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para MutantDetector
 * Cobertura completa de casos: mutantes, humanos, edge cases y validaciones
 */
@DisplayName("MutantDetector Tests")
class MutantDetectorTest {

    private MutantDetector mutantDetector;

    @BeforeEach
    void setUp() {
        mutantDetector = new MutantDetector();
    }

    // ==================== TESTS DE MUTANTES ====================

    @Test
    @DisplayName("Debe detectar mutante con secuencias horizontal y vertical")
    void testMutant_HorizontalAndVertical() {
        String[] dna = {
            "ATGCGA",
            "CAGTGC",
            "TTATGT",
            "AGAAGG",
            "CCCCTA",
            "TCACTG"
        };
        
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe detectar mutante con secuencias diagonales")
    void testMutant_Diagonal() {
        String[] dna = {
            "ATGCGA",
            "CAGTGC",
            "TTATGT",
            "AGAAGG",
            "CCCCTA",
            "TCACTG"
        };
        
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe detectar mutante con múltiples A horizontales")
    void testMutant_MultipleHorizontalA() {
        String[] dna = {
            "AAAATG",
            "TGCAGT",
            "GCTTCC",
            "CCCCTG",
            "GTAGTC",
            "AGTCAC"
        };
        
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe detectar mutante con diagonal descendente")
    void testMutant_DiagonalDescending() {
        String[] dna = {
            "ATGCGA",
            "CAGTGC",
            "TTATGT",
            "AGAAGG",
            "CCCCTA",
            "TCACTG"
        };
        
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe detectar mutante en matriz 4x4 (tamaño mínimo)")
    void testMutant_4x4Matrix() {
        String[] dna = {
            "AAAA",
            "CCCC",
            "TATA",
            "TGTG"
        };
        
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe detectar mutante con early termination (optimización)")
    void testMutant_EarlyTermination() {
        // Dos secuencias en las primeras filas -> debe terminar rápido
        String[] dna = {
            "AAAA",
            "AAAA",
            "TATA",
            "TGTG"
        };
        
        assertTrue(mutantDetector.isMutant(dna));
    }

    // ==================== TESTS DE HUMANOS ====================

    @Test
    @DisplayName("Debe detectar humano (no mutante) - caso base")
    void testHuman_NotMutant() {
        String[] dna = {
            "ATGCGA",
            "CAGTGC",
            "TTATGT",
            "AGACGG",
            "CCCTTA",
            "TCACTG"
        };
        
        assertFalse(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe detectar humano con solo una secuencia")
    void testHuman_OnlyOneSequence() {
        String[] dna = {
            "ATGCGA",
            "CAGTGC",
            "TTATGT",
            "AGACGG",
            "GCGTCA",
            "TCACTG"
        };
        
        assertFalse(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe detectar humano sin secuencias")
    void testHuman_NoSequences() {
        String[] dna = {
            "ATGC",
            "CAGT",
            "TTAT",
            "AGAC"
        };
        
        assertFalse(mutantDetector.isMutant(dna));
    }

    // ==================== TESTS DE VALIDACIÓN ====================

    @Test
    @DisplayName("Debe lanzar excepción si DNA es null")
    void testValidation_NullDna() {
        assertThrows(IllegalArgumentException.class, () -> {
            mutantDetector.isMutant(null);
        });
    }

    @Test
    @DisplayName("Debe lanzar excepción si DNA está vacío")
    void testValidation_EmptyDna() {
        String[] dna = {};
        
        assertThrows(IllegalArgumentException.class, () -> {
            mutantDetector.isMutant(dna);
        });
    }

    @Test
    @DisplayName("Debe lanzar excepción si matriz no es NxN")
    void testValidation_NotSquareMatrix() {
        String[] dna = {
            "ATGC",
            "CAGTGC",  // Diferente tamaño
            "TTAT",
            "AGAC"
        };
        
        assertThrows(IllegalArgumentException.class, () -> {
            mutantDetector.isMutant(dna);
        });
    }

    @Test
    @DisplayName("Debe lanzar excepción si contiene caracteres inválidos")
    void testValidation_InvalidCharacters() {
        String[] dna = {
            "ATGC",
            "CXGT",  // X es inválido
            "TTAT",
            "AGAC"
        };
        
        assertThrows(IllegalArgumentException.class, () -> {
            mutantDetector.isMutant(dna);
        });
    }

    @Test
    @DisplayName("Debe lanzar excepción si contiene minúsculas")
    void testValidation_LowercaseCharacters() {
        String[] dna = {
            "ATGc",  // c en minúscula
            "CAGT",
            "TTAT",
            "AGAC"
        };
        
        assertThrows(IllegalArgumentException.class, () -> {
            mutantDetector.isMutant(dna);
        });
    }

    @Test
    @DisplayName("Debe lanzar excepción si una fila es null")
    void testValidation_NullRow() {
        String[] dna = {
            "ATGC",
            null,
            "TTAT",
            "AGAC"
        };
        
        assertThrows(IllegalArgumentException.class, () -> {
            mutantDetector.isMutant(dna);
        });
    }

    // ==================== TESTS PARAMETRIZADOS ====================

    @ParameterizedTest(name = "{index} - {2}")
    @MethodSource("provideMutantDnaSequences")
    @DisplayName("Tests parametrizados de mutantes")
    void testParameterized_Mutants(String[] dna, boolean expected, String description) {
        assertEquals(expected, mutantDetector.isMutant(dna));
    }

    static Stream<Arguments> provideMutantDnaSequences() {
        return Stream.of(
            Arguments.of(
                new String[]{"AAAA", "CCCC", "TATA", "TGTG"},
                true,
                "Mutante con 2 secuencias horizontales"
            ),
            Arguments.of(
                new String[]{"ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"},
                true,
                "Mutante ejemplo MercadoLibre"
            ),
            Arguments.of(
                new String[]{"ATGC", "CAGT", "TTAT", "AGAC"},
                false,
                "Humano sin secuencias"
            ),
            Arguments.of(
                new String[]{"ATGCGA", "CAGTGC", "TTATGT", "AGACGG", "CCCTTA", "TCACTG"},
                false,
                "Humano ejemplo MercadoLibre modificado"
            )
        );
    }

    // ==================== TESTS DE EDGE CASES ====================

    @Test
    @DisplayName("Debe manejar matriz grande 10x10")
    void testEdgeCase_LargeMatrix() {
        String[] dna = {
            "ATGCGATTTT",
            "CAGTGCAGTG",
            "TTATGTTTAT",
            "AGAAGGTATA",
            "CCCCTACCCC",
            "TCACTGACAG",
            "ATGCGATTTT",
            "CAGTGCAGTG",
            "TTATGTTTAT",
            "AGAAGGTATA"
        };
        
        // Debe ejecutarse sin errores
        assertDoesNotThrow(() -> mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe detectar mutante con todas las G")
    void testEdgeCase_AllSameCharacter() {
        String[] dna = {
            "GGGG",
            "GGGG",
            "GGGG",
            "GGGG"
        };
        
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe manejar secuencia mínima válida 4x4")
    void testEdgeCase_MinimumSize() {
        String[] dna = {
            "ATGC",
            "CAGT",
            "TTAT",
            "AGAC"  // Sin secuencias
        };
        
        assertFalse(mutantDetector.isMutant(dna)); // Solo 0 secuencias
    }
}
