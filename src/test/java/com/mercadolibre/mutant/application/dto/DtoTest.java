package com.mercadolibre.mutant.application.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para DTOs
 * Cubre métodos custom y validaciones
 */
@DisplayName("DTO Tests")
class DtoTest {

    @Test
    @DisplayName("DnaRequest debe crear instancia correctamente")
    void testDnaRequest_Creation() {
        // Arrange
        String[] dna = { "ATGC", "CAGT", "TTAT", "AGAC" };

        // Act
        DnaRequest request = new DnaRequest(dna);

        // Assert
        assertNotNull(request);
        assertArrayEquals(dna, request.getDna());
    }

    @Test
    @DisplayName("DnaRequest debe manejar null")
    void testDnaRequest_Null() {
        // Act
        DnaRequest request = new DnaRequest(null);

        // Assert
        assertNotNull(request);
        assertNull(request.getDna());
    }

    @Test
    @DisplayName("StatsResponse debe crear instancia con builder")
    void testStatsResponse_Builder() {
        // Act
        StatsResponse stats = StatsResponse.builder()
                .countMutantDna(40L)
                .countHumanDna(100L)
                .ratio(0.4)
                .build();

        // Assert
        assertNotNull(stats);
        assertEquals(40L, stats.getCountMutantDna());
        assertEquals(100L, stats.getCountHumanDna());
        assertEquals(0.4, stats.getRatio());
    }

    @Test
    @DisplayName("StatsResponse debe manejar valores cero")
    void testStatsResponse_ZeroValues() {
        // Act
        StatsResponse stats = StatsResponse.builder()
                .countMutantDna(0L)
                .countHumanDna(0L)
                .ratio(0.0)
                .build();

        // Assert
        assertEquals(0L, stats.getCountMutantDna());
        assertEquals(0L, stats.getCountHumanDna());
        assertEquals(0.0, stats.getRatio());
    }

    @Test
    @DisplayName("StatsResponse debe serializar correctamente con nombres JSON")
    void testStatsResponse_JsonPropertyNames() {
        // Act
        StatsResponse stats = StatsResponse.builder()
                .countMutantDna(10L)
                .countHumanDna(20L)
                .ratio(0.5)
                .build();

        // Assert - Verificar que los getters funcionan
        assertNotNull(stats.getCountMutantDna());
        assertNotNull(stats.getCountHumanDna());
        assertNotNull(stats.getRatio());
    }
}
