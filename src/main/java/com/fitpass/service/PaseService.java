package com.fitpass.service;

import com.fitpass.dto.CheckInRequest;
import com.fitpass.entity.Gimnasio;
import com.fitpass.entity.Pase;
import com.fitpass.entity.Usuario;
import com.fitpass.repository.GimnasioRepository;
import com.fitpass.repository.PaseRepository;
import com.fitpass.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaseService {

    private final PaseRepository paseRepository;
    private final UsuarioRepository usuarioRepository;
    private final GimnasioRepository gimnasioRepository;

    public PaseService(PaseRepository paseRepository,
                       UsuarioRepository usuarioRepository,
                       GimnasioRepository gimnasioRepository) {
        this.paseRepository = paseRepository;
        this.usuarioRepository = usuarioRepository;
        this.gimnasioRepository = gimnasioRepository;
    }

    public Pase registrarCheckIn(CheckInRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + request.getUsuarioId()));

        Gimnasio gimnasio = gimnasioRepository.findById(request.getGimnasioId())
                .orElseThrow(() -> new RuntimeException("Gimnasio no encontrado con ID: " + request.getGimnasioId()));

        Pase nuevoPase = new Pase(usuario, gimnasio);
        return paseRepository.save(nuevoPase);
    }

    public List<Pase> obtenerHistorialUsuario(Long usuarioId) {
        return paseRepository.findByUsuarioIdOrderByFechaCheckInDesc(usuarioId);
    }
}