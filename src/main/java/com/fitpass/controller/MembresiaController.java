package com.fitpass.controller;

import com.fitpass.dto.ApiResponse;
import com.fitpass.dto.ErrorResponseDto;
import com.fitpass.entity.Membresia;
import com.fitpass.entity.Usuario;
import com.fitpass.repository.MembresiaRepository;
import com.fitpass.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/membresias")
@CrossOrigin(origins = "*")
@Tag(name = "Membresías", description = "Asignación, confirmación de pagos y generación de pase QR")
@SecurityRequirement(name = "BearerAuth")
public class MembresiaController {

    private final MembresiaRepository membresiaRepository;
    private final UsuarioRepository usuarioRepository;

    public MembresiaController(
            MembresiaRepository membresiaRepository,
            UsuarioRepository usuarioRepository) {
        this.membresiaRepository = membresiaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/asignar")
    @Operation(
            summary = "Asignar membresía",
            description = "Genera una solicitud de membresía pendiente de pago para un usuario, evitando duplicados activos o pendientes."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Solicitud de membresía generada"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Tipo de pase inválido, usuario no encontrado o ya existe membresía activa/pendiente",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    public ResponseEntity<ApiResponse<Membresia>> asignarMembresia(
            @RequestParam Long usuarioId,
            @RequestParam String tipoPase) {

        try {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException(
                            "Usuario no encontrado con ID: " + usuarioId));

            LocalDate hoy = LocalDate.now();

            List<Membresia> existentes = membresiaRepository.findAll()
                    .stream()
                    .filter(m -> m.getUsuario() != null && m.getUsuario().getId().equals(usuarioId))
                    .toList();

            boolean tienePendiente = existentes.stream()
                    .anyMatch(m -> "PENDIENTE".equalsIgnoreCase(m.getEstadoPago()));

            if (tienePendiente) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                false,
                                "Ya cuentas con una solicitud de membresía pendiente de pago en recepción",
                                null
                        ));
            }

            boolean tieneActivaVigente = existentes.stream()
                    .anyMatch(m -> m.isActiva()
                            && "PAGADO".equalsIgnoreCase(m.getEstadoPago())
                            && m.getFechaFin() != null
                            && !m.getFechaFin().isBefore(hoy));

            if (tieneActivaVigente) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                false,
                                "El usuario ya tiene una membresía activa vigente",
                                null
                        ));
            }

            LocalDate fechaFin;
            Double precio;

            String tipoNormalizado = tipoPase.trim().toUpperCase();

            switch (tipoNormalizado) {
                case "VISITA":
                    fechaFin = hoy;
                    precio = 50.0;
                    break;
                case "SEMANAL":
                    fechaFin = hoy.plusDays(7);
                    precio = 150.0;
                    break;
                case "MENSUAL":
                    fechaFin = hoy.plusMonths(1);
                    precio = 450.0;
                    break;
                case "ANUAL":
                    fechaFin = hoy.plusYears(1);
                    precio = 4200.0;
                    break;
                default:
                    throw new RuntimeException("Tipo de pase no válido: " + tipoPase);
            }

            Membresia nueva = new Membresia(
                    usuario,
                    tipoNormalizado,
                    hoy,
                    fechaFin,
                    false
            );

            nueva.setPrecio(precio);
            nueva.setEstadoPago("PENDIENTE");

            Membresia guardada = membresiaRepository.save(nueva);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(
                            true,
                            "Solicitud generada. Realiza tu pago en recepción por $" + precio,
                            guardada
                    ));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PutMapping("/confirmar-pago/{membresiaId}")
    @Operation(summary = "Confirmar pago por ID de membresía", description = "Activa una membresía pendiente mediante su ID.")
    public ResponseEntity<ApiResponse<Membresia>> confirmarPagoMembresia(
            @PathVariable Long membresiaId) {

        Optional<Membresia> membresiaOpt =
                membresiaRepository.findById(membresiaId);

        if (membresiaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(
                            false,
                            "Membresía no encontrada con ID: " + membresiaId,
                            null
                    ));
        }

        Membresia membresia = membresiaOpt.get();

        if ("PAGADO".equalsIgnoreCase(membresia.getEstadoPago())
                && membresia.isActiva()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>(
                            false,
                            "Esta membresía ya tiene un pago confirmado",
                            null
                    ));
        }

        LocalDate fechaPago = LocalDate.now();
        LocalDate fechaFin;

        switch (membresia.getTipoPase().trim().toUpperCase()) {
            case "VISITA":
                fechaFin = fechaPago;
                break;
            case "SEMANAL":
                fechaFin = fechaPago.plusDays(7);
                break;
            case "MENSUAL":
                fechaFin = fechaPago.plusMonths(1);
                break;
            case "ANUAL":
                fechaFin = fechaPago.plusYears(1);
                break;
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                false,
                                "Tipo de membresía no válido",
                                null
                        ));
        }

        membresia.setFechaInicio(fechaPago);
        membresia.setFechaFin(fechaFin);
        membresia.setActiva(true);
        membresia.setEstadoPago("PAGADO");

        Membresia actualizada = membresiaRepository.save(membresia);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Pago confirmado. Membresía activada correctamente.",
                actualizada
        ));
    }

    @PutMapping("/confirmar-pago-usuario/{usuarioId}")
    @Operation(summary = "Confirmar pago por ID de usuario", description = "Activa la última membresía pendiente de un usuario.")
    public ResponseEntity<ApiResponse<Membresia>> confirmarPagoPorUsuarioId(
            @PathVariable Long usuarioId) {

        List<Membresia> membresias = membresiaRepository.findAll()
                .stream()
                .filter(m -> m.getUsuario() != null
                        && m.getUsuario().getId().equals(usuarioId))
                .sorted(Comparator.comparing(
                        Membresia::getId,
                        Comparator.nullsFirst(Comparator.naturalOrder())
                ))
                .toList();

        Optional<Membresia> pendiente = membresias.stream()
                .filter(m -> "PENDIENTE".equalsIgnoreCase(m.getEstadoPago()))
                .reduce((primera, ultima) -> ultima);

        if (pendiente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(
                            false,
                            "No se encontró una membresía pendiente para el usuario ID: " + usuarioId,
                            null
                    ));
        }

        Membresia membresia = pendiente.get();
        LocalDate fechaPago = LocalDate.now();
        LocalDate fechaFin;

        switch (membresia.getTipoPase().trim().toUpperCase()) {
            case "VISITA":
                fechaFin = fechaPago;
                break;
            case "SEMANAL":
                fechaFin = fechaPago.plusDays(7);
                break;
            case "MENSUAL":
                fechaFin = fechaPago.plusMonths(1);
                break;
            case "ANUAL":
                fechaFin = fechaPago.plusYears(1);
                break;
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                false,
                                "Tipo de membresía no válido",
                                null
                        ));
        }

        membresia.setFechaInicio(fechaPago);
        membresia.setFechaFin(fechaFin);
        membresia.setActiva(true);
        membresia.setEstadoPago("PAGADO");

        Membresia actualizada = membresiaRepository.save(membresia);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Pago confirmado para el usuario #" + usuarioId + ". QR activado.",
                actualizada
        ));
    }

    @PutMapping("/confirmar-pago-por-correo")
    @Operation(summary = "Confirmar pago por correo", description = "Activa la última membresía pendiente de un usuario buscando por email.")
    public ResponseEntity<ApiResponse<Membresia>> confirmarPagoPorCorreo(
            @RequestParam String email) {

        Optional<Usuario> usuarioOpt =
                usuarioRepository.findByEmail(email.trim());

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(
                            false,
                            "Usuario no encontrado con el correo: " + email,
                            null
                    ));
        }

        Usuario usuario = usuarioOpt.get();

        List<Membresia> membresias = membresiaRepository.findAll()
                .stream()
                .filter(m -> m.getUsuario() != null
                        && m.getUsuario().getId().equals(usuario.getId()))
                .sorted(Comparator.comparing(
                        Membresia::getId,
                        Comparator.nullsFirst(Comparator.naturalOrder())
                ))
                .toList();

        Optional<Membresia> pendiente = membresias.stream()
                .filter(m -> !"PAGADO".equalsIgnoreCase(m.getEstadoPago()))
                .reduce((primera, ultima) -> ultima);

        if (pendiente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(
                            false,
                            "No se encontró una membresía pendiente para este usuario",
                            null
                    ));
        }

        Membresia membresia = pendiente.get();
        LocalDate fechaPago = LocalDate.now();
        LocalDate fechaFin;

        switch (membresia.getTipoPase().trim().toUpperCase()) {
            case "VISITA":
                fechaFin = fechaPago;
                break;
            case "SEMANAL":
                fechaFin = fechaPago.plusDays(7);
                break;
            case "MENSUAL":
                fechaFin = fechaPago.plusMonths(1);
                break;
            case "ANUAL":
                fechaFin = fechaPago.plusYears(1);
                break;
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                false,
                                "Tipo de membresía no válido",
                                null
                        ));
        }

        membresia.setFechaInicio(fechaPago);
        membresia.setFechaFin(fechaFin);
        membresia.setActiva(true);
        membresia.setEstadoPago("PAGADO");

        Membresia actualizada = membresiaRepository.save(membresia);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Pago confirmado por correo. Membresía activada correctamente.",
                actualizada
        ));
    }


    @GetMapping("/generar-qr/{usuarioId}")
    @Operation(
            summary = "Generar pase QR",
            description = "Devuelve los datos del QR si el usuario tiene una membresía activa y pagada."
    )
    public ResponseEntity<ApiResponse<Map<String, Object>>> generarPaseQr(
            @PathVariable Long usuarioId) {

        Optional<Membresia> membresiaOpt = membresiaRepository
                .findFirstByUsuarioIdAndActivaTrueAndFechaFinGreaterThanEqualOrderByIdDesc(
                        usuarioId,
                        LocalDate.now()
                );

        if (membresiaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            false,
                            "No tienes una membresía activa vigente",
                            null
                    ));
        }

        Membresia membresia = membresiaOpt.get();

        if (!"PAGADO".equalsIgnoreCase(membresia.getEstadoPago())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            false,
                            "Debes liquidar tu membresía en recepción para obtener el Pase QR",
                            null
                    ));
        }

        Map<String, Object> qrPayload = new HashMap<>();
        qrPayload.put("usuarioId", usuarioId);
        qrPayload.put("membresiaId", membresia.getId());
        qrPayload.put("tipoPase", membresia.getTipoPase());
        qrPayload.put("fechaFin", membresia.getFechaFin());
        qrPayload.put("precio", membresia.getPrecio());
        qrPayload.put("estadoPago", membresia.getEstadoPago());

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "QR generado exitosamente",
                        qrPayload
                )
        );
    }


    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Obtener membresía actual", description = "Consulta la membresía más reciente registrada de un usuario.")
    public ResponseEntity<ApiResponse<Membresia>> obtenerMembresiaActivaUsuario(
            @PathVariable Long usuarioId) {

        List<Membresia> membresias = membresiaRepository.findAll()
                .stream()
                .filter(m -> m.getUsuario() != null
                        && m.getUsuario().getId().equals(usuarioId))
                .sorted(Comparator.comparing(
                        Membresia::getId,
                        Comparator.nullsFirst(Comparator.naturalOrder())
                ))
                .toList();

        if (membresias.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(
                            false,
                            "No se encontró una membresía registrada para este usuario",
                            null
                    ));
        }

        Membresia ultima = membresias.get(membresias.size() - 1);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Membresía recuperada con éxito",
                ultima
        ));
    }

    @GetMapping("/usuario/{usuarioId}/historial")
    @Operation(summary = "Historial de membresías", description = "Lista todas las membresías anteriores y actuales de un usuario.")
    public ResponseEntity<ApiResponse<List<Membresia>>> obtenerHistorialMembresias(
            @PathVariable Long usuarioId) {

        List<Membresia> membresias = membresiaRepository.findAll()
                .stream()
                .filter(m -> m.getUsuario() != null
                        && m.getUsuario().getId().equals(usuarioId))
                .sorted(Comparator.comparing(
                        Membresia::getId,
                        Comparator.nullsFirst(Comparator.naturalOrder())
                ).reversed())
                .toList();

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Historial de membresías recuperado",
                membresias
        ));
    }
}