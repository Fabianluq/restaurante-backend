package com.example.restaurApp.service;

import com.example.restaurApp.entity.EstadoReserva;
import com.example.restaurApp.repository.EstadoReservaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstadoReservaService {
    private EstadoReservaRepository estadoReservaRepository;
    public EstadoReservaService(EstadoReservaRepository estadoReservaRepository) {
        this.estadoReservaRepository = estadoReservaRepository;
    }

    public EstadoReserva crearEstadoReserva(EstadoReserva estadoReserva) {
        return estadoReservaRepository.save(estadoReserva);
    }

    public List<EstadoReserva> ListarEstadoReserva() {
        return estadoReservaRepository.findAll();
    }

    public Optional<EstadoReserva> listasEstadoReservaPorId(Long id) {
        return estadoReservaRepository.findById(id);
    }

    public EstadoReserva actualizarEstadoReserva(Long id, EstadoReserva estadoReserva) {
        return estadoReservaRepository.findById(id)
                .map (em ->{
                    em.setDescripcion(estadoReserva.getDescripcion());
                    return estadoReservaRepository.save(em);
                }).orElseThrow(() -> new RuntimeException("Estado de reserva no encontrao"));
    }

    public void eliminarEstadoReserva(Long id){
        if(!estadoReservaRepository.existsById(id)){
            throw new RuntimeException("Estado de reserva no encontrado con id: " + id);
        }
        estadoReservaRepository.deleteById(id);
    }
}
