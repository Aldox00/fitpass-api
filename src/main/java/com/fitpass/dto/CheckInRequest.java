package com.fitpass.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Solicitud para registrar el acceso (check-in) de un usuario a un gimnasio")
public class CheckInRequest {

    @Schema(description = "ID del usuario que realiza el check-in", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    @Schema(description = "ID del gimnasio destino", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El ID del gimnasio es obligatorio")
    private Long gimnasioId;

    public CheckInRequest() {}

    public CheckInRequest(Long usuarioId, Long gimnasioId) {
        this.usuarioId = usuarioId;
        this.gimnasioId = gimnasioId;
    }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Long getGimnasioId() { return gimnasioId; }
    public void setGimnasioId(Long gimnasioId) { this.gimnasioId = gimnasioId; }
}