package com.fitpass.controller;

import com.fitpass.dto.ApiResponse;
import com.fitpass.dto.GimnasioRequest;
import com.fitpass.entity.Gimnasio;
import com.fitpass.service.GimnasioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/gimnasios")
public class GimnasioController {

    private final GimnasioService gimnasioService;

    public GimnasioController(GimnasioService gimnasioService) {
        this.gimnasioService = gimnasioService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Gimnasio>>> listarGimnasios(
            @RequestParam(required = false) String ciudad) {
        List<Gimnasio> lista;
        if (ciudad != null && !ciudad.isBlank()) {
            lista = gimnasioService.buscarPorCiudad(ciudad);
        } else {
            lista = gimnasioService.obtenerTodosActivos();
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Gimnasios recuperados exitosamente", lista));
    }

    @GetMapping("/cercanos")
    public ResponseEntity<ApiResponse<List<Gimnasio>>> obtenerCercanos(
            @RequestParam(required = false, defaultValue = "Suchiapa") String ciudad,
            @RequestParam(required = false) Double latitud,
            @RequestParam(required = false) Double longitud) {

        List<Gimnasio> cercanos = gimnasioService.buscarCercanos(ciudad, latitud, longitud);
        return ResponseEntity.ok(new ApiResponse<>(true, "Gimnasios cercanos encontrados correctamente", cercanos));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Gimnasio>> crearGimnasio(@Valid @RequestBody GimnasioRequest request) {
        Gimnasio nuevoGimnasio = gimnasioService.registrarGimnasio(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Gimnasio registrado correctamente", nuevoGimnasio));
    }
}