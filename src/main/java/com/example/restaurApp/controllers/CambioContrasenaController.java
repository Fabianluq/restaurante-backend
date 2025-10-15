package com.example.restaurApp.controllers;

import com.example.restaurApp.dto.CambioContrasenaRequest;
import com.example.restaurApp.dto.ApiResponse;
import com.example.restaurApp.excepciones.Validacion;
import com.example.restaurApp.service.CambioContrasenaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/empleados")
public class CambioContrasenaController {
    private final CambioContrasenaService cambioContrasenaService;

    public CambioContrasenaController(CambioContrasenaService cambioContrasenaService) {
        this.cambioContrasenaService = cambioContrasenaService;
    }

    @PutMapping("/cambiar-contrasena")
    public ResponseEntity<ApiResponse<Void>> cambiarContrasena(
            @RequestBody CambioContrasenaRequest request,
            @RequestHeader("Authorization") String token) {
        cambioContrasenaService.cambiarContrasena(request, token);
        return ResponseEntity.ok(ApiResponse.success("Contraseña cambiada exitosamente", null));
    }

    @PutMapping("/{id}/cambiar-contrasena")
    public ResponseEntity<ApiResponse<Void>> cambiarContrasenaPorAdmin(
            @PathVariable Long id,
            @RequestBody String nuevaContrasena,
            @RequestHeader("Authorization") String token) {
        cambioContrasenaService.cambiarContrasenaPorAdmin(id, nuevaContrasena, token);
        return ResponseEntity.ok(ApiResponse.success("Contraseña cambiada exitosamente por administrador", null));
    }
}
