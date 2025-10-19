package com.example.restaurApp.service;

import com.example.restaurApp.entity.Empleado;
import com.example.restaurApp.entity.EstadoMesa;
import com.example.restaurApp.entity.Mesa;
import com.example.restaurApp.repository.CategoriaRepository;
import com.example.restaurApp.repository.EmpleadoRepository;

import com.example.restaurApp.repository.EstadoMesaRepository;
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
    private EstadoMesaRepository estadoMesaRepository;

    public EmpleadoService(EmpleadoRepository empleadoRepository, PasswordEncoder passwordEncoder,
                           MesaRepository mesaRepository, EstadoMesaRepository estadoMesaRepository) {
        this.empleadoRepository = empleadoRepository;
        this.passwordEncoder = passwordEncoder;
        this.mesaRepository = mesaRepository;
        this.estadoMesaRepository = estadoMesaRepository;
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

        List<String> rolesPermitidos = List.of("MESERO", "ADMIN");

        if (!rolesPermitidos.contains(empleado.getRol().getNombre())) {
            throw new RuntimeException("Solo los meseros o administradores pueden marcar mesas como ocupadas.");
        }

        if (!mesa.getEstado().getDescripcion().equalsIgnoreCase("Disponible")) {
            throw new RuntimeException("La mesa está reservada o ya está ocupada.");
        }

        EstadoMesa estadoOcupada = estadoMesaRepository.findByDescripcionIgnoreCase("Ocupada")
                .orElseThrow(() -> new RuntimeException("No se encontró el estado 'Ocupada' en la base de datos."));

        mesa.setEstado(estadoOcupada);
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
