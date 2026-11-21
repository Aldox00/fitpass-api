package com.fitpass.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Estructura estándar de respuesta para endpoints de FitPass")
public class ApiResponse<T> {

    @Schema(description = "Indica si la operación fue exitosa", example = "true")
    private boolean exito;

    @Schema(description = "Mensaje descriptivo del resultado", example = "Operación realizada correctamente")
    private String mensaje;

    @Schema(description = "Datos devueltos por la operación")
    private T datos;

    public ApiResponse() {}

    public ApiResponse(boolean exito, String mensaje, T datos) {
        this.exito = exito;
        this.mensaje = mensaje;
        this.datos = datos;
    }

    public static <T> ApiResponse<T> ok(String mensaje, T datos) {
        return new ApiResponse<>(true, mensaje, datos);
    }

    public static <T> ApiResponse<T> error(String mensaje) {
        return new ApiResponse<>(false, mensaje, null);
    }

    public boolean isExito() { return exito; }
    public void setExito(boolean exito) { this.exito = exito; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public T getDatos() { return datos; }
    public void setDatos(T datos) { this.datos = datos; }
}