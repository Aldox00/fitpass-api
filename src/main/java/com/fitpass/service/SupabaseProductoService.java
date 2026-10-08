package com.fitpass.service;

import com.fitpass.dto.CrearProductoRequest;
import com.fitpass.dto.ProductoDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class SupabaseProductoService {

    private final RestClient restClient;

    public SupabaseProductoService(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<ProductoDTO> listarTodos() {
        return restClient.get()
                .uri("/productos?select=*")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ProductoDTO>>() {});
    }

    public ProductoDTO obtenerPorId(Long id) {
        List<ProductoDTO> resultado = restClient.get()
                .uri("/productos?id=eq." + id + "&select=*")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ProductoDTO>>() {});

        if (resultado == null || resultado.isEmpty()) {
            throw new RuntimeException("Producto no encontrado con ID: " + id);
        }
        return resultado.get(0);
    }

    public ProductoDTO crear(CrearProductoRequest request) {
        List<ProductoDTO> resultado = restClient.post()
                .uri("/productos")
                .header("Prefer", "return=representation")
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<List<ProductoDTO>>() {});

        return (resultado != null && !resultado.isEmpty()) ? resultado.get(0) : null;
    }

    public ProductoDTO actualizar(Long id, CrearProductoRequest request) {
        obtenerPorId(id);

        List<ProductoDTO> resultado = restClient.patch()
                .uri("/productos?id=eq." + id)
                .header("Prefer", "return=representation")
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<List<ProductoDTO>>() {});

        return (resultado != null && !resultado.isEmpty()) ? resultado.get(0) : null;
    }

    public void eliminar(Long id) {
        obtenerPorId(id);

        restClient.delete()
                .uri("/productos?id=eq." + id)
                .retrieve()
                .toBodilessEntity();
    }

    public List<ProductoDTO> buscarPorNombre(String nombre) {
        return restClient.get()
                .uri("/productos?nombre=ilike.*" + nombre + "*&select=*")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ProductoDTO>>() {});
    }
}