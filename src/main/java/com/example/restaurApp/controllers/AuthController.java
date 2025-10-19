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
import com.example.restaurApp.excepciones.EmpleadoInactivoException;
import com.example.restaurApp.repository.EmpleadoRepository;
import com.example.restaurApp.security.JwtUtil;
import com.example.restaurApp.util.EmpleadoUtil;

import jakarta.persistence.EntityNotFoundException;

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
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getCorreo(), req.getContrasenia()));

            Empleado emp = empleadoRepository.findByCorreo(req.getCorreo())
                    .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado"));

            EmpleadoUtil.validarEmpleadoActivo(emp);

            String token = jwtUtil.generateToken(emp.getCorreo(), emp.getRol().getNombre(),emp.getId().toString());
            LoginResponse response = new LoginResponse(token, emp.getRol().getNombre());
            return ResponseEntity.ok(ApiResponse.success("Login exitoso", response));
        } catch (BadCredentialsException ex) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Credenciales inválidas", HttpStatus.UNAUTHORIZED.value()));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND.value()));
        } catch (EmpleadoInactivoException ex) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(ex.getMessage(), HttpStatus.FORBIDDEN.value()));
        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/validar-rol")
    public ResponseEntity<ApiResponse<String>> validarRol(@RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        Empleado empleado = jwtUtil.getEmpleadoFromToken(token.substring(7));

        if (empleado == null) {
            return ResponseEntity.ok(ApiResponse.unauthorized("Token inválido"));
        }

        EmpleadoUtil.validarEmpleadoActivo(empleado);

        return ResponseEntity.ok(ApiResponse.success("Rol validado exitosamente", empleado.getRol().getNombre()));
    }

}
