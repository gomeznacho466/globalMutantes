package com.mercadolibre.mutant.application.service;

import com.mercadolibre.mutant.application.dto.StatsResponse;
import com.mercadolibre.mutant.domain.repository.DnaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para StatsService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("StatsService Tests")
class StatsServiceTest {

    @Mock
    private DnaRepository dnaRepository;

    @InjectMocks
    private StatsService statsService;

    @Test
    @DisplayName("Debe calcular estadísticas correctamente con mutantes y humanos")
    void testGetStats_WithMutantsAndHumans() {
        // Arrange
        when(dnaRepository.countMutants()).thenReturn(40L);
        when(dnaRepository.countHumans()).thenReturn(100L);

        // Act
        StatsResponse stats = statsService.getStats();

        // Assert
        assertNotNull(stats);
        assertEquals(40L, stats.getCountMutantDna());
        assertEquals(100L, stats.getCountHumanDna());
        assertEquals(0.4, stats.getRatio());

        verify(dnaRepository, times(1)).countMutants();
        verify(dnaRepository, times(1)).countHumans();
    }

    @Test
    @DisplayName("Debe retornar ratio 0 cuando no hay registros")
    void testGetStats_NoRecords() {
        // Arrange
        when(dnaRepository.countMutants()).thenReturn(0L);
        when(dnaRepository.countHumans()).thenReturn(0L);

        // Act
        StatsResponse stats = statsService.getStats();

        // Assert
        assertNotNull(stats);
        assertEquals(0L, stats.getCountMutantDna());
        assertEquals(0L, stats.getCountHumanDna());
        assertEquals(0.0, stats.getRatio());
    }

    @Test
    @DisplayName("Debe calcular ratio cuando solo hay mutantes")
    void testGetStats_OnlyMutants() {
        // Arrange
        when(dnaRepository.countMutants()).thenReturn(10L);
        when(dnaRepository.countHumans()).thenReturn(0L);

        // Act
        StatsResponse stats = statsService.getStats();

        // Assert
        assertNotNull(stats);
        assertEquals(10L, stats.getCountMutantDna());
        assertEquals(0L, stats.getCountHumanDna());
        assertEquals(10.0, stats.getRatio()); // Ratio cuando humanos = 0
    }

    @Test
    @DisplayName("Debe calcular ratio 0 cuando solo hay humanos")
    void testGetStats_OnlyHumans() {
        // Arrange
        when(dnaRepository.countMutants()).thenReturn(0L);
        when(dnaRepository.countHumans()).thenReturn(50L);

        // Act
        StatsResponse stats = statsService.getStats();

        // Assert
        assertNotNull(stats);
        assertEquals(0L, stats.getCountMutantDna());
        assertEquals(50L, stats.getCountHumanDna());
        assertEquals(0.0, stats.getRatio());
    }

    @Test
    @DisplayName("Debe calcular ratio 1.0 cuando hay igual cantidad")
    void testGetStats_EqualAmounts() {
        // Arrange
        when(dnaRepository.countMutants()).thenReturn(50L);
        when(dnaRepository.countHumans()).thenReturn(50L);

        // Act
        StatsResponse stats = statsService.getStats();

        // Assert
        assertNotNull(stats);
        assertEquals(50L, stats.getCountMutantDna());
        assertEquals(50L, stats.getCountHumanDna());
        assertEquals(1.0, stats.getRatio());
    }

    @Test
    @DisplayName("Debe redondear ratio correctamente a 2 decimales")
    void testGetStats_RatioRounding() {
        // Arrange
        when(dnaRepository.countMutants()).thenReturn(1L);
        when(dnaRepository.countHumans()).thenReturn(3L);

        // Act
        StatsResponse stats = statsService.getStats();

        // Assert
        assertNotNull(stats);
        assertEquals(0.33, stats.getRatio()); // 1/3 = 0.333... -> 0.33
    }

    @Test
    @DisplayName("Debe manejar números grandes correctamente")
    void testGetStats_LargeNumbers() {
        // Arrange
        when(dnaRepository.countMutants()).thenReturn(1000000L);
        when(dnaRepository.countHumans()).thenReturn(5000000L);

        // Act
        StatsResponse stats = statsService.getStats();

        // Assert
        assertNotNull(stats);
        assertEquals(1000000L, stats.getCountMutantDna());
        assertEquals(5000000L, stats.getCountHumanDna());
        assertEquals(0.2, stats.getRatio());
    }
}
