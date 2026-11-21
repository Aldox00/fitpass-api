package com.fitpass.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Representación resumida de un producto")
public record ProductoDTO(
        @Schema(description = "Identificador del producto", example = "1")
        Long id,

        @Schema(description = "Nombre del producto", example = "Proteína Whey")
        String nombre,

        @Schema(description = "Precio unitario", example = "699.00")
        BigDecimal precio,

        @Schema(description = "Unidades disponibles en inventario", example = "15")
        Integer stock
) {}