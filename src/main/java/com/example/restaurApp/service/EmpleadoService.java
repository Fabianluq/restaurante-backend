package com.example.restaurApp.service;

import com.example.restaurApp.entity.Empleado;
import com.example.restaurApp.repository.EmpleadoRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmpleadoService {
    private EmpleadoRepository empleadoRepository;
    private PasswordEncoder passwordEncoder;

    public EmpleadoService(EmpleadoRepository empleadoRepository, PasswordEncoder passwordEncoder) {
        this.empleadoRepository = empleadoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Empleado crearEmpleado(Empleado empleado) {
        empleado.setContrasenia(passwordEncoder.encode(empleado.getContrasenia()));
        return empleadoRepository.save(empleado);
    }

    public List<Empleado> listarEmpleados() {
        return empleadoRepository.findAll();
    }

    public Optional<Empleado> buscarPorId(Long id) {
        return empleadoRepository.findById(id);
    }

    public List<Empleado> buscarPorNombre(String nombre) {
        return empleadoRepository.findByNombre(nombre);
    }

    public Optional<Empleado> buscarPorCorreo(String correo) {
        return empleadoRepository.findByCorreo(correo);
    }

    public List<Empleado> ListarEmpleadoPorRol(Long rolId) {
        return empleadoRepository.findByRol_Id(rolId);
    }

    public Empleado actualizarEmpleado(Long id, Empleado empleado) {
        return empleadoRepository.findById(id)
                .map(e -> {
                    e.setNombre(empleado.getNombre());
                    e.setCorreo(empleado.getCorreo());
                    e.setRol(empleado.getRol());
                    e.setTelefono(empleado.getTelefono());
                    if (empleado.getContrasenia() != null && !empleado.getContrasenia().isBlank()) {
                        e.setContrasenia(passwordEncoder.encode(empleado.getContrasenia()));
                    }
                    return empleadoRepository.save(e);
                })
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
    }

    public void eliminarEmpleado(Long id) {
        if (!empleadoRepository.existsById(id)) {
            throw new RuntimeException("Empleado no encontrado con id: " + id);
        }
        empleadoRepository.deleteById(id);
    }
}
