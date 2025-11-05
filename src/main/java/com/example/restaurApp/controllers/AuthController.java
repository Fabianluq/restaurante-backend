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
import com.example.restaurApp.dto.EmpleadoResponse;
import com.example.restaurApp.dto.SolicitarRecuperacionRequest;
import com.example.restaurApp.dto.ResetearPasswordRequest;
import com.example.restaurApp.dto.CambiarPasswordRequest;
import com.example.restaurApp.entity.Empleado;
import com.example.restaurApp.entity.PasswordResetToken;
import com.example.restaurApp.excepciones.EmpleadoInactivoException;
import com.example.restaurApp.mapper.EmpleadoMapper;
import com.example.restaurApp.repository.EmpleadoRepository;
import com.example.restaurApp.security.JwtUtil;
import com.example.restaurApp.service.EmailService;
import com.example.restaurApp.service.EmpleadoService;
import com.example.restaurApp.service.PasswordResetTokenService;
import com.example.restaurApp.util.EmpleadoUtil;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final EmpleadoRepository empleadoRepository;
    private final JwtUtil jwtUtil;
    private final PasswordResetTokenService passwordResetTokenService;
    private final EmailService emailService;
    private final EmpleadoService empleadoService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, EmpleadoRepository empleadoRepository,
            JwtUtil jwtUtil, PasswordResetTokenService passwordResetTokenService, EmailService emailService,
            EmpleadoService empleadoService, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.empleadoRepository = empleadoRepository;
        this.jwtUtil = jwtUtil;
        this.passwordResetTokenService = passwordResetTokenService;
        this.emailService = emailService;
        this.empleadoService = empleadoService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest req) {
        try {
            authenticationManager.authenticate(
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
        Empleado empleado = jwtUtil.getEmpleadoFromToken(token.substring(7));

        if (empleado == null) {
            return ResponseEntity.ok(ApiResponse.unauthorized("Token inválido"));
        }

        EmpleadoUtil.validarEmpleadoActivo(empleado);

        return ResponseEntity.ok(ApiResponse.success("Rol validado exitosamente", empleado.getRol().getNombre()));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<EmpleadoResponse>> getCurrentUser(Authentication authentication) {
        try {
            String correo = authentication.getName();
            Empleado empleado = empleadoRepository.findByCorreo(correo)
                    .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado"));

            EmpleadoUtil.validarEmpleadoActivo(empleado);

            EmpleadoResponse empleadoResponse = EmpleadoMapper.toResponse(empleado);
            return ResponseEntity.ok(ApiResponse.success("Perfil obtenido exitosamente", empleadoResponse));
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

    @PostMapping("/recuperar-contrasenia")
    public ResponseEntity<ApiResponse<Void>> solicitarRecuperacion(@Valid @RequestBody SolicitarRecuperacionRequest request) {
        try {
            // Buscar empleado por correo
            Optional<Empleado> empleadoOpt = empleadoRepository.findByCorreo(request.getCorreo());
            
            if (empleadoOpt.isPresent()) {
                Empleado empleado = empleadoOpt.get();
                // Generar token de recuperación
                String token = passwordResetTokenService.generarToken(empleado);
                // Enviar correo electrónico
                emailService.enviarEmailRecuperacion(empleado.getCorreo(), token);
            }
            
            // Por seguridad, siempre devolver éxito (no revelar si el correo existe o no)
            return ResponseEntity.ok(ApiResponse.success(
                "Se ha enviado un correo con instrucciones para recuperar tu contraseña", 
                null
            ));
        } catch (Exception ex) {
            // Por seguridad, siempre devolver éxito incluso si hay error
            return ResponseEntity.ok(ApiResponse.success(
                "Se ha enviado un correo con instrucciones para recuperar tu contraseña", 
                null
            ));
        }
    }

    @PostMapping("/resetear-contrasenia")
    public ResponseEntity<ApiResponse<Void>> resetearPassword(@Valid @RequestBody ResetearPasswordRequest request) {
        try {
            // Validar token
            Optional<PasswordResetToken> resetTokenOpt = passwordResetTokenService.validarToken(request.getToken());
            
            if (resetTokenOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Token inválido o expirado", HttpStatus.BAD_REQUEST.value()));
            }

            PasswordResetToken resetToken = resetTokenOpt.get();
            
            // Validar nueva contraseña
            if (request.getNuevaContrasenia() == null || request.getNuevaContrasenia().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("La nueva contraseña no puede estar vacía", HttpStatus.BAD_REQUEST.value()));
            }

            if (request.getNuevaContrasenia().length() < 6) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("La contraseña debe tener al menos 6 caracteres", HttpStatus.BAD_REQUEST.value()));
            }

            // Cambiar contraseña
            empleadoService.cambiarContrasenia(resetToken.getEmpleado().getId(), request.getNuevaContrasenia());
            
            // Marcar token como usado
            passwordResetTokenService.marcarComoUsado(request.getToken());

            return ResponseEntity.ok(ApiResponse.success("Contraseña restablecida exitosamente", null));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Token inválido o expirado", HttpStatus.BAD_REQUEST.value()));
        }
    }

    @PutMapping("/cambiar-contrasenia")
    public ResponseEntity<ApiResponse<Void>> cambiarPassword(
            @Valid @RequestBody CambiarPasswordRequest request,
            Authentication authentication) {
        try {
            String correo = authentication.getName();
            Empleado empleado = empleadoRepository.findByCorreo(correo)
                    .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado"));

            EmpleadoUtil.validarEmpleadoActivo(empleado);

            // Validar contraseña actual
            if (!passwordEncoder.matches(request.getContraseniaActual(), empleado.getContrasenia())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("La contraseña actual es incorrecta", HttpStatus.BAD_REQUEST.value()));
            }

            // Validar nueva contraseña
            if (request.getNuevaContrasenia() == null || request.getNuevaContrasenia().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("La nueva contraseña no puede estar vacía", HttpStatus.BAD_REQUEST.value()));
            }

            if (request.getNuevaContrasenia().length() < 6) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("La contraseña debe tener al menos 6 caracteres", HttpStatus.BAD_REQUEST.value()));
            }

            // Validar que la nueva contraseña sea diferente a la actual
            if (passwordEncoder.matches(request.getNuevaContrasenia(), empleado.getContrasenia())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("La nueva contraseña debe ser diferente a la contraseña actual", HttpStatus.BAD_REQUEST.value()));
            }

            // Cambiar contraseña
            empleadoService.cambiarContrasenia(empleado.getId(), request.getNuevaContrasenia());

            return ResponseEntity.ok(ApiResponse.success("Contraseña cambiada exitosamente", null));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND.value()));
        } catch (EmpleadoInactivoException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(ex.getMessage(), HttpStatus.FORBIDDEN.value()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

}
