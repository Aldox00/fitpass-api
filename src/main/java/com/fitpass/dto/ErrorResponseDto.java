package com.fitpass.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Respuesta estándar para errores HTTP (4xx y 5xx)")
public class ErrorResponseDto {

    @Schema(description = "Código HTTP de error", example = "400")
    private int status;

    @Schema(description = "Detalle del error ocurrido", example = "El usuario ya tiene una membresía activa vigente")
    private String mensaje;

    @Schema(description = "Ruta del endpoint solicitado", example = "/api/v1/membresias/asignar")
    private String ruta;

    @Schema(description = "Fecha y hora del error", example = "2026-11-20T20:30:00")
    private LocalDateTime timestamp;

    public ErrorResponseDto() {
    }

    public ErrorResponseDto(int status, String mensaje, String ruta, LocalDateTime timestamp) {
        this.status = status;
        this.mensaje = mensaje;
        this.ruta = ruta;
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}