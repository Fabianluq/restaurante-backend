package com.example.restaurApp.service;

import com.example.restaurApp.entity.Empleado;
import com.example.restaurApp.entity.Mesa;
import com.example.restaurApp.repository.EmpleadoRepository;

import com.example.restaurApp.repository.MesaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmpleadoService {
    private EmpleadoRepository empleadoRepository;
    private PasswordEncoder passwordEncoder;
    private MesaRepository mesaRepository;

    public EmpleadoService(EmpleadoRepository empleadoRepository, PasswordEncoder passwordEncoder,  MesaRepository mesaRepository) {
        this.empleadoRepository = empleadoRepository;
        this.passwordEncoder = passwordEncoder;
        this.mesaRepository = mesaRepository;
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

    public String ocuparMesa(Long mesaId, Long empleadoId) {
        Mesa mesa = mesaRepository.findById(mesaId)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));

        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        if (!empleado.getRol().getNombre().equalsIgnoreCase("Mesero")) {
            throw new RuntimeException("Solo los meseros pueden marcar mesas como ocupadas.");
        }

        if (!mesa.getEstado().getDescripcion().equalsIgnoreCase("Reservada")) {
            throw new RuntimeException("La mesa no está reservada o ya está ocupada.");
        }

        mesa.getEstado().setDescripcion("Ocupada");
        mesaRepository.save(mesa);

        return "Mesa " + mesa.getNumero() + " marcada como ocupada correctamente.";
    }

    public void eliminarEmpleado(Long id) {
        if (!empleadoRepository.existsById(id)) {
            throw new RuntimeException("Empleado no encontrado con id: " + id);
        }
        empleadoRepository.deleteById(id);
    }
}
