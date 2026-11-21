package com.fitpass.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Información de ubicación formateada de un gimnasio")
public class UbicacionGimnasioDto {

    @Schema(description = "Nombre del gimnasio", example = "FitPass Norte")
    private String nombreGimnasio;

    @Schema(description = "Latitud", example = "16.7685")
    private String latitud;

    @Schema(description = "Longitud", example = "-93.1204")
    private String longitud;

    @Schema(description = "Dirección formateada", example = "Blvd. Belisario Domínguez #2400")
    private String direccionFormateada;

    public UbicacionGimnasioDto(String nombreGimnasio, String latitud, String longitud, String direccionFormateada) {
        this.nombreGimnasio = nombreGimnasio;
        this.latitud = latitud;
        this.longitud = longitud;
        this.direccionFormateada = direccionFormateada;
    }

    public String getNombreGimnasio() { return nombreGimnasio; }
    public String getLatitud() { return latitud; }
    public String getLongitud() { return longitud; }
    public String getDireccionFormateada() { return direccionFormateada; }
}