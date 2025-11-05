package com.example.restaurApp.controllers;

import com.example.restaurApp.dto.ApiResponse;
import com.example.restaurApp.entity.Reserva;
import com.example.restaurApp.service.NotificacionService;
import com.example.restaurApp.service.ReservaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {
    private final NotificacionService notificacionService;
    private final ReservaService reservaService;

    public NotificacionController(NotificacionService notificacionService, ReservaService reservaService) {
        this.notificacionService = notificacionService;
        this.reservaService = reservaService;
    }

    @PostMapping("/reserva/{reservaId}/confirmacion")
    public ResponseEntity<ApiResponse<Void>> enviarConfirmacionReserva(
            @PathVariable Long reservaId,
            @RequestHeader("Authorization") String token) {
        
        Optional<Reserva> reservaOpt = reservaService.buscarReservaPorId(reservaId);
        if (reservaOpt.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Reserva no encontrada"));
        }
        
        notificacionService.enviarConfirmacionReserva(reservaOpt.get());
        return ResponseEntity.ok(ApiResponse.success("Email de confirmación enviado exitosamente", null));
    }

    @PostMapping("/enviar")
    public ResponseEntity<ApiResponse<Void>> enviarNotificacionPersonalizada(
            @RequestParam String destinatario,
            @RequestParam String asunto,
            @RequestParam String mensaje,
            @RequestHeader("Authorization") String token) {
        
        notificacionService.enviarNotificacionPersonalizada(destinatario, asunto, mensaje, token);
        return ResponseEntity.ok(ApiResponse.success("Notificación enviada exitosamente", null));
    }
}
