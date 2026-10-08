package com.fitpass.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GeolocalizacionGimnasioService {

    private final RestTemplate restTemplate;

    public GeolocalizacionGimnasioService() {
        this.restTemplate = new RestTemplate();
    }

    public String geolocalizarSucursal(String ciudad) {
        String url = "https://nominatim.openstreetmap.org/search?q=" + ciudad + "&format=json&limit=1";

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "FitPassApp-AcademicProject");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );

        if (response.getBody() != null && !response.getBody().isEmpty()) {
            Map<String, Object> primerResultado = response.getBody().get(0);
            return " Latitud: " + primerResultado.get("lat") +
                    " | Longitud: " + primerResultado.get("lon") +
                    "\n   Ubicación: " + primerResultado.get("display_name");
        }
        return "No se encontraron coordenadas.";
    }
}