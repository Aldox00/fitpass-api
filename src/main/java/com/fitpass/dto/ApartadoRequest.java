package com.fitpass.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ApartadoRequest(
        @NotNull(message = "El ID del usuario es obligatorio")
        Long usuarioId,

        String nombreUsuario,

        @NotNull(message = "El ID del producto es obligatorio")
        Long productoId,

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        Integer cantidad
) {}