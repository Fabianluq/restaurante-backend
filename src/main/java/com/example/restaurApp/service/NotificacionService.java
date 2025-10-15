package com.example.restaurApp.service;

import com.example.restaurApp.entity.Reserva;
import com.example.restaurApp.excepciones.Validacion;
import com.example.restaurApp.repository.ReservaRepository;
import com.example.restaurApp.security.JwtUtil;
import com.example.restaurApp.util.EmpleadoUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class NotificacionService {
    private final JavaMailSender mailSender;
    private final ReservaRepository reservaRepository;
    private final JwtUtil jwtUtil;

    @Value("${app.email.from:noreply@restaurante.com}")
    private String fromEmail;

    @Value("${app.restaurant.name:Restaurante App}")
    private String restaurantName;

    public NotificacionService(JavaMailSender mailSender, ReservaRepository reservaRepository, JwtUtil jwtUtil) {
        this.mailSender = mailSender;
        this.reservaRepository = reservaRepository;
        this.jwtUtil = jwtUtil;
    }

    public void enviarConfirmacionReserva(Long reservaId, String token) {
        // Validar token y obtener empleado
        String username = jwtUtil.extractUsername(token.substring(7));
        com.example.restaurApp.entity.Empleado empleado = jwtUtil.getEmpleadoFromToken(token.substring(7));
        
        if (empleado == null) {
            throw new Validacion("Token inválido o empleado no encontrado.");
        }

        // Validar que el empleado esté activo
        EmpleadoUtil.validarEmpleadoActivo(empleado);

        // Buscar la reserva
        Reserva reserva = reservaRepository.findById(reservaId)
            .orElseThrow(() -> new Validacion("Reserva no encontrada."));

        // Validar que la reserva tenga cliente con email
        if (reserva.getCliente() == null || reserva.getCliente().getCorreo() == null) {
            throw new Validacion("La reserva no tiene un cliente con email válido.");
        }

        // Enviar email de confirmación
        enviarEmailConfirmacion(reserva);
    }

    private void enviarEmailConfirmacion(Reserva reserva) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(reserva.getCliente().getCorreo());
            message.setSubject("Confirmación de Reserva - " + restaurantName);
            message.setText(generarContenidoEmail(reserva));

            mailSender.send(message);
        } catch (Exception e) {
            throw new Validacion("Error al enviar el email de confirmación: " + e.getMessage());
        }
    }

    private String generarContenidoEmail(Reserva reserva) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        StringBuilder contenido = new StringBuilder();
        contenido.append("Estimado/a ").append(reserva.getCliente().getNombre()).append(" ").append(reserva.getCliente().getApellido()).append(",\n\n");
        contenido.append("Su reserva ha sido confirmada exitosamente.\n\n");
        contenido.append("Detalles de la reserva:\n");
        contenido.append("• Fecha: ").append(reserva.getFechaReserva().format(dateFormatter)).append("\n");
        contenido.append("• Hora: ").append(reserva.getHoraReserva().format(timeFormatter)).append("\n");
        contenido.append("• Mesa: ").append(reserva.getMesa().getNumero()).append("\n");
        contenido.append("• Número de personas: ").append(reserva.getCantidadPersonas()).append("\n");
        contenido.append("• Estado: ").append(reserva.getEstadoReserva().getDescripcion()).append("\n\n");
        
        // Nota: La entidad Reserva no tiene campo observaciones, se omite esta sección
        
        contenido.append("¡Esperamos verlo pronto!\n\n");
        contenido.append("Saludos cordiales,\n");
        contenido.append("Equipo de ").append(restaurantName);

        return contenido.toString();
    }

    public void enviarNotificacionPersonalizada(String destinatario, String asunto, String mensaje, String token) {
        // Validar token y obtener empleado
        String username = jwtUtil.extractUsername(token.substring(7));
        com.example.restaurApp.entity.Empleado empleado = jwtUtil.getEmpleadoFromToken(token.substring(7));
        
        if (empleado == null) {
            throw new Validacion("Token inválido o empleado no encontrado.");
        }

        // Validar que el empleado esté activo y sea administrador
        EmpleadoUtil.validarRolEmpleado(empleado, "ADMINISTRADOR");

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(destinatario);
            message.setSubject(asunto);
            message.setText(mensaje);

            mailSender.send(message);
        } catch (Exception e) {
            throw new Validacion("Error al enviar la notificación: " + e.getMessage());
        }
    }
}
