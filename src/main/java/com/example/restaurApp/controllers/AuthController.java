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
import com.example.restaurApp.entity.Empleado;
import com.example.restaurApp.repository.EmpleadoRepository;
import com.example.restaurApp.security.JwtUtil;

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
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {

            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getCorreo(), req.getContrasenia()));

            Empleado emp = empleadoRepository.findByCorreo(req.getCorreo())
                    .orElseThrow(() -> new RuntimeException("Empleado no encontrado en BD"));

            String token = jwtUtil.generateToken(emp.getCorreo(), emp.getRol().getNombre());

            return ResponseEntity.ok(new LoginResponse(token, emp.getRol().getNombre()));
        } catch (BadCredentialsException ex) {
            System.out.println("Error de credenciales para correo: " + req.getCorreo());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }
    }

}
