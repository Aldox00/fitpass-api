package com.fitpass.dto;

import jakarta.validation.constraints.NotNull;

public class CheckInRequest {

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El ID del gimnasio es obligatorio")
    private Long gimnasioId;

    public CheckInRequest() {}

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Long getGimnasioId() { return gimnasioId; }
    public void setGimnasioId(Long gimnasioId) { this.gimnasioId = gimnasioId; }
}