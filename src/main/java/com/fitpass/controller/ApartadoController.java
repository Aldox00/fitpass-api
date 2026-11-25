package com.fitpass.controller;

import com.fitpass.dto.ApartadoRequest;
import com.fitpass.dto.ApiResponse;
import com.fitpass.entity.Apartado;
import com.fitpass.entity.Producto;
import com.fitpass.repository.ApartadoRepository;
import com.fitpass.repository.ProductoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/apartados")
@Tag(name = "Apartados", description = "Gestión de productos apartados por socios para pago en recepción")
@SecurityRequirement(name = "BearerAuth")
public class ApartadoController {

    private final ApartadoRepository apartadoRepository;
    private final ProductoRepository productoRepository;

    public ApartadoController(ApartadoRepository apartadoRepository, ProductoRepository productoRepository) {
        this.apartadoRepository = apartadoRepository;
        this.productoRepository = productoRepository;
    }

    @GetMapping
    @Operation(summary = "Listar apartados pendientes", description = "Lista todos los apartados pendientes o los de un socio específico.")
    public ResponseEntity<ApiResponse<List<Apartado>>> listarPendientes(
            @RequestParam(required = false) Long usuarioId) {

        List<Apartado> lista = (usuarioId != null && usuarioId > 0)
                ? apartadoRepository.findByUsuarioIdAndEstadoOrderByCreadoEnDesc(usuarioId, "PENDIENTE")
                : apartadoRepository.findByEstadoOrderByCreadoEnDesc("PENDIENTE");

        return ResponseEntity.ok(new ApiResponse<>(true, "Apartados recuperados correctamente", lista));
    }

    @PostMapping
    @Operation(summary = "Crear o actualizar apartado", description = "Aparta un producto para un socio validando existencias.")
    public ResponseEntity<ApiResponse<Apartado>> crearApartado(@Valid @RequestBody ApartadoRequest request) {
        if (request.usuarioId() == null || request.usuarioId() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "ID de socio inválido", null));
        }

        Producto producto = productoRepository.findById(request.productoId())
                .orElse(null);

        if (producto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Producto no encontrado", null));
        }

        int stockDisponible = producto.getStock() != null ? producto.getStock() : 0;
        if (stockDisponible < request.cantidad()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Stock insuficiente. Disponible: " + stockDisponible, null));
        }

        String nombreSocio = (request.nombreUsuario() != null && !request.nombreUsuario().trim().isEmpty())
                ? request.nombreUsuario().trim()
                : "Socio #" + request.usuarioId();

        Apartado apartado = apartadoRepository
                .findByUsuarioIdAndProductoIdAndEstado(request.usuarioId(), request.productoId(), "PENDIENTE")
                .orElseGet(Apartado::new);

        apartado.setUsuarioId(request.usuarioId());
        apartado.setNombreUsuario(nombreSocio);
        apartado.setProductoId(producto.getId());
        apartado.setNombreProducto(producto.getNombre());
        apartado.setCantidad(request.cantidad());
        apartado.setPrecioUnitario(producto.getPrecio() != null ? producto.getPrecio() : 0.0);
        apartado.setEstado("PENDIENTE");
        if (apartado.getCreadoEn() == null) {
            apartado.setCreadoEn(LocalDateTime.now());
        }

        Apartado guardado = apartadoRepository.save(apartado);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Producto apartado correctamente", guardado));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar apartado", description = "Cancela un apartado pendiente por su ID.")
    public ResponseEntity<ApiResponse<Void>> cancelarApartado(@PathVariable Long id) {
        Apartado apartado = apartadoRepository.findById(id).orElse(null);
        if (apartado == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Apartado no encontrado", null));
        }
        apartado.setEstado("CANCELADO");
        apartadoRepository.save(apartado);
        return ResponseEntity.ok(new ApiResponse<>(true, "Apartado cancelado correctamente", null));
    }

    @PutMapping("/cobrar-socio/{usuarioId}")
    @Transactional
    @Operation(summary = "Cobrar y entregar apartados de un socio", description = "Descuenta el stock de todos los apartados pendientes del socio y los marca como cobrados.")
    public ResponseEntity<ApiResponse<List<Apartado>>> cobrarApartadosSocio(@PathVariable Long usuarioId) {
        List<Apartado> pendientes = apartadoRepository.findByUsuarioIdAndEstadoOrderByCreadoEnDesc(usuarioId, "PENDIENTE");
        if (pendientes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "El socio #" + usuarioId + " no tiene apartados pendientes", null));
        }

        for (Apartado item : pendientes) {
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + item.getNombreProducto()));

            int stockActual = producto.getStock() != null ? producto.getStock() : 0;
            if (stockActual < item.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para " + item.getNombreProducto() + " (disponible: " + stockActual + ")");
            }

            producto.setStock(stockActual - item.getCantidad());
            productoRepository.save(producto);

            item.setEstado("COBRADO");
            apartadoRepository.save(item);
        }

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Apartados cobrados y stock descontado correctamente", pendientes)
        );
    }
}