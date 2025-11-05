package com.example.restaurApp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    @Value("${app.email.from:noreply@restaurante.com}")
    private String fromEmail;

    @Value("${app.nombre-restaurante:RestaurApp}")
    private String nombreRestaurante;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void enviarEmailRecuperacion(String correo, String token) {
        try {
            String resetLink = frontendUrl + "/resetear-contrasenia?token=" + token;

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(correo);
            helper.setSubject("Recuperación de Contraseña - " + nombreRestaurante);
            
            // Preparar contexto para Thymeleaf
            Context context = new Context();
            context.setVariable("resetLink", resetLink);
            context.setVariable("nombreRestaurante", nombreRestaurante);
            context.setVariable("token", token);
            
            // Procesar plantilla HTML
            String htmlContent = templateEngine.process("email/recuperar-contrasenia", context);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            
        } catch (MessagingException e) {
            // Log del error pero no lanzar excepción para no revelar si el correo existe
            System.err.println("Error al enviar email de recuperación: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error al generar plantilla de email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

