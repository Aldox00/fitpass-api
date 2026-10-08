package com.fitpass.dto;

import java.math.BigDecimal;

public record ProductoDTO(
        Long id,
        String nombre,
        BigDecimal precio,
        Integer stock
) {}