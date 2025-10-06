package com.example.restaurApp.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.example.restaurApp.entity.Empleado;
import com.example.restaurApp.repository.EmpleadoRepository;

import jakarta.transaction.Transactional;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final EmpleadoRepository empleadoRepository;

    public CustomUserDetailsService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Empleado empleado = empleadoRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + correo));

        String nombreRol = empleado.getRol().getNombre();

        return new org.springframework.security.core.userdetails.User(
                empleado.getCorreo(),
                empleado.getContrasenia(),
                List.of(new SimpleGrantedAuthority("ROLE_" + nombreRol.toUpperCase())));
    }
}
