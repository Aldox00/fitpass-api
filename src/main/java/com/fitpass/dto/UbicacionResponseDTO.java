package com.fitpass.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta de geocodificación obtenida desde el servicio de mapas")
public record UbicacionResponseDTO(
        @Schema(description = "Identificador del lugar", example = "987654321")
        @JsonProperty("place_id") Long placeId,

        @Schema(description = "Dirección completa devuelta por el proveedor", example = "Villa Corzo, Chiapas, México")
        @JsonProperty("display_name") String direccionCompleta,

        @Schema(description = "Latitud en texto", example = "16.1833")
        @JsonProperty("lat") String latitud,

        @Schema(description = "Longitud en texto", example = "-93.2667")
        @JsonProperty("lon") String longitud,

        @Schema(description = "Tipo de ubicación", example = "administrative")
        String type,

        @Schema(description = "Relevancia del resultado", example = "0.65")
        Double importance
) {}