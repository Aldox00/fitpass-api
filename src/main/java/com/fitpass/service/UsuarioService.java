package com.fitpass.service;

import com.fitpass.dto.LoginRequest;
import com.fitpass.dto.RegisterRequest;
import com.fitpass.dto.UsuarioResponse;
import com.fitpass.entity.Usuario;
import com.fitpass.repository.UsuarioRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    private final Map<String, String> codigosRecuperacion = new HashMap<>();

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JavaMailSender mailSender) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    public UsuarioResponse registrar(RegisterRequest request) {
        String emailLimpio = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "";

        if (usuarioRepository.existsByEmailIgnoreCase(emailLimpio)) {
            throw new IllegalArgumentException("El correo ya está registrado en FitPass");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(emailLimpio);
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(request.getRol() != null ? request.getRol() : "SOCIO");

        Usuario guardado = usuarioRepository.save(usuario);
        return mapToResponse(guardado);
    }

    public UsuarioResponse login(LoginRequest request) {
        String emailLimpio = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "";

        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(emailLimpio)
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        return mapToResponse(usuario);
    }

    public UsuarioResponse loginORegistrarGoogle(String idToken) {
        String emailGoogle = (idToken != null && idToken.contains("@"))
                ? idToken.trim().toLowerCase()
                : "usuario.google@fitpass.com";

        String nombreGenerado = emailGoogle.split("@")[0];
        String nombreGoogle = nombreGenerado.substring(0, 1).toUpperCase() + nombreGenerado.substring(1);

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmailIgnoreCase(emailGoogle);
        Usuario usuario;

        if (usuarioOpt.isPresent()) {
            usuario = usuarioOpt.get();
        } else {
            usuario = new Usuario();
            usuario.setNombre(nombreGoogle);
            usuario.setEmail(emailGoogle);
            usuario.setPassword(passwordEncoder.encode("GOOGLE_AUTH_SECURE_TOKEN"));
            usuario.setRol("SOCIO");
            usuario = usuarioRepository.save(usuario);
        }

        return mapToResponse(usuario);
    }

    public void enviarCodigoRecuperacion(String email) {
        String emailLimpio = email != null ? email.trim().toLowerCase() : "";

        if (!usuarioRepository.existsByEmailIgnoreCase(emailLimpio)) {
            throw new IllegalArgumentException("El correo no está registrado en FitPass");
        }

        String codigo = String.format("%06d", new Random().nextInt(999999));
        codigosRecuperacion.put(emailLimpio, codigo);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(emailLimpio);
            helper.setFrom("FitPass Soporte <fitpass.oficial@gmail.com>"); // Remitente corporativo
            helper.setSubject("Código de Recuperación - FitPass");

            String htmlContent = """
                <div style="background-color: #121212; padding: 40px 0; font-family: Arial, sans-serif; text-align: center;">
                    <div style="max-width: 480px; margin: 0 auto; background-color: #1a1a1a; border-radius: 16px; padding: 40px; border: 1px solid #2c2c2c; box-shadow: 0 4px 15px rgba(0,0,0,0.5);">
                        
                        <h1 style="color: #FF5722; font-size: 28px; margin-bottom: 8px; font-weight: bold;">FitPass</h1>
                        <p style="color: #888888; font-size: 14px; margin-top: 0; margin-bottom: 30px;">Acceso a Gimnasios</p>
                        
                        <h3 style="color: #ffffff; font-size: 18px; margin-bottom: 15px;">Hola</h3>
                        
                        <p style="color: #cccccc; font-size: 14px; line-height: 1.6; margin-bottom: 30px;">
                            Recibimos una solicitud para restablecer tu acceso. Introduce este código de seguridad de 6 dígitos dentro de la aplicación:
                        </p>
                        
                        <div style="background-color: #121212; border: 2px dashed #FF5722; border-radius: 12px; padding: 20px; margin-bottom: 30px;">
                            <span style="color: #FF5722; font-size: 36px; font-weight: bold; letter-spacing: 6px;">%s</span>
                        </div>
                        
                        <p style="color: #777777; font-size: 12px; line-height: 1.5;">
                            Este código expirará automáticamente en 15 minutos. Si no solicitaste este cambio, puedes ignorar este mensaje.
                        </p>
                        
                    </div>
                </div>
                """.formatted(codigo);

            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el correo HTML de recuperación", e);
        }
    }

    public void restablecerContrasena(String email, String codigoIngresado, String nuevaPassword) {
        String emailLimpio = email != null ? email.trim().toLowerCase() : "";
        String codigoGuardado = codigosRecuperacion.get(emailLimpio);

        if (codigoGuardado == null || !codigoGuardado.equals(codigoIngresado)) {
            throw new IllegalArgumentException("Código de verificación incorrecto o expirado");
        }

        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(emailLimpio)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);

        // Limpiar el código usado de la memoria temporal
        codigosRecuperacion.remove(emailLimpio);
    }

    public List<UsuarioResponse> obtenerTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public UsuarioResponse obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        return mapToResponse(usuario);
    }

    public List<UsuarioResponse> buscarPorNombreOEmail(String criterio) {
        return usuarioRepository.findAll().stream()
                .filter(u -> u.getNombre().toLowerCase().contains(criterio.toLowerCase()) ||
                        u.getEmail().toLowerCase().contains(criterio.toLowerCase()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public UsuarioResponse actualizar(Long id, RegisterRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        usuario.setNombre(request.getNombre());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getRol() != null) {
            usuario.setRol(request.getRol());
        }

        Usuario actualizado = usuarioRepository.save(usuario);
        return mapToResponse(actualizado);
    }

    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    private UsuarioResponse mapToResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol()
        );
    }
}