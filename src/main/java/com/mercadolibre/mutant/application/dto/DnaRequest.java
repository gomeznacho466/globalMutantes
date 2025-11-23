package com.mercadolibre.mutant.application.dto;

import com.mercadolibre.mutant.application.validation.ValidDna;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para la solicitud de análisis de ADN
 * Incluye validación customizada @ValidDna
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud de análisis de ADN")
public class DnaRequest {

    @NotNull(message = "DNA sequence cannot be null")
    @ValidDna
    @Schema(
        description = "Array de strings representando cada fila de la secuencia de ADN (NxN). Solo caracteres A, T, C, G permitidos.",
        example = "[\"ATGCGA\",\"CAGTGC\",\"TTATGT\",\"AGAAGG\",\"CCCCTA\",\"TCACTG\"]",
        required = true
    )
    private String[] dna;
}
