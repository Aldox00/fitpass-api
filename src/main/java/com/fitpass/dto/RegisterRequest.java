package com.fitpass.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos para registrar una nueva cuenta en FitPass")
public class RegisterRequest {

        @Schema(description = "Nombre completo del usuario", example = "Aldo Arroyo", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "El nombre es obligatorio")
        private String nombre;

        @Schema(description = "Correo electrónico del usuario", example = "aldo@fitpass.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El formato del correo no es válido")
        private String email;

        @Schema(description = "Contraseña (mínimo 6 caracteres)", example = "ClaveSegura123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        private String password;

        @Schema(description = "Rol asignado al usuario", example = "USUARIO", allowableValues = {"USUARIO", "ADMIN", "RECEPCION"})
        private String rol;

        public RegisterRequest() {}

        public RegisterRequest(String nombre, String email, String password, String rol) {
                this.nombre = nombre;
                this.email = email;
                this.password = password;
                this.rol = rol;
        }

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getRol() { return rol; }
        public void setRol(String rol) { this.rol = rol; }
}