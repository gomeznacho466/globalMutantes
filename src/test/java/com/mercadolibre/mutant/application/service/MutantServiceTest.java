package com.mercadolibre.mutant.application.service;

import com.mercadolibre.mutant.domain.detector.MutantDetector;
import com.mercadolibre.mutant.domain.entity.VerificationLog;
import com.mercadolibre.mutant.domain.repository.DnaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para MutantService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MutantService Tests")
class MutantServiceTest {

    @Mock
    private DnaRepository dnaRepository;

    @Mock
    private MutantDetector mutantDetector;

    @InjectMocks
    private MutantService mutantService;

    private String[] mutantDna;
    private String[] humanDna;

    @BeforeEach
    void setUp() {
        mutantDna = new String[]{
            "ATGCGA",
            "CAGTGC",
            "TTATGT",
            "AGAAGG",
            "CCCCTA",
            "TCACTG"
        };

        humanDna = new String[]{
            "ATGCGA",
            "CAGTGC",
            "TTATGT",
            "AGAAGG",
            "CCCTTA",
            "TCACTG"
        };
    }

    @Test
    @DisplayName("Debe analizar y guardar ADN mutante nuevo")
    void testIsMutant_NewMutantDna() {
        // Arrange
        when(dnaRepository.findById(anyString())).thenReturn(Optional.empty());
        when(mutantDetector.isMutant(mutantDna)).thenReturn(true);
        when(dnaRepository.save(any(VerificationLog.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        boolean result = mutantService.isMutant(mutantDna);

        // Assert
        assertTrue(result);
        verify(dnaRepository, times(1)).findById(anyString());
        verify(mutantDetector, times(1)).isMutant(mutantDna);
        verify(dnaRepository, times(1)).save(any(VerificationLog.class));

        // Verificar que se guardó correctamente
        ArgumentCaptor<VerificationLog> captor = ArgumentCaptor.forClass(VerificationLog.class);
        verify(dnaRepository).save(captor.capture());
        VerificationLog saved = captor.getValue();
        assertTrue(saved.getIsMutant());
        assertEquals(6, saved.getSequenceSize());
        assertNotNull(saved.getDnaHash());
    }

    @Test
    @DisplayName("Debe analizar y guardar ADN humano nuevo")
    void testIsMutant_NewHumanDna() {
        // Arrange
        when(dnaRepository.findById(anyString())).thenReturn(Optional.empty());
        when(mutantDetector.isMutant(humanDna)).thenReturn(false);
        when(dnaRepository.save(any(VerificationLog.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        boolean result = mutantService.isMutant(humanDna);

        // Assert
        assertFalse(result);
        verify(mutantDetector, times(1)).isMutant(humanDna);
        verify(dnaRepository, times(1)).save(any(VerificationLog.class));

        ArgumentCaptor<VerificationLog> captor = ArgumentCaptor.forClass(VerificationLog.class);
        verify(dnaRepository).save(captor.capture());
        VerificationLog saved = captor.getValue();
        assertFalse(saved.getIsMutant());
    }

    @Test
    @DisplayName("Debe retornar resultado desde caché sin analizar nuevamente")
    void testIsMutant_CachedResult() {
        // Arrange
        VerificationLog cachedRecord = VerificationLog.builder()
                .dnaHash("test-hash")
                .isMutant(true)
                .sequenceSize(6)
                .build();

        when(dnaRepository.findById(anyString())).thenReturn(Optional.of(cachedRecord));

        // Act
        boolean result = mutantService.isMutant(mutantDna);

        // Assert
        assertTrue(result);
        verify(dnaRepository, times(1)).findById(anyString());
        verify(mutantDetector, never()).isMutant(any()); // No debe llamar al detector
        verify(dnaRepository, never()).save(any()); // No debe guardar
    }

    @Test
    @DisplayName("Debe generar hash único para la misma secuencia")
    void testGenerateDnaHash_SameSequence() {
        // Arrange
        when(dnaRepository.findById(anyString())).thenReturn(Optional.empty());
        when(mutantDetector.isMutant(any())).thenReturn(true);
        when(dnaRepository.save(any(VerificationLog.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        mutantService.isMutant(mutantDna);
        mutantService.isMutant(mutantDna);

        // Assert
        ArgumentCaptor<String> hashCaptor = ArgumentCaptor.forClass(String.class);
        verify(dnaRepository, times(2)).findById(hashCaptor.capture());
        
        // Los dos hash deben ser iguales
        assertEquals(hashCaptor.getAllValues().get(0), hashCaptor.getAllValues().get(1));
    }

    @Test
    @DisplayName("Debe generar hash diferente para secuencias diferentes")
    void testGenerateDnaHash_DifferentSequences() {
        // Arrange
        when(dnaRepository.findById(anyString())).thenReturn(Optional.empty());
        when(mutantDetector.isMutant(any())).thenReturn(true);
        when(dnaRepository.save(any(VerificationLog.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        mutantService.isMutant(mutantDna);
        mutantService.isMutant(humanDna);

        // Assert
        ArgumentCaptor<String> hashCaptor = ArgumentCaptor.forClass(String.class);
        verify(dnaRepository, times(2)).findById(hashCaptor.capture());
        
        // Los dos hash deben ser diferentes
        assertNotEquals(hashCaptor.getAllValues().get(0), hashCaptor.getAllValues().get(1));
    }

    @Test
    @DisplayName("Debe generar hash de 64 caracteres (SHA-256)")
    void testGenerateDnaHash_Length() {
        // Arrange
        when(dnaRepository.findById(anyString())).thenReturn(Optional.empty());
        when(mutantDetector.isMutant(any())).thenReturn(true);
        when(dnaRepository.save(any(VerificationLog.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        mutantService.isMutant(mutantDna);

        // Assert
        ArgumentCaptor<VerificationLog> captor = ArgumentCaptor.forClass(VerificationLog.class);
        verify(dnaRepository).save(captor.capture());
        
        assertEquals(64, captor.getValue().getDnaHash().length());
    }
}
