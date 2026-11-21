
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
import java.util.Map;

@RestController
@RequestMapping("/api/v1/pases")
public class PaseController {

    private final PaseService paseService;

    public PaseController(PaseService paseService) {
        this.paseService = paseService;
    }

    @PostMapping("/checkin")
    public ResponseEntity<ApiResponse<Pase>> realizarCheckIn(
            @Valid @RequestBody CheckInRequest request) {

        try {
            Pase pase = paseService.registrarCheckIn(request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(
                            true,
                            "Check-In realizado exitosamente en FitPass",
                            pase
                    ));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            false,
                            e.getMessage(),
                            null
                    ));
        }
    }

    @PostMapping("/checkout/{usuarioId}")
    public ResponseEntity<ApiResponse<Pase>> realizarCheckOut(
            @PathVariable Long usuarioId) {

        try {
            Pase pase = paseService.registrarCheckOut(usuarioId);

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Check-Out realizado exitosamente en FitPass. ¡Hasta luego!",
                            pase
                    )
            );

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            false,
                            e.getMessage(),
                            null
                    ));
        }
    }

    @GetMapping("/gimnasio/{gimnasioId}/reporte-hoy")
    public ResponseEntity<ApiResponse<Map<String, Object>>> obtenerReporteDiario(
            @PathVariable Long gimnasioId) {

        try {
            Map<String, Object> reporte =
                    paseService.obtenerReporteDiarioGimnasio(gimnasioId);

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Reporte diario generado exitosamente",
                            reporte
                    )
            );

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            false,
                            e.getMessage(),
                            null
                    ));
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<ApiResponse<List<Pase>>> obtenerHistorialUsuario(
            @PathVariable Long usuarioId) {

        try {
            List<Pase> historial =
                    paseService.obtenerHistorialUsuario(usuarioId);

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Historial de pases recuperado correctamente",
                            historial
                    )
            );

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            false,
                            e.getMessage(),
                            null
                    ));
        }
    }
}
