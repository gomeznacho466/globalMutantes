package com.mercadolibre.mutant.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.mutant.application.dto.DnaRequest;
import com.mercadolibre.mutant.application.dto.StatsResponse;
import com.mercadolibre.mutant.application.service.MutantService;
import com.mercadolibre.mutant.application.service.StatsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para MutantController
 */
@WebMvcTest(MutantController.class)
@DisplayName("MutantController Integration Tests")
class MutantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MutantService mutantService;

    @MockBean
    private StatsService statsService;

    @Test
    @DisplayName("POST /mutant debe retornar 200 OK para mutante")
    void testMutantEndpoint_ReturnOkForMutant() throws Exception {
        // Arrange
        String[] dna = {
            "ATGCGA",
            "CAGTGC",
            "TTATGT",
            "AGAAGG",
            "CCCCTA",
            "TCACTG"
        };
        DnaRequest request = new DnaRequest(dna);

        when(mutantService.isMutant(any(String[].class))).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(mutantService, times(1)).isMutant(any(String[].class));
    }

    @Test
    @DisplayName("POST /mutant debe retornar 403 FORBIDDEN para humano")
    void testMutantEndpoint_ReturnForbiddenForHuman() throws Exception {
        // Arrange
        String[] dna = {
            "ATGCGA",
            "CAGTGC",
            "TTATGT",
            "AGAAGG",
            "CCCTTA",
            "TCACTG"
        };
        DnaRequest request = new DnaRequest(dna);

        when(mutantService.isMutant(any(String[].class))).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(mutantService, times(1)).isMutant(any(String[].class));
    }

    @Test
    @DisplayName("POST /mutant debe retornar 400 BAD REQUEST para DNA null")
    void testMutantEndpoint_ReturnBadRequestForNullDna() throws Exception {
        // Arrange
        DnaRequest request = new DnaRequest(null);

        // Act & Assert
        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(mutantService, never()).isMutant(any());
    }

    @Test
    @DisplayName("POST /mutant debe retornar 400 BAD REQUEST para DNA vacío")
    void testMutantEndpoint_ReturnBadRequestForEmptyDna() throws Exception {
        // Arrange
        String[] dna = {};
        DnaRequest request = new DnaRequest(dna);

        // Act & Assert
        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(mutantService, never()).isMutant(any());
    }

    @Test
    @DisplayName("POST /mutant debe retornar 400 BAD REQUEST para matriz no cuadrada")
    void testMutantEndpoint_ReturnBadRequestForNonSquareMatrix() throws Exception {
        // Arrange
        String[] dna = {
            "ATGC",
            "CAGTGC",  // Diferente tamaño
            "TTAT"
        };
        DnaRequest request = new DnaRequest(dna);

        // Act & Assert
        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(mutantService, never()).isMutant(any());
    }

    @Test
    @DisplayName("POST /mutant debe retornar 400 BAD REQUEST para caracteres inválidos")
    void testMutantEndpoint_ReturnBadRequestForInvalidCharacters() throws Exception {
        // Arrange
        String[] dna = {
            "ATGC",
            "CXGT",  // X es inválido
            "TTAT",
            "AGAC"
        };
        DnaRequest request = new DnaRequest(dna);

        // Act & Assert
        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(mutantService, never()).isMutant(any());
    }

    @Test
    @DisplayName("GET /stats debe retornar estadísticas correctamente")
    void testStatsEndpoint_ReturnStats() throws Exception {
        // Arrange
        StatsResponse stats = StatsResponse.builder()
                .countMutantDna(40L)
                .countHumanDna(100L)
                .ratio(0.4)
                .build();

        when(statsService.getStats()).thenReturn(stats);

        // Act & Assert
        mockMvc.perform(get("/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count_mutant_dna").value(40))
                .andExpect(jsonPath("$.count_human_dna").value(100))
                .andExpect(jsonPath("$.ratio").value(0.4));

        verify(statsService, times(1)).getStats();
    }

    @Test
    @DisplayName("GET /stats debe retornar estadísticas vacías")
    void testStatsEndpoint_ReturnEmptyStats() throws Exception {
        // Arrange
        StatsResponse stats = StatsResponse.builder()
                .countMutantDna(0L)
                .countHumanDna(0L)
                .ratio(0.0)
                .build();

        when(statsService.getStats()).thenReturn(stats);

        // Act & Assert
        mockMvc.perform(get("/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count_mutant_dna").value(0))
                .andExpect(jsonPath("$.count_human_dna").value(0))
                .andExpect(jsonPath("$.ratio").value(0.0));

        verify(statsService, times(1)).getStats();
    }
}
