package com.fitpass.service;

import com.fitpass.dto.UbicacionResponseDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

@Service
public class GeolocalizacionService {

    private final RestClient restClient;

    public GeolocalizacionService(@Qualifier("nominatimRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public List<UbicacionResponseDTO> buscarPorDireccion(String direccion) {
        try {
            List<UbicacionResponseDTO> resultado = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam("q", direccion)
                            .queryParam("format", "json")
                            .queryParam("limit", 5)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<UbicacionResponseDTO>>() {});
            return resultado != null ? resultado : Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public UbicacionResponseDTO buscarPorCoordenadas(Double latitud, Double longitud) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/reverse")
                            .queryParam("lat", latitud)
                            .queryParam("lon", longitud)
                            .queryParam("format", "json")
                            .build())
                    .retrieve()
                    .body(UbicacionResponseDTO.class);
        } catch (Exception e) {
            return null;
        }
    }

    public List<UbicacionResponseDTO> buscarGimnasiosPorCiudad(String ciudad) {
        return buscarPorDireccion(ciudad);
    }
}