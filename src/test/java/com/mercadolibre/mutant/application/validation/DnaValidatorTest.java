package com.mercadolibre.mutant.application.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jakarta.validation.ConstraintValidatorContext;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para DnaValidator
 */
@DisplayName("DnaValidator Tests")
class DnaValidatorTest {

    private DnaValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new DnaValidator();
        context = mock(ConstraintValidatorContext.class);
        
        ConstraintValidatorContext.ConstraintViolationBuilder builder = 
            mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
    }

    @Test
    @DisplayName("Debe validar DNA correcto")
    void testValid_CorrectDna() {
        String[] dna = {
            "ATGC",
            "CAGT",
            "TTAT",
            "AGAC"
        };

        assertTrue(validator.isValid(dna, context));
    }

    @Test
    @DisplayName("Debe rechazar DNA null")
    void testInvalid_NullDna() {
        assertFalse(validator.isValid(null, context));
    }

    @Test
    @DisplayName("Debe rechazar DNA vacío")
    void testInvalid_EmptyDna() {
        String[] dna = {};
        assertFalse(validator.isValid(dna, context));
    }

    @Test
    @DisplayName("Debe rechazar matriz no cuadrada")
    void testInvalid_NonSquareMatrix() {
        String[] dna = {
            "ATGC",
            "CAGTGC",  // Diferente tamaño
            "TTAT",
            "AGAC"
        };

        assertFalse(validator.isValid(dna, context));
    }

    @Test
    @DisplayName("Debe rechazar caracteres inválidos")
    void testInvalid_InvalidCharacters() {
        String[] dna = {
            "ATGC",
            "CXGT",  // X es inválido
            "TTAT",
            "AGAC"
        };

        assertFalse(validator.isValid(dna, context));
    }

    @Test
    @DisplayName("Debe rechazar minúsculas")
    void testInvalid_LowercaseCharacters() {
        String[] dna = {
            "atgc",  // Minúsculas
            "CAGT",
            "TTAT",
            "AGAC"
        };

        assertFalse(validator.isValid(dna, context));
    }

    @Test
    @DisplayName("Debe rechazar fila null")
    void testInvalid_NullRow() {
        String[] dna = {
            "ATGC",
            null,
            "TTAT",
            "AGAC"
        };

        assertFalse(validator.isValid(dna, context));
    }

    @ParameterizedTest(name = "{index} - {1}")
    @MethodSource("provideInvalidDnaSequences")
    @DisplayName("Tests parametrizados de validaciones")
    void testParameterized_InvalidDna(String[] dna, String description) {
        assertFalse(validator.isValid(dna, context));
    }

    static Stream<Arguments> provideInvalidDnaSequences() {
        return Stream.of(
            Arguments.of(null, "DNA null"),
            Arguments.of(new String[]{}, "DNA vacío"),
            Arguments.of(new String[]{"ATGC", "CA", "TT", "AG"}, "Matriz no cuadrada"),
            Arguments.of(new String[]{"ATGC", "CXGT", "TTAT", "AGAC"}, "Caracteres inválidos"),
            Arguments.of(new String[]{"atgc", "cagt", "ttat", "agac"}, "Minúsculas"),
            Arguments.of(new String[]{"ATGC", null, "TTAT", "AGAC"}, "Fila null"),
            Arguments.of(new String[]{"ATGC", "CAGT", "TTAT", "AGA1"}, "Números"),
            Arguments.of(new String[]{"AT GC", "CAGT", "TTAT", "AGAC"}, "Espacios")
        );
    }
}
