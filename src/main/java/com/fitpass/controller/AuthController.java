package com.fitpass.controller;

import com.fitpass.dto.ApiResponse;
import com.fitpass.dto.GoogleLoginRequest;
import com.fitpass.dto.LoginRequest;
import com.fitpass.dto.RegisterRequest;
import com.fitpass.dto.UsuarioResponse;
import com.fitpass.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UsuarioResponse>> registrar(@RequestBody RegisterRequest request) {
        UsuarioResponse usuario = usuarioService.registrar(request);
        return new ResponseEntity<>(ApiResponse.ok("Usuario registrado exitosamente", usuario), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UsuarioResponse>> login(@RequestBody LoginRequest request) {
        UsuarioResponse usuario = usuarioService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Inicio de sesión exitoso", usuario));
    }

    @PostMapping("/google")
    public ResponseEntity<ApiResponse<UsuarioResponse>> loginConGoogle(@RequestBody GoogleLoginRequest request) {
        UsuarioResponse usuario = usuarioService.loginORegistrarGoogle(request.getIdToken());
        return ResponseEntity.ok(ApiResponse.ok("Acceso con Google exitoso", usuario));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> solicitarRecuperacion(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        usuarioService.enviarCodigoRecuperacion(email);
        return ResponseEntity.ok(ApiResponse.ok("Código de recuperación enviado a tu correo", null));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> restablecerPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String codigo = request.get("codigo");
        String nuevaPassword = request.get("nuevaPassword");

        usuarioService.restablecerContrasena(email, codigo, nuevaPassword);
        return ResponseEntity.ok(ApiResponse.ok("Contraseña actualizada con éxito", null));
    }
}