package com.fitpass.controller;

import com.fitpass.dto.ApiResponse;
import com.fitpass.dto.CheckInRequest;
import com.fitpass.entity.Pase;
import com.fitpass.service.PaseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pases")
public class PaseController {

    private final PaseService paseService;

    public PaseController(PaseService paseService) {
        this.paseService = paseService;
    }

    @PostMapping("/checkin")
    public ResponseEntity<ApiResponse<Pase>> realizarCheckIn(@Valid @RequestBody CheckInRequest request) {
        Pase pase = paseService.registrarCheckIn(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Check-In realizado exitosamente en FitPass", pase));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<ApiResponse<List<Pase>>> obtenerHistorialUsuario(@PathVariable Long usuarioId) {
        List<Pase> historial = paseService.obtenerHistorialUsuario(usuarioId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Historial de pases recuperado correctamente", historial));
    }
}