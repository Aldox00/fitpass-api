package com.fitpass.controller;

import com.fitpass.dto.ApiResponse;
import com.fitpass.dto.LoginRequest;
import com.fitpass.dto.RegisterRequest;
import com.fitpass.dto.UsuarioResponse;
import com.fitpass.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
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
}