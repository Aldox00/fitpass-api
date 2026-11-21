package com.fitpass.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos públicos del usuario autenticado o consultado")
public class UsuarioResponse {

    @Schema(description = "ID único del usuario", example = "1")
    private Long id;

    @Schema(description = "Nombre del usuario", example = "Aldo Arroyo")
    private String nombre;

    @Schema(description = "Correo electrónico", example = "aldo@fitpass.com")
    private String email;

    @Schema(description = "Rol del usuario en el sistema", example = "USUARIO")
    private String rol;

    public UsuarioResponse() {}

    public UsuarioResponse(Long id, String nombre, String email, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}