package com.example.restaurApp.controllers;

import com.example.restaurApp.dto.ApiResponse;
import com.example.restaurApp.dto.DashboardResponse;
import com.example.restaurApp.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardResponse>> obtenerDashboard(
            @RequestHeader("Authorization") String token) {
        
        DashboardResponse dashboard = dashboardService.obtenerDashboard(token);
        return ResponseEntity.ok(ApiResponse.success("Dashboard obtenido exitosamente", dashboard));
    }
}
