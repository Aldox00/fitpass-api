package com.fitpass.dto;

public class UbicacionGimnasioDto {
    private String nombreGimnasio;
    private String latitud;
    private String longitud;
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