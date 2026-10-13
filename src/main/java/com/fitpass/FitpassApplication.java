package com.fitpass;

import com.fitpass.client.FitPassClientExample;
import com.fitpass.client.GeolocalizacionGimnasioService;
import com.fitpass.dto.UbicacionResponseDTO;
import com.fitpass.dto.UsuarioResponse;
import com.fitpass.service.GeolocalizacionServiceOfuscado;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class FitpassApplication {

    public static void main(String[] args) {
        SpringApplication.run(FitpassApplication.class, args);
    }

    @Bean
    public CommandLineRunner runClientExamples(
            FitPassClientExample client,
            GeolocalizacionGimnasioService geoService,
            GeolocalizacionServiceOfuscado geoServiceOfuscado) {
        return args -> {
            System.out.println("\n=== INICIANDO CONSUMO CLIENTE DE API FITPASS ===");

            try {
                String emailPrueba = "cliente_spb_" + System.currentTimeMillis() + "@fitpass.com";
                UsuarioResponse nuevo = client.consumirRegistro("Socio Cliente SPB", emailPrueba, "PassClient123", "SOCIO");
                System.out.println("USUARIO REGISTRADO VIA RESTTEMPLATE:");
                System.out.println("   ID: " + nuevo.getId() + " | Nombre: " + nuevo.getNombre() + " | Email: " + nuevo.getEmail() + " | Rol: " + nuevo.getRol());
            } catch (Exception e) {
                System.err.println("Error consumiendo registro: " + e.getMessage());
            }

            try {
                List<UsuarioResponse> lista = client.consumirObtenerUsuarios();
                System.out.println("\n LISTA DE USUARIOS OBTENIDA VIA RESTTEMPLATE (" + lista.size() + " registros):");
                for (UsuarioResponse u : lista) {
                    System.out.println("   - [" + u.getId() + "] " + u.getNombre() + " (" + u.getEmail() + ") - Rol: " + u.getRol());
                }
            } catch (Exception e) {
                System.err.println("Error consumiendo lista de usuarios: " + e.getMessage());
            }

            System.out.println("\n=== CONSUMO DE API TERCEROS (GEOLOCALIZACIÓN DE GIMNASIOS) ===");
            try {
                String datosUbicacion = geoService.geolocalizarSucursal("Tuxtla Gutierrez");
                System.out.println(" GEOLOCALIZACIÓN DE SUCURSAL FITPASS (NOMINATIM / OSM):");
                System.out.println("   " + datosUbicacion);
            } catch (Exception e) {
                System.err.println("Error en geolocalización: " + e.getMessage());
            }

            System.out.println("\n=== CONSUMO DESDE CLASE OFUSCADA (MÉTODOS RENOMBRADOS + BASE64) ===");
            try {
                List<UbicacionResponseDTO> listaOfuscada = geoServiceOfuscado.a("Tuxtla Gutierrez");
                if (!listaOfuscada.isEmpty()) {
                    System.out.println(" PRUEBA OFUSCADA EXITOSA (.a() -> Base64 Decode /search):");
                    System.out.println("   Registros recuperados: " + listaOfuscada.size());
                    System.out.println("   Muestra: " + listaOfuscada.get(0).direccionCompleta());
                } else {
                    System.out.println(" Consulta ofuscada ejecutada sin resultados.");
                }
            } catch (Exception e) {
                System.err.println("Error en servicio ofuscado: " + e.getMessage());
            }

            System.out.println("=======\n");
        };
    }
}