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
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PaseService {

    private static final long MINUTOS_MINIMOS_ESTANCIA = 120L; // 2 horas

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

    public Map<String, Object> obtenerEstadoQr(Long usuarioId) {
        Map<String, Object> estado = new HashMap<>();

        Optional<Membresia> membresiaOpt = membresiaRepository
                .findFirstByUsuarioIdAndActivaTrueAndFechaFinGreaterThanEqualOrderByIdDesc(usuarioId, LocalDate.now());

        if (membresiaOpt.isEmpty() || !"PAGADO".equalsIgnoreCase(membresiaOpt.get().getEstadoPago())) {
            estado.put("modo", "BLOQUEADO");
            estado.put("mensaje", "Membresía pendiente de pago o inactiva.");
            return estado;
        }

        Membresia membresia = membresiaOpt.get();
        Optional<Pase> paseAbiertoOpt = paseRepository
                .findFirstByUsuarioIdAndFechaCheckOutIsNullOrderByFechaCheckInDesc(usuarioId);

        if (paseAbiertoOpt.isEmpty()) {
            estado.put("modo", "ENTRADA");
            estado.put("codigoQr", "FITPASS-ENTRADA-" + usuarioId + "-" + membresia.getId());
            estado.put("mensaje", "Muestra este QR en la entrada del gimnasio");
            return estado;
        }

        Pase paseAbierto = paseAbiertoOpt.get();
        long minutosTranscurridos = Duration.between(paseAbierto.getFechaCheckIn(), LocalDateTime.now()).toMinutes();

        if (minutosTranscurridos < MINUTOS_MINIMOS_ESTANCIA) {
            long faltan = MINUTOS_MINIMOS_ESTANCIA - minutosTranscurridos;
            estado.put("modo", "EN_SESION");
            estado.put("minutosRestantes", faltan);
            estado.put("mensaje", "Entrada ya registrada. El QR de salida se activará en " + faltan + " min.");
        } else {
            estado.put("modo", "SALIDA");
            estado.put("codigoQr", "FITPASS-SALIDA-" + usuarioId + "-" + paseAbierto.getId());
            estado.put("mensaje", "Tiempo cumplido. Muestra este QR para registrar tu salida");
        }

        return estado;
    }

    public Pase procesarEscaneoQr(String codigoQr, Long gimnasioId) {
        if (codigoQr == null || codigoQr.isBlank()) {
            throw new RuntimeException("Código QR vacío o inválido.");
        }

        String limpio = codigoQr.trim();
        String[] partes = limpio.split("-");
        // Formato esperado: FITPASS-ENTRADA-{usuarioId}-{id} o FITPASS-SALIDA-{usuarioId}-{paseId}
        if (partes.length != 4 || !"FITPASS".equals(partes[0])) {
            throw new RuntimeException("Formato de código QR no reconocido por FitPass.");
        }

        String tipo = partes[1];
        Long usuarioId;
        Long referenciaId;
        try {
            usuarioId = Long.parseLong(partes[2]);
            referenciaId = Long.parseLong(partes[3]);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Datos corruptos en el código QR.");
        }

        if ("ENTRADA".equalsIgnoreCase(tipo)) {
            Optional<Pase> yaDentro = paseRepository
                    .findFirstByUsuarioIdAndFechaCheckOutIsNullOrderByFechaCheckInDesc(usuarioId);
            if (yaDentro.isPresent()) {
                throw new RuntimeException("Este QR de entrada ya fue escaneado. El socio ya está dentro del gimnasio.");
            }
            CheckInRequest req = new CheckInRequest();
            req.setUsuarioId(usuarioId);
            req.setGimnasioId(gimnasioId != null ? gimnasioId : 1L);
            return registrarCheckIn(req);
        }

        if ("SALIDA".equalsIgnoreCase(tipo)) {
            Pase paseActivo = paseRepository
                    .findFirstByUsuarioIdAndFechaCheckOutIsNullOrderByFechaCheckInDesc(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Este QR de salida ya fue utilizado o no existe una entrada activa."));

            if (!paseActivo.getId().equals(referenciaId)) {
                throw new RuntimeException("Código QR de salida expirado o no corresponde a la sesión actual.");
            }

            long minutos = Duration.between(paseActivo.getFechaCheckIn(), LocalDateTime.now()).toMinutes();
            if (minutos < MINUTOS_MINIMOS_ESTANCIA) {
                long faltan = MINUTOS_MINIMOS_ESTANCIA - minutos;
                throw new RuntimeException("Aún no se cumplen las 2 horas de estancia. Faltan " + faltan + " minutos para poder salir.");
            }

            return registrarCheckOut(usuarioId);
        }

        throw new RuntimeException("Tipo de pase QR inválido: " + tipo);
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