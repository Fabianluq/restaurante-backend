package com.example.restaurApp.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ReporteVentasRequest {
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String tipoReporte; // "diario", "semanal", "mensual", "personalizado"

    public ReporteVentasRequest() {}

    public ReporteVentasRequest(LocalDate fechaInicio, LocalDate fechaFin, String tipoReporte) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.tipoReporte = tipoReporte;
    }
}
