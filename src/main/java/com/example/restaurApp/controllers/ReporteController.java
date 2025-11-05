package com.example.restaurApp.controllers;

import com.example.restaurApp.dto.ApiResponse;
import com.example.restaurApp.dto.ReporteVentasRequest;
import com.example.restaurApp.dto.ReporteVentasResponse;
import com.example.restaurApp.service.ReporteService;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reportes")
public class ReporteController {
    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @PostMapping("/ventas")
    public ResponseEntity<ApiResponse<ReporteVentasResponse>> generarReporteVentas(
            @Valid @RequestBody ReporteVentasRequest request,
            @RequestHeader("Authorization") String token) {
        
        ReporteVentasResponse reporte = reporteService.generarReporteVentas(request, token);
        return ResponseEntity.ok(ApiResponse.success("Reporte de ventas generado exitosamente", reporte));
    }

    @GetMapping("/ventas/diario")
    public ResponseEntity<ApiResponse<ReporteVentasResponse>> generarReporteVentasDiario(
            @RequestHeader("Authorization") String token) {
        
        // Generar reporte del día actual
        java.time.LocalDate hoy = java.time.LocalDate.now();
        ReporteVentasRequest request = new ReporteVentasRequest(hoy, hoy, "diario");
        
        ReporteVentasResponse reporte = reporteService.generarReporteVentas(request, token);
        return ResponseEntity.ok(ApiResponse.success("Reporte diario generado exitosamente", reporte));
    }

    @GetMapping("/ventas/semanal")
    public ResponseEntity<ApiResponse<ReporteVentasResponse>> generarReporteVentasSemanal(
            @RequestHeader("Authorization") String token) {
        
        // Generar reporte de la semana actual
        java.time.LocalDate hoy = java.time.LocalDate.now();
        java.time.LocalDate inicioSemana = hoy.minusDays(hoy.getDayOfWeek().getValue() - 1);
        java.time.LocalDate finSemana = inicioSemana.plusDays(6);
        
        ReporteVentasRequest request = new ReporteVentasRequest(inicioSemana, finSemana, "semanal");
        
        ReporteVentasResponse reporte = reporteService.generarReporteVentas(request, token);
        return ResponseEntity.ok(ApiResponse.success("Reporte semanal generado exitosamente", reporte));
    }

    @GetMapping("/ventas/mensual")
    public ResponseEntity<ApiResponse<ReporteVentasResponse>> generarReporteVentasMensual(
            @RequestHeader("Authorization") String token) {
        
        // Generar reporte del mes actual
        java.time.LocalDate hoy = java.time.LocalDate.now();
        java.time.LocalDate inicioMes = hoy.withDayOfMonth(1);
        java.time.LocalDate finMes = hoy.withDayOfMonth(hoy.lengthOfMonth());
        
        ReporteVentasRequest request = new ReporteVentasRequest(inicioMes, finMes, "mensual");
        
        ReporteVentasResponse reporte = reporteService.generarReporteVentas(request, token);
        return ResponseEntity.ok(ApiResponse.success("Reporte mensual generado exitosamente", reporte));
    }
}
