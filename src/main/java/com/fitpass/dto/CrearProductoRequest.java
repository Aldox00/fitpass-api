package com.fitpass.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CrearProductoRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
        BigDecimal precio,

        @NotNull(message = "El stock es obligatorio")
        @Min(value = 0, message = "El stock no puede ser negativo")
        Integer stock
) {}