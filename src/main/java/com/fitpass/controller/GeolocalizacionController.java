package com.fitpass.controller;

import com.fitpass.dto.UbicacionResponseDTO;
import com.fitpass.service.GeolocalizacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/geolocalizacion")
public class GeolocalizacionController {

    private final GeolocalizacionService service;

    public GeolocalizacionController(GeolocalizacionService service) {
        this.service = service;
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<UbicacionResponseDTO>> buscar(@RequestParam String direccion) {
        List<UbicacionResponseDTO> resultados = service.buscarPorDireccion(direccion);
        if (resultados.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/reversa")
    public ResponseEntity<UbicacionResponseDTO> reversa(
            @RequestParam Double latitud,
            @RequestParam Double longitud) {
        UbicacionResponseDTO resultado = service.buscarPorCoordenadas(latitud, longitud);
        if (resultado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/gimnasios")
    public ResponseEntity<List<UbicacionResponseDTO>> buscarGimnasios(@RequestParam String ciudad) {
        List<UbicacionResponseDTO> gimnasios = service.buscarGimnasiosPorCiudad(ciudad);
        return ResponseEntity.ok(gimnasios);
    }
}