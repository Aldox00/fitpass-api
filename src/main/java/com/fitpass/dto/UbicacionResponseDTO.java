package com.fitpass.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UbicacionResponseDTO(
        @JsonProperty("place_id") Long placeId,
        @JsonProperty("display_name") String direccionCompleta,
        @JsonProperty("lat") String latitud,
        @JsonProperty("lon") String longitud,
        String type,
        Double importance
) {}