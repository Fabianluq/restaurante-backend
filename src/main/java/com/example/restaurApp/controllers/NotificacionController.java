package com.example.restaurApp.controllers;

import com.example.restaurApp.dto.ApiResponse;
import com.example.restaurApp.service.NotificacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {
    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @PostMapping("/reserva/{reservaId}/confirmacion")
    @PreAuthorize("hasAnyRole('ADMIN','MESERO')")
    public ResponseEntity<ApiResponse<Void>> enviarConfirmacionReserva(
            @PathVariable Long reservaId,
            @RequestHeader("Authorization") String token) {
        
        notificacionService.enviarConfirmacionReserva(reservaId, token);
        return ResponseEntity.ok(ApiResponse.success("Email de confirmación enviado exitosamente", null));
    }

    @PostMapping("/enviar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> enviarNotificacionPersonalizada(
            @RequestParam String destinatario,
            @RequestParam String asunto,
            @RequestParam String mensaje,
            @RequestHeader("Authorization") String token) {
        
        notificacionService.enviarNotificacionPersonalizada(destinatario, asunto, mensaje, token);
        return ResponseEntity.ok(ApiResponse.success("Notificación enviada exitosamente", null));
    }
}
