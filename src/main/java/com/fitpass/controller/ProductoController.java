package com.fitpass.controller;

import com.fitpass.dto.CrearProductoRequest;
import com.fitpass.dto.ProductoDTO;
import com.fitpass.service.SupabaseProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    private final SupabaseProductoService service;

    public ProductoController(SupabaseProductoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<ProductoDTO> crear(@Valid @RequestBody CrearProductoRequest request) {
        return new ResponseEntity<>(service.crear(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizar(@PathVariable Long id, @Valid @RequestBody CrearProductoRequest request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoDTO>> buscar(@RequestParam String nombre) {
        return ResponseEntity.ok(service.buscarPorNombre(nombre));
    }
}