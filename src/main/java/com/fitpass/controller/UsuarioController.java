
package com.fitpass.controller;

import com.fitpass.dto.ApiResponse;
import com.fitpass.entity.Pase;
import com.fitpass.entity.Usuario;
import com.fitpass.repository.UsuarioRepository;
import com.fitpass.service.PaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final PaseService paseService;

    public UsuarioController(
            UsuarioRepository usuarioRepository,
            PaseService paseService) {
        this.usuarioRepository = usuarioRepository;
        this.paseService = paseService;
    }

    @GetMapping("/perfil/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> obtenerPerfil(
            @PathVariable Long id) {

        Usuario usuario = usuarioRepository.findById(id).orElse(null);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(
                            false,
                            "Usuario no encontrado con ID: " + id,
                            null
                    ));
        }

        List<Pase> pases = paseService.obtenerHistorialUsuario(id);

        Map<String, Object> perfilData = new HashMap<>();
        perfilData.put("usuario", usuario);
        perfilData.put("totalCheckIns", pases.size());
        perfilData.put(
                "ultimoCheckIn",
                pases.isEmpty() ? "Sin registro" : pases.get(0).getFechaCheckIn()
        );
        perfilData.put("historial", pases);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Perfil del usuario cargado exitosamente",
                        perfilData
                )
        );
    }

    @PutMapping("/hacer-admin")
    public ResponseEntity<ApiResponse<Usuario>> promoverAdmin(
            @RequestParam String email) {

        try {
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Usuario no encontrado con ese correo"
                            )
                    );

            usuario.setRol("ADMIN");
            Usuario actualizado = usuarioRepository.save(usuario);

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Usuario promovido a Administrador con éxito",
                            actualizado
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            false,
                            e.getMessage(),
                            null
                    ));
        }
    }
}
