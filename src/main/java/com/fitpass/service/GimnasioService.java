    package com.fitpass.service;

    import com.fitpass.dto.GimnasioRequest;
    import com.fitpass.entity.Gimnasio;
    import com.fitpass.repository.GimnasioRepository;
    import org.springframework.stereotype.Service;
    import java.util.ArrayList;
    import java.util.Comparator;
    import java.util.List;

    @Service
    public class GimnasioService {

        private static final double LAT_SUCHIAPA_DEFAULT = 16.6215427;
        private static final double LON_SUCHIAPA_DEFAULT = -93.1047773;

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
            String ciudadLimpia = (request.getCiudad() == null || request.getCiudad().isBlank())
                    ? "Suchiapa"
                    : request.getCiudad().trim();

            Gimnasio gimnasio = new Gimnasio(
                    request.getNombre().trim(),
                    request.getDireccion().trim(),
                    ciudadLimpia,
                    request.getLatitud(),
                    request.getLongitud()
            );
            return gimnasioRepository.save(gimnasio);
        }

        public List<Gimnasio> buscarCercanos(String ciudad, Double latitud, Double longitud) {
            List<Gimnasio> base = (ciudad != null && !ciudad.isBlank())
                    ? new ArrayList<>(buscarPorCiudad(ciudad.trim()))
                    : new ArrayList<>(obtenerTodosActivos());

            double refLat = (latitud != null) ? latitud : LAT_SUCHIAPA_DEFAULT;
            double refLon = (longitud != null) ? longitud : LON_SUCHIAPA_DEFAULT;

            base.sort(Comparator.comparingDouble(g -> {
                if (g.getLatitud() == null || g.getLongitud() == null) {
                    return Double.MAX_VALUE;
                }
                return calcularDistanciaHaversine(refLat, refLon, g.getLatitud(), g.getLongitud());
            }));

            return base;
        }

        private double calcularDistanciaHaversine(double lat1, double lon1, double lat2, double lon2) {
            final double RADIO_TIERRA_METROS = 6371000.0;
            double dLat = Math.toRadians(lat2 - lat1);
            double dLon = Math.toRadians(lon2 - lon1);
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                    + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                    * Math.sin(dLon / 2) * Math.sin(dLon / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            return RADIO_TIERRA_METROS * c;
        }
    }