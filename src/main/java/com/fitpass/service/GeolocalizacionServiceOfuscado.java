package com.fitpass.service;

import com.fitpass.dto.UbicacionResponseDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Service("geolocalizacionServiceOfuscado")
public class GeolocalizacionServiceOfuscado {

    private final RestClient a;

    public GeolocalizacionServiceOfuscado(@Qualifier("nominatimRestClient") RestClient a) {
        this.a = a;
    }

    // Método ofuscado: buscarPorDireccion -> a
    public List<UbicacionResponseDTO> a(String a) {
        try {
            // Decodificación dinámica de cadenas ocultas en Base64
            String _a = new String(Base64.getDecoder().decode("L3NlYXJjaA==")); // "/search"
            String _b = new String(Base64.getDecoder().decode("cQ=="));          // "q"
            String _c = new String(Base64.getDecoder().decode("Zm9ybWF0"));      // "format"
            String _d = new String(Base64.getDecoder().decode("anNvbg=="));      // "json"

            List<UbicacionResponseDTO> _e = this.a.get()
                    .uri(b -> b.path(_a)
                            .queryParam(_b, a)
                            .queryParam(_c, _d)
                            .queryParam("limit", 5)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<UbicacionResponseDTO>>() {});
            return _e != null ? _e : Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // Método ofuscado: buscarPorCoordenadas -> b
    public UbicacionResponseDTO b(Double a, Double b) {
        try {
            String _a = new String(Base64.getDecoder().decode("L3JldmVyc2U=")); // "/reverse"
            String _b = new String(Base64.getDecoder().decode("bGF0"));          // "lat"
            String _c = new String(Base64.getDecoder().decode("bG9u"));          // "lon"
            String _d = new String(Base64.getDecoder().decode("Zm9ybWF0"));      // "format"
            String _e = new String(Base64.getDecoder().decode("anNvbg=="));      // "json"

            return this.a.get()
                    .uri(c -> c.path(_a)
                            .queryParam(_b, a)
                            .queryParam(_c, b)
                            .queryParam(_d, _e)
                            .build())
                    .retrieve()
                    .body(UbicacionResponseDTO.class);
        } catch (Exception e) {
            return null;
        }
    }
}