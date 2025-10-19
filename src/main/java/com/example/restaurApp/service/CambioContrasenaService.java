package com.example.restaurApp.service;

import com.example.restaurApp.dto.CambioContrasenaRequest;
import com.example.restaurApp.entity.Empleado;
import com.example.restaurApp.excepciones.Validacion;
import com.example.restaurApp.repository.EmpleadoRepository;
import com.example.restaurApp.security.JwtUtil;
import com.example.restaurApp.util.EmpleadoUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CambioContrasenaService {
    private final EmpleadoRepository empleadoRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public CambioContrasenaService(EmpleadoRepository empleadoRepository, 
                                 PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.empleadoRepository = empleadoRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public void cambiarContrasena(CambioContrasenaRequest request, String token) {
        // Validar token y obtener empleado
        String username = jwtUtil.extractUsername(token.substring(7));
        Empleado empleado = jwtUtil.getEmpleadoFromToken(token.substring(7));
        
        if (empleado == null) {
            throw new Validacion("Token inválido o empleado no encontrado.");
        }

        // Validar que el empleado esté activo
        EmpleadoUtil.validarEmpleadoActivo(empleado);

        // Validar contraseña actual
        if (!passwordEncoder.matches(request.getContrasenaActual(), empleado.getContrasenia())) {
            throw new Validacion("La contraseña actual es incorrecta.");
        }

        // Validar nueva contraseña
        if (request.getNuevaContrasena() == null || request.getNuevaContrasena().trim().isEmpty()) {
            throw new Validacion("La nueva contraseña no puede estar vacía.");
        }

        if (request.getNuevaContrasena().length() < 6) {
            throw new Validacion("La nueva contraseña debe tener al menos 6 caracteres.");
        }

        // Validar confirmación de contraseña
        if (!request.getNuevaContrasena().equals(request.getConfirmarContrasena())) {
            throw new Validacion("La confirmación de contraseña no coincide.");
        }

        // Validar que la nueva contraseña sea diferente a la actual
        if (passwordEncoder.matches(request.getNuevaContrasena(), empleado.getContrasenia())) {
            throw new Validacion("La nueva contraseña debe ser diferente a la contraseña actual.");
        }

        // Cambiar la contraseña
        String nuevaContrasenaHash = passwordEncoder.encode(request.getNuevaContrasena());
        empleado.setContrasenia(nuevaContrasenaHash);
        
        empleadoRepository.save(empleado);
    }

    @Transactional
    public void cambiarContrasenaPorAdmin(Long empleadoId, String nuevaContrasena, String token) {
        // Validar token y obtener empleado administrador
        String username = jwtUtil.extractUsername(token.substring(7));
        Empleado admin = jwtUtil.getEmpleadoFromToken(token.substring(7));
        
        if (admin == null) {
            throw new Validacion("Token inválido o empleado no encontrado.");
        }

        // Validar que el administrador esté activo y tenga rol ADMINISTRADOR
        EmpleadoUtil.validarRolEmpleado(admin, "ADMIN");

        // Buscar el empleado objetivo
        Empleado empleadoObjetivo = empleadoRepository.findById(empleadoId)
            .orElseThrow(() -> new Validacion("Empleado no encontrado."));

        // Validar nueva contraseña
        if (nuevaContrasena == null || nuevaContrasena.trim().isEmpty()) {
            throw new Validacion("La nueva contraseña no puede estar vacía.");
        }

        if (nuevaContrasena.length() < 6) {
            throw new Validacion("La nueva contraseña debe tener al menos 6 caracteres.");
        }

        // Cambiar la contraseña
        String nuevaContrasenaHash = passwordEncoder.encode(nuevaContrasena);
        empleadoObjetivo.setContrasenia(nuevaContrasenaHash);
        
        empleadoRepository.save(empleadoObjetivo);
    }
}
