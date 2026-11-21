package com.fitpass.service;

import com.fitpass.dto.CheckInRequest;
import com.fitpass.entity.Gimnasio;
import com.fitpass.entity.Membresia;
import com.fitpass.entity.Pase;
import com.fitpass.entity.Usuario;
import com.fitpass.repository.GimnasioRepository;
import com.fitpass.repository.MembresiaRepository;
import com.fitpass.repository.PaseRepository;
import com.fitpass.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PaseService {

    private final PaseRepository paseRepository;
    private final UsuarioRepository usuarioRepository;
    private final GimnasioRepository gimnasioRepository;
    private final MembresiaRepository membresiaRepository;

    public PaseService(PaseRepository paseRepository,
                       UsuarioRepository usuarioRepository,
                       GimnasioRepository gimnasioRepository,
                       MembresiaRepository membresiaRepository) {
        this.paseRepository = paseRepository;
        this.usuarioRepository = usuarioRepository;
        this.gimnasioRepository = gimnasioRepository;
        this.membresiaRepository = membresiaRepository;
    }

    public Pase registrarCheckIn(CheckInRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + request.getUsuarioId()));

        Gimnasio gimnasio = gimnasioRepository.findById(request.getGimnasioId())
                .orElseThrow(() -> new RuntimeException("Gimnasio no encontrado con ID: " + request.getGimnasioId()));

        if (!Boolean.TRUE.equals(gimnasio.getEsActivo())) {
            throw new RuntimeException("El gimnasio seleccionado no está activo actualmente.");
        }

        Optional<Pase> paseAbierto = paseRepository.findFirstByUsuarioIdAndFechaCheckOutIsNullOrderByFechaCheckInDesc(usuario.getId());
        if (paseAbierto.isPresent()) {
            throw new RuntimeException("Acceso denegado: El usuario ya se encuentra dentro del gimnasio. Debe marcar su SALIDA antes de registrar otro ingreso.");
        }

        Optional<Membresia> membresiaOpt = membresiaRepository
                .findFirstByUsuarioIdAndActivaTrueAndFechaFinGreaterThanEqualOrderByIdDesc(usuario.getId(), LocalDate.now());

        if (membresiaOpt.isEmpty()) {
            throw new RuntimeException("Acceso denegado: El usuario no cuenta con una membresía o pase activo vigente.");
        }

        Pase nuevoPase = new Pase(usuario, gimnasio, membresiaOpt.get());
        return paseRepository.save(nuevoPase);
    }

    public Pase registrarCheckOut(Long usuarioId) {
        Pase paseActivo = paseRepository.findFirstByUsuarioIdAndFechaCheckOutIsNullOrderByFechaCheckInDesc(usuarioId)
                .orElseThrow(() -> new RuntimeException("No se encontró una visita activa para este usuario. Es necesario haber registrado un Check-In previamente."));

        paseActivo.setFechaCheckOut(LocalDateTime.now());
        paseActivo.setEstado("FINALIZADO");
        return paseRepository.save(paseActivo);
    }

    public List<Pase> obtenerHistorialUsuario(Long usuarioId) {
        return paseRepository.findByUsuarioIdOrderByFechaCheckInDesc(usuarioId);
    }

    public List<Pase> obtenerHistorialGimnasio(Long gimnasioId) {
        return paseRepository.findByGimnasioIdOrderByFechaCheckInDesc(gimnasioId);
    }

    public Map<String, Object> obtenerReporteDiarioGimnasio(Long gimnasioId) {
        LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
        LocalDateTime finHoy = LocalDate.now().atTime(LocalTime.MAX);

        List<Pase> pasesHoy = paseRepository.findByGimnasioIdAndFechaCheckInBetween(gimnasioId, inicioHoy, finHoy);

        long visitas = pasesHoy.stream()
                .filter(p -> p.getMembresia() != null && "VISITA".equalsIgnoreCase(p.getMembresia().getTipoPase()))
                .count();

        long semanales = pasesHoy.stream()
                .filter(p -> p.getMembresia() != null && "SEMANAL".equalsIgnoreCase(p.getMembresia().getTipoPase()))
                .count();

        long mensuales = pasesHoy.stream()
                .filter(p -> p.getMembresia() != null && "MENSUAL".equalsIgnoreCase(p.getMembresia().getTipoPase()))
                .count();

        long anuales = pasesHoy.stream()
                .filter(p -> p.getMembresia() != null && "ANUAL".equalsIgnoreCase(p.getMembresia().getTipoPase()))
                .count();

        long aforoActual = pasesHoy.stream()
                .filter(p -> p.getFechaCheckOut() == null)
                .count();

        Map<String, Object> reporte = new HashMap<>();
        reporte.put("gimnasioId", gimnasioId);
        reporte.put("fechaReporte", LocalDate.now());
        reporte.put("totalCheckInsHoy", pasesHoy.size());
        reporte.put("personasEntrenandoAhora", aforoActual);
        reporte.put("desgloseVisita", visitas);
        reporte.put("desgloseSemanal", semanales);
        reporte.put("desgloseMensual", mensuales);
        reporte.put("desgloseAnual", anuales);

        return reporte;
    }
}