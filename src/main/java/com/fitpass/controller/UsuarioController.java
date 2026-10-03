package com.fitpass.controller;

import com.fitpass.dto.ApiResponse;
import com.fitpass.dto.RegisterRequest;
import com.fitpass.dto.UsuarioResponse;
import com.fitpass.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UsuarioResponse>>> obtenerTodos() {
        return ResponseEntity.ok(ApiResponse.ok("Lista de usuarios obtenida", usuarioService.obtenerTodos()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Usuario encontrado", usuarioService.obtenerPorId(id)));
    }

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<List<UsuarioResponse>>> buscar(@RequestParam String q) {
        return ResponseEntity.ok(ApiResponse.ok("Resultados de la búsqueda", usuarioService.buscarPorNombreOEmail(q)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponse>> actualizar(@PathVariable Long id, @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Usuario actualizado", usuarioService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok("Usuario eliminado correctamente", null));
    }
}