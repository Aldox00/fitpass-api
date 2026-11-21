package com.fitpass.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Datos para registrar o actualizar un gimnasio")
public class GimnasioRequest {

    @Schema(description = "Nombre del gimnasio", example = "FitPass Centro", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El nombre del gimnasio es obligatorio")
    private String nombre;

    @Schema(description = "Dirección física del gimnasio", example = "Av. Central Norte #120", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @Schema(description = "Ciudad donde se ubica el gimnasio", example = "Tuxtla Gutiérrez")
    private String ciudad;

    @Schema(description = "Latitud geográfica", example = "16.7516", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "La latitud es obligatoria")
    private Double latitud;

    @Schema(description = "Longitud geográfica", example = "-93.1161", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "La longitud es obligatoria")
    private Double longitud;

    public GimnasioRequest() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }

    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }
}