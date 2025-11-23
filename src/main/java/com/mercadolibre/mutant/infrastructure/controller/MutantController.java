package com.mercadolibre.mutant.infrastructure.controller;

import com.mercadolibre.mutant.application.dto.DnaRequest;
import com.mercadolibre.mutant.application.dto.StatsResponse;
import com.mercadolibre.mutant.application.service.MutantService;
import com.mercadolibre.mutant.application.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para detección de mutantes
 * 
 * Endpoints:
 * - POST /mutant: Analiza ADN y retorna 200 (mutante) o 403 (humano)
 * - GET /stats: Retorna estadísticas de verificaciones
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Mutant Detector", description = "API para detección de mutantes mediante análisis de ADN")
public class MutantController {

    private final MutantService mutantService;
    private final StatsService statsService;

    /**
     * Endpoint POST /mutant
     * Analiza una secuencia de ADN y determina si pertenece a un mutante
     * 
     * @param request DTO con la secuencia de ADN
     * @return 200 OK si es mutante, 403 FORBIDDEN si es humano
     */
    @PostMapping(value = "/mutant", 
                 consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Detectar si un humano es mutante",
        description = "Analiza una secuencia de ADN (matriz NxN) y determina si pertenece a un mutante. " +
                      "Se considera mutante si se encuentran más de una secuencia de cuatro letras iguales " +
                      "(A, T, C, G) de forma horizontal, vertical u oblicua."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Es mutante - Se detectaron más de una secuencia de 4 letras iguales",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "403",
            description = "No es mutante - Es un humano normal",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos inválidos - Secuencia de ADN no cumple con el formato requerido",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Secuencia de ADN a analizar",
        required = true,
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = DnaRequest.class),
            examples = {
                @ExampleObject(
                    name = "Mutante",
                    description = "Ejemplo de secuencia mutante (2 secuencias horizontales)",
                    value = "{\"dna\":[\"ATGCGA\",\"CAGTGC\",\"TTATGT\",\"AGAAGG\",\"CCCCTA\",\"TCACTG\"]}"
                ),
                @ExampleObject(
                    name = "Humano",
                    description = "Ejemplo de secuencia humana (no mutante)",
                    value = "{\"dna\":[\"ATGCGA\",\"CAGTGC\",\"TTATGT\",\"AGAAGG\",\"CCCTTA\",\"TCACTG\"]}"
                )
            }
        )
    )
    public ResponseEntity<Void> isMutant(@Valid @RequestBody DnaRequest request) {
        log.info("POST /mutant - Analyzing DNA sequence of size: {}", request.getDna().length);
        
        boolean isMutant = mutantService.isMutant(request.getDna());
        
        if (isMutant) {
            log.info("Result: MUTANT detected");
            return ResponseEntity.ok().build();
        } else {
            log.info("Result: HUMAN (not mutant)");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Endpoint GET /stats
     * Retorna estadísticas de las verificaciones de ADN
     * 
     * @return JSON con contadores y ratio
     */
    @GetMapping(value = "/stats", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Obtener estadísticas de verificaciones",
        description = "Retorna estadísticas globales de las verificaciones de ADN realizadas, " +
                      "incluyendo cantidad de mutantes, humanos y el ratio entre ellos."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Estadísticas obtenidas exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = StatsResponse.class),
                examples = @ExampleObject(
                    name = "Estadísticas",
                    value = "{\"count_mutant_dna\":40,\"count_human_dna\":100,\"ratio\":0.4}"
                )
            )
        )
    })
    public ResponseEntity<StatsResponse> getStats() {
        log.info("GET /stats - Retrieving statistics");
        
        StatsResponse stats = statsService.getStats();
        
        log.info("Stats: Mutants={}, Humans={}, Ratio={}", 
                 stats.getCountMutantDna(), 
                 stats.getCountHumanDna(), 
                 stats.getRatio());
        
        return ResponseEntity.ok(stats);
    }
}
