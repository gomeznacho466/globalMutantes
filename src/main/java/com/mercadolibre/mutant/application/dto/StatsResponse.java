package com.mercadolibre.mutant.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para las estadísticas de análisis
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Estadísticas de verificaciones de ADN")
public class StatsResponse {

    @JsonProperty("count_mutant_dna")
    @Schema(description = "Cantidad de ADN mutante detectado", example = "40")
    private Long countMutantDna;

    @JsonProperty("count_human_dna")
    @Schema(description = "Cantidad de ADN humano (no mutante) detectado", example = "100")
    private Long countHumanDna;

    @JsonProperty("ratio")
    @Schema(description = "Ratio de mutantes sobre humanos", example = "0.4")
    private Double ratio;
}
