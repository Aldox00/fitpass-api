package com.fitpass.client;

import com.fitpass.dto.ApiResponse;
import com.fitpass.dto.RegisterRequest;
import com.fitpass.dto.UsuarioResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.List;


@Component
public class FitPassClientExample {

    private final RestTemplate restTemplate;
    private final String BASE_URL = "http://localhost:8080/api";

    public FitPassClientExample() {
        this.restTemplate = new RestTemplate();
    }

    public UsuarioResponse consumirRegistro(String nombre, String email, String password, String rol) {
        String url = BASE_URL + "/auth/register";

        RegisterRequest requestBody = new RegisterRequest(nombre, email, password, rol);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RegisterRequest> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<ApiResponse<UsuarioResponse>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<ApiResponse<UsuarioResponse>>() {}
        );

        if (response.getBody() != null && response.getBody().isExito()) {
            return response.getBody().getDatos();
        }
        return null;
    }

    public List<UsuarioResponse> consumirObtenerUsuarios() {
        String url = BASE_URL + "/usuarios";

        ResponseEntity<ApiResponse<List<UsuarioResponse>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ApiResponse<List<UsuarioResponse>>>() {}
        );

        if (response.getBody() != null && response.getBody().isExito()) {
            return response.getBody().getDatos();
        }
        return List.of();
    }
}