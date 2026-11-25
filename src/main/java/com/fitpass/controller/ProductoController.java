package com.fitpass.controller;

import com.fitpass.dto.ApiResponse;
import com.fitpass.dto.CrearProductoRequest;
import com.fitpass.dto.ErrorResponseDto;
import com.fitpass.entity.Producto;
import com.fitpass.repository.ProductoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@Tag(name = "Productos", description = "Gestión del catálogo y stock de la tienda FitPass")
@SecurityRequirement(name = "BearerAuth")
public class ProductoController {

    private final ProductoRepository productoRepository;

    public ProductoController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @GetMapping
    @Operation(
            summary = "Listar productos",
            description = "Obtiene todos los productos disponibles en el inventario."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Productos recuperados correctamente"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    public ResponseEntity<ApiResponse<List<Producto>>> obtenerProductos() {
        List<Producto> productos = productoRepository.findAll();
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Productos recuperados correctamente", productos)
        );
    }

    @PostMapping
    @Operation(
            summary = "Crear producto",
            description = "Registra un nuevo producto validando nombre único, precio positivo y stock no negativo."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Producto creado exitosamente"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o producto duplicado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    public ResponseEntity<ApiResponse<Producto>> crearProducto(@Valid @RequestBody CrearProductoRequest request) {
        String nombreLimpio = request.nombre().trim();

        if (request.precio() == null || request.precio().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            false,
                            "El precio del producto debe ser mayor a 0",
                            null
                    ));
        }

        if (request.stock() == null || request.stock() < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            false,
                            "El stock del producto no puede ser negativo",
                            null
                    ));
        }

        if (productoRepository.existsByNombreIgnoreCase(nombreLimpio)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            false,
                            "El producto '" + nombreLimpio + "' ya se encuentra registrado. Utiliza el método PUT para actualizar su stock o precio.",
                            null
                    ));
        }

        Producto nuevo = new Producto();
        nuevo.setId(null);
        nuevo.setNombre(nombreLimpio);
        nuevo.setPrecio(request.precio().doubleValue());
        nuevo.setStock(request.stock());

        Producto guardado = productoRepository.save(nuevo);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Producto creado exitosamente", guardado));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar producto",
            description = "Actualiza los datos y existencias de un producto existente por su ID."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Producto actualizado correctamente"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Producto no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    public ResponseEntity<ApiResponse<Producto>> actualizarProducto(
            @PathVariable Long id,
            @Valid @RequestBody CrearProductoRequest datos) {

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

        if (datos.precio() == null || datos.precio().compareTo(BigDecimal.ZERO) <= 0
                || datos.stock() == null || datos.stock() < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            false,
                            "Precio debe ser mayor a 0 y stock no puede ser negativo",
                            null
                    ));
        }

        producto.setNombre(datos.nombre().trim());
        producto.setPrecio(datos.precio().doubleValue());
        producto.setStock(datos.stock());

        Producto actualizado = productoRepository.save(producto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Producto actualizado correctamente", actualizado));
    }

    @PutMapping("/{id}/descontar-stock")
    @Operation(
            summary = "Descontar stock por venta",
            description = "Resta la cantidad vendida al stock actual del producto."
    )
    public ResponseEntity<ApiResponse<Producto>> descontarStock(
            @PathVariable Long id,
            @RequestParam int cantidad) {

        if (cantidad <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "La cantidad a descontar debe ser mayor a 0", null));
        }

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

        int stockActual = producto.getStock() != null ? producto.getStock() : 0;
        if (stockActual < cantidad) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            false,
                            "Stock insuficiente. Disponible: " + stockActual + ", solicitado: " + cantidad,
                            null
                    ));
        }

        producto.setStock(stockActual - cantidad);
        Producto actualizado = productoRepository.save(producto);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Stock descontado correctamente", actualizado)
        );
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar producto",
            description = "Elimina un producto del catálogo por su ID."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Producto eliminado exitosamente"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Producto no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    public ResponseEntity<ApiResponse<Void>> eliminarProducto(@PathVariable Long id) {
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado con ID: " + id);
        }
        productoRepository.deleteById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Producto eliminado exitosamente", null));
    }
}