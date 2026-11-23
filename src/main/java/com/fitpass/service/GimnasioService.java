package com.fitpass.service;

import com.fitpass.dto.GimnasioRequest;
import com.fitpass.entity.Gimnasio;
import com.fitpass.repository.GimnasioRepository;
import org.springframework.stereotype.Service;
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
                request.getCiudad()
        );
        return gimnasioRepository.save(gimnasio);
    }

    public List<Gimnasio> buscarCercanos(String ciudad) {
        if (ciudad != null && !ciudad.isBlank()) {
            return buscarPorCiudad(ciudad);
        }
        return obtenerTodosActivos();
    }
}