package com.example.restaurApp.service;

import com.example.restaurApp.entity.EstadoDetalle;
import com.example.restaurApp.repository.EstadoDetalleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstadoDetalleService {
    private final EstadoDetalleRepository estadoDetalleRepository;

    public EstadoDetalleService(EstadoDetalleRepository estadoDetalleRepository) {
        this.estadoDetalleRepository = estadoDetalleRepository;
    }

    public EstadoDetalle crearEstadoDetalle(EstadoDetalle estadoDetalle) {
        return estadoDetalleRepository.save(estadoDetalle);
    }

    public List<EstadoDetalle> listarEstadosDetalle() {
        return estadoDetalleRepository.findAll();
    }

    public Optional<EstadoDetalle> buscarEstadoDetallePorId(Long id) {
        return estadoDetalleRepository.findById(id);
    }

    public Optional<EstadoDetalle> buscarPorDescripcion(String descripcion) {
        return estadoDetalleRepository.findByDescripcionIgnoreCase(descripcion);
    }

    public EstadoDetalle actualizarEstadoDetalle(Long id, EstadoDetalle estadoDetalle) {
        return estadoDetalleRepository.findById(id)
                .map(estado -> {
                    estado.setDescripcion(estadoDetalle.getDescripcion());
                    return estadoDetalleRepository.save(estado);
                })
                .orElseThrow(() -> new RuntimeException("Estado detalle no encontrado con id: " + id));
    }

    public void eliminarEstadoDetalle(Long id) {
        if (!estadoDetalleRepository.existsById(id)) {
            throw new RuntimeException("Estado detalle no encontrado con id: " + id);
        }
        estadoDetalleRepository.deleteById(id);
    }
}
