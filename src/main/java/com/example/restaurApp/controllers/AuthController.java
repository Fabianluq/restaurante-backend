package com.example.restaurApp.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.restaurApp.dto.LoginRequest;
import com.example.restaurApp.dto.LoginResponse;
import com.example.restaurApp.dto.ApiResponse;
import com.example.restaurApp.entity.Empleado;
import com.example.restaurApp.repository.EmpleadoRepository;
import com.example.restaurApp.security.JwtUtil;
import com.example.restaurApp.util.EmpleadoUtil;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final EmpleadoRepository empleadoRepository;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, EmpleadoRepository empleadoRepository,
            JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.empleadoRepository = empleadoRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getCorreo(), req.getContrasenia()));

        Empleado emp = empleadoRepository.findByCorreo(req.getCorreo())
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado en BD"));

        // Validar que el empleado esté activo
        EmpleadoUtil.validarEmpleadoActivo(emp);

        String token = jwtUtil.generateToken(emp.getCorreo(), emp.getRol().getNombre());
        LoginResponse response = new LoginResponse(token, emp.getRol().getNombre());

        return ResponseEntity.ok(ApiResponse.success("Login exitoso", response));
    }

    @GetMapping("/validar-rol")
    public ResponseEntity<ApiResponse<String>> validarRol(@RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        Empleado empleado = jwtUtil.getEmpleadoFromToken(token.substring(7));
        
        if (empleado == null) {
            return ResponseEntity.ok(ApiResponse.unauthorized("Token inválido"));
        }

        // Validar que el empleado esté activo
        EmpleadoUtil.validarEmpleadoActivo(empleado);

        return ResponseEntity.ok(ApiResponse.success("Rol validado exitosamente", empleado.getRol().getNombre()));
    }

}
