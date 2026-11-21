package com.fitpass.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Solicitud de inicio de sesión mediante token de Google")
public class GoogleLoginRequest {

    @Schema(description = "ID Token emitido por Google Sign-In", example = "eyJhbGciOiJSUzI1NiIsImtpZCI6...", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El idToken es obligatorio")
    private String idToken;

    public GoogleLoginRequest() {}

    public GoogleLoginRequest(String idToken) {
        this.idToken = idToken;
    }

    public String getIdToken() { return idToken; }
    public void setIdToken(String idToken) { this.idToken = idToken; }
}