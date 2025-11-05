package com.example.restaurApp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    @Value("${app.email.from:noreply@restaurante.com}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarEmailRecuperacion(String correo, String token) {
        try {
            String resetLink = frontendUrl + "/resetear-contrasenia?token=" + token;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(correo);
            message.setSubject("Recuperación de Contraseña - RestaurApp");
            message.setText(String.format(
                "Hola,\n\n" +
                "Has solicitado recuperar tu contraseña. Haz clic en el siguiente enlace para restablecerla:\n\n" +
                "%s\n\n" +
                "Este enlace expirará en 1 hora.\n\n" +
                "Si no solicitaste este cambio, ignora este correo.\n\n" +
                "Saludos,\n" +
                "Equipo RestaurApp",
                resetLink
            ));

            mailSender.send(message);
        } catch (Exception e) {
            // Log del error pero no lanzar excepción para no revelar si el correo existe
            System.err.println("Error al enviar email de recuperación: " + e.getMessage());
        }
    }
}

