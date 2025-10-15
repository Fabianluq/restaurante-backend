package com.example.restaurApp.controllers;

import com.example.restaurApp.dto.PagoRequest;
import com.example.restaurApp.dto.PagoResponse;
import com.example.restaurApp.dto.ApiResponse;
import com.example.restaurApp.excepciones.Validacion;
import com.example.restaurApp.service.PagoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pagos")
public class PagoController {
    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MESERO','ADMIN')")
    public ResponseEntity<ApiResponse<PagoResponse>> procesarPago(
            @Valid @RequestBody PagoRequest request,
            @RequestHeader("Authorization") String token) {
        PagoResponse response = pagoService.procesarPago(request, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created("Pago procesado exitosamente", response));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PagoResponse>>> listarPagos(
            @RequestHeader("Authorization") String token) {
        List<PagoResponse> pagos = pagoService.listarPagos(token);
        return ResponseEntity.ok(ApiResponse.success("Pagos listados exitosamente", pagos));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PagoResponse>> buscarPagoPorId(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        PagoResponse pago = pagoService.buscarPagoPorId(id, token);
        return ResponseEntity.ok(ApiResponse.success("Pago encontrado", pago));
    }

    @GetMapping("/pedido/{pedidoId}")
    @PreAuthorize("hasAnyRole('ADMIN','MESERO')")
    public ResponseEntity<ApiResponse<List<PagoResponse>>> buscarPagosPorPedido(
            @PathVariable Long pedidoId,
            @RequestHeader("Authorization") String token) {
        List<PagoResponse> pagos = pagoService.buscarPagosPorPedido(pedidoId, token);
        return ResponseEntity.ok(ApiResponse.success("Pagos del pedido listados exitosamente", pagos));
    }
}
