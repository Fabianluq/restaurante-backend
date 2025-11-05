package com.example.restaurApp.service;

import com.example.restaurApp.entity.Reserva;
import com.example.restaurApp.entity.Cliente;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Servicio para enviar notificaciones por Email, SMS y WhatsApp
 */
@Service
public class NotificacionService {

    private static final Logger logger = LoggerFactory.getLogger(NotificacionService.class);

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.notificaciones.email.habilitado:true}")
    private boolean emailHabilitado;

    @Value("${app.notificaciones.sms.habilitado:false}")
    private boolean smsHabilitado;

    @Value("${app.notificaciones.whatsapp.habilitado:false}")
    private boolean whatsappHabilitado;

    @Value("${app.notificaciones.email.remitente:restaurapp@restaurante.com}")
    private String emailRemitente;

    @Value("${app.notificaciones.twilio.account-sid:}")
    private String twilioAccountSid;

    @Value("${app.notificaciones.twilio.auth-token:}")
    private String twilioAuthToken;

    @Value("${app.notificaciones.twilio.numero-sms:}")
    private String twilioNumeroSMS;

    @Value("${app.notificaciones.twilio.numero-whatsapp:}")
    private String twilioNumeroWhatsApp;

    @Value("${app.nombre-restaurante:RestaurApp}")
    private String nombreRestaurante;

    public NotificacionService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    private void inicializarTwilio() {
        if ((smsHabilitado || whatsappHabilitado) &&
                twilioAccountSid != null && !twilioAccountSid.isEmpty() &&
                twilioAuthToken != null && !twilioAuthToken.isEmpty()) {
            try {
                Twilio.init(twilioAccountSid, twilioAuthToken);
                logger.info("Twilio inicializado correctamente");
            } catch (Exception e) {
                logger.error("Error al inicializar Twilio: {}", e.getMessage());
            }
        }
    }

    /**
     * Envía notificación de confirmación de reserva
     */
    public void enviarConfirmacionReserva(Reserva reserva) {
        Cliente cliente = reserva.getCliente();
        String asunto = "✅ Reserva Confirmada - " + nombreRestaurante;

        if (emailHabilitado && cliente.getCorreo() != null) {
            enviarEmailConfirmacion(reserva, cliente, asunto);
        }

        if (smsHabilitado && cliente.getTelefono() != null) {
            enviarSMSConfirmacion(reserva, cliente);
        }

        if (whatsappHabilitado && cliente.getTelefono() != null) {
            enviarWhatsAppConfirmacion(reserva, cliente);
        }
    }

    /**
     * Envía notificación de cancelación de reserva
     */
    public void enviarCancelacionReserva(Reserva reserva) {
        Cliente cliente = reserva.getCliente();
        String asunto = "❌ Reserva Cancelada - " + nombreRestaurante;

        if (emailHabilitado && cliente.getCorreo() != null) {
            enviarEmailCancelacion(reserva, cliente, asunto);
        }

        if (smsHabilitado && cliente.getTelefono() != null) {
            enviarSMSCancelacion(reserva, cliente);
        }

        if (whatsappHabilitado && cliente.getTelefono() != null) {
            enviarWhatsAppCancelacion(reserva, cliente);
        }
    }

    /**
     * Envía email de confirmación usando plantilla HTML
     */
    private void enviarEmailConfirmacion(Reserva reserva, Cliente cliente, String asunto) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailRemitente);
            helper.setTo(cliente.getCorreo());
            helper.setSubject(asunto);

            // Preparar contexto para Thymeleaf
            Context context = new Context();
            context.setVariable("nombreCliente", cliente.getNombre() + " " + cliente.getApellido());
            context.setVariable("fechaReserva", reserva.getFechaReserva().toString());
            context.setVariable("horaReserva", reserva.getHoraReserva().toString());
            context.setVariable("cantidadPersonas", reserva.getCantidadPersonas());
            context.setVariable("numeroMesa",
                    reserva.getMesa() != null ? reserva.getMesa().getNumero() : "Por asignar");
            context.setVariable("numeroReserva", reserva.getId());
            context.setVariable("nombreRestaurante", nombreRestaurante);
            context.setVariable("tipoNotificacion", "confirmacion");

            // Procesar plantilla HTML
            String htmlContent = templateEngine.process("email/confirmacion-reserva", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("Email de confirmación enviado a: {}", cliente.getCorreo());

        } catch (MessagingException e) {
            logger.error("Error al enviar email de confirmación: {}", e.getMessage());
        }
    }

    /**
     * Envía email de cancelación usando plantilla HTML
     */
    private void enviarEmailCancelacion(Reserva reserva, Cliente cliente, String asunto) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailRemitente);
            helper.setTo(cliente.getCorreo());
            helper.setSubject(asunto);

            // Preparar contexto para Thymeleaf
            Context context = new Context();
            context.setVariable("nombreCliente", cliente.getNombre() + " " + cliente.getApellido());
            context.setVariable("fechaReserva", reserva.getFechaReserva().toString());
            context.setVariable("horaReserva", reserva.getHoraReserva().toString());
            context.setVariable("cantidadPersonas", reserva.getCantidadPersonas());
            context.setVariable("numeroReserva", reserva.getId());
            context.setVariable("nombreRestaurante", nombreRestaurante);
            context.setVariable("tipoNotificacion", "cancelacion");

            // Procesar plantilla HTML
            String htmlContent = templateEngine.process("email/cancelacion-reserva", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("Email de cancelación enviado a: {}", cliente.getCorreo());

        } catch (MessagingException e) {
            logger.error("Error al enviar email de cancelación: {}", e.getMessage());
        }
    }

    /**
     * Envía SMS de confirmación usando Twilio
     */
    private void enviarSMSConfirmacion(Reserva reserva, Cliente cliente) {
        if (!smsHabilitado || twilioNumeroSMS == null || twilioNumeroSMS.isEmpty()) {
            return;
        }

        try {
            inicializarTwilio();

            String mensaje = String.format(
                    "✅ %s: Tu reserva #%d para el %s a las %s ha sido confirmada. Mesa: #%d. Esperamos verte pronto!",
                    nombreRestaurante,
                    reserva.getId(),
                    reserva.getFechaReserva(),
                    reserva.getHoraReserva(),
                    reserva.getMesa() != null ? reserva.getMesa().getNumero() : 0);

            Message message = Message.creator(
                    new PhoneNumber(formatearTelefono(cliente.getTelefono())),
                    new PhoneNumber(twilioNumeroSMS),
                    mensaje).create();

            logger.info("SMS de confirmación enviado. SID: {}", message.getSid());

        } catch (Exception e) {
            logger.error("Error al enviar SMS de confirmación: {}", e.getMessage());
        }
    }

    /**
     * Envía SMS de cancelación usando Twilio
     */
    private void enviarSMSCancelacion(Reserva reserva, Cliente cliente) {
        if (!smsHabilitado || twilioNumeroSMS == null || twilioNumeroSMS.isEmpty()) {
            return;
        }

        try {
            inicializarTwilio();

            String mensaje = String.format(
                    "❌ %s: Lamentamos informarte que tu reserva #%d para el %s a las %s ha sido cancelada. Para más información, contáctanos.",
                    nombreRestaurante,
                    reserva.getId(),
                    reserva.getFechaReserva(),
                    reserva.getHoraReserva());

            Message message = Message.creator(
                    new PhoneNumber(formatearTelefono(cliente.getTelefono())),
                    new PhoneNumber(twilioNumeroSMS),
                    mensaje).create();

            logger.info("SMS de cancelación enviado. SID: {}", message.getSid());

        } catch (Exception e) {
            logger.error("Error al enviar SMS de cancelación: {}", e.getMessage());
        }
    }

    /**
     * Envía mensaje WhatsApp de confirmación usando Twilio
     */
    private void enviarWhatsAppConfirmacion(Reserva reserva, Cliente cliente) {
        if (!whatsappHabilitado || twilioNumeroWhatsApp == null || twilioNumeroWhatsApp.isEmpty()) {
            return;
        }

        try {
            inicializarTwilio();

            String mensaje = String.format(
                    "✅ *%s*\n\n" +
                            "Tu reserva ha sido *confirmada*\n\n" +
                            "📋 *Detalles:*\n" +
                            "• Número de reserva: #%d\n" +
                            "• Fecha: %s\n" +
                            "• Hora: %s\n" +
                            "• Personas: %d\n" +
                            "• Mesa: #%d\n\n" +
                            "¡Esperamos verte pronto! 🍽️",
                    nombreRestaurante,
                    reserva.getId(),
                    reserva.getFechaReserva(),
                    reserva.getHoraReserva(),
                    reserva.getCantidadPersonas(),
                    reserva.getMesa() != null ? reserva.getMesa().getNumero() : 0);

            Message message = Message.creator(
                    new PhoneNumber("whatsapp:" + formatearTelefono(cliente.getTelefono())),
                    new PhoneNumber("whatsapp:" + twilioNumeroWhatsApp),
                    mensaje).create();

            logger.info("WhatsApp de confirmación enviado. SID: {}", message.getSid());

        } catch (Exception e) {
            logger.error("Error al enviar WhatsApp de confirmación: {}", e.getMessage());
        }
    }

    /**
     * Envía mensaje WhatsApp de cancelación usando Twilio
     */
    private void enviarWhatsAppCancelacion(Reserva reserva, Cliente cliente) {
        if (!whatsappHabilitado || twilioNumeroWhatsApp == null || twilioNumeroWhatsApp.isEmpty()) {
            return;
        }

        try {
            inicializarTwilio();

            String mensaje = String.format(
                    "❌ *%s*\n\n" +
                            "Lamentamos informarte que tu reserva ha sido *cancelada*\n\n" +
                            "📋 *Detalles:*\n" +
                            "• Número de reserva: #%d\n" +
                            "• Fecha: %s\n" +
                            "• Hora: %s\n\n" +
                            "Para más información, contáctanos.",
                    nombreRestaurante,
                    reserva.getId(),
                    reserva.getFechaReserva(),
                    reserva.getHoraReserva());

            Message message = Message.creator(
                    new PhoneNumber("whatsapp:" + formatearTelefono(cliente.getTelefono())),
                    new PhoneNumber("whatsapp:" + twilioNumeroWhatsApp),
                    mensaje).create();

            logger.info("WhatsApp de cancelación enviado. SID: {}", message.getSid());

        } catch (Exception e) {
            logger.error("Error al enviar WhatsApp de cancelación: {}", e.getMessage());
        }
    }

    /**
     * Envía una notificación personalizada por email
     * @param destinatario Email del destinatario
     * @param asunto Asunto del email
     * @param mensaje Mensaje del email
     * @param token Token de autenticación (no utilizado por ahora, pero requerido por el controlador)
     */
    public void enviarNotificacionPersonalizada(String destinatario, String asunto, String mensaje, String token) {
        if (!emailHabilitado || destinatario == null || destinatario.isEmpty()) {
            logger.warn("Email deshabilitado o destinatario inválido. No se enviará notificación.");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailRemitente);
            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(mensaje, false); // false = texto plano, true = HTML

            mailSender.send(message);
            logger.info("Notificación personalizada enviada a: {}", destinatario);

        } catch (MessagingException e) {
            logger.error("Error al enviar notificación personalizada: {}", e.getMessage());
            throw new RuntimeException("Error al enviar notificación: " + e.getMessage(), e);
        }
    }

    /**
     * Formatea el número de teléfono para Twilio (formato internacional)
     */
    private String formatearTelefono(String telefono) {
        if (telefono == null || telefono.isEmpty()) {
            return "";
        }

        // Eliminar espacios y caracteres especiales
        String limpio = telefono.replaceAll("[^0-9+]", "");

        // Si no empieza con +, agregar código de país (Colombia: +57)
        if (!limpio.startsWith("+")) {
            if (limpio.startsWith("57")) {
                return "+" + limpio;
            } else if (limpio.length() == 10) {
                // Asumir número colombiano
                return "+57" + limpio;
            }
        }

        return limpio;
    }
}
