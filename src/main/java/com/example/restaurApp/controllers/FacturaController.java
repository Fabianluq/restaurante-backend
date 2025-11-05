package com.example.restaurApp.controllers;

import com.example.restaurApp.dto.ApiResponse;
import com.example.restaurApp.dto.FacturaResponse;
import com.example.restaurApp.service.FacturaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/facturas")
public class FacturaController {

    private final FacturaService facturaService;

    public FacturaController(FacturaService facturaService) {
        this.facturaService = facturaService;
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<ApiResponse<FacturaResponse>> generarFactura(@PathVariable Long pedidoId) {
        FacturaResponse factura = facturaService.generarFactura(pedidoId);
        return ResponseEntity.ok(ApiResponse.success("Factura generada", factura));
    }
}


