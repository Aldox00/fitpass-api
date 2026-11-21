package com.fitpass.service;

import com.fitpass.dto.GimnasioRequest;
import com.fitpass.entity.Gimnasio;
import com.fitpass.repository.GimnasioRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class GimnasioService {

    private final GimnasioRepository gimnasioRepository;

    public GimnasioService(GimnasioRepository gimnasioRepository) {
        this.gimnasioRepository = gimnasioRepository;
    }

    public List<Gimnasio> obtenerTodosActivos() {
        return gimnasioRepository.findByEsActivoTrue();
    }

    public List<Gimnasio> buscarPorCiudad(String ciudad) {
        return gimnasioRepository.findByCiudadContainingIgnoreCaseAndEsActivoTrue(ciudad);
    }

    public Gimnasio registrarGimnasio(GimnasioRequest request) {
        Gimnasio gimnasio = new Gimnasio(
                request.getNombre(),
                request.getDireccion(),
                request.getCiudad(),
                request.getLatitud(),
                request.getLongitud()
        );
        return gimnasioRepository.save(gimnasio);
    }

    public List<Gimnasio> buscarCercanos(Double latUsuario, Double lonUsuario, Double radioKm) {
        List<Gimnasio> todos = gimnasioRepository.findByEsActivoTrue();
        List<Gimnasio> cercanos = new ArrayList<>();

        for (Gimnasio g : todos) {
            double distancia = calcularDistanciaKm(latUsuario, lonUsuario, g.getLatitud(), g.getLongitud());
            if (distancia <= radioKm) {
                cercanos.add(g);
            }
        }
        return cercanos;
    }

    private double calcularDistanciaKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radio de la Tierra en Km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}