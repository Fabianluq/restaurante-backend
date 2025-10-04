package com.example.restaurApp.service;

import com.example.restaurApp.entity.EstadoMesa;
import com.example.restaurApp.repository.EstadoMesaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstadoMesaService {
    private EstadoMesaRepository estadoMesaRepository;
    public EstadoMesaService(EstadoMesaRepository estadoMesaRepository) {
        this.estadoMesaRepository = estadoMesaRepository;
    }

    public EstadoMesa crearEstadoMesa(EstadoMesa estadoMesa) {
        return estadoMesaRepository.save(estadoMesa);
    }

    public List<EstadoMesa> ListarEstadoMesa() {
        return estadoMesaRepository.findAll();
    }

    public Optional<EstadoMesa> listasEstadoMesasPorId(Long id) {
        return estadoMesaRepository.findById(id);
    }

    public EstadoMesa actualizarEstadoMesa(Long id, EstadoMesa estadoMesa) {
        return estadoMesaRepository.findById(id)
           .map (em ->{
            em.setDescripcion(estadoMesa.getDescripcion());
            return estadoMesaRepository.save(em);
        }).orElseThrow(() -> new RuntimeException("Estado de mesa no encontrao"));
    }

    public void eliminarEstadoMesa(Long id){
        if(!estadoMesaRepository.existsById(id)){
            throw new RuntimeException("Estado de mesa no encontrado con id: " + id);
        }
        estadoMesaRepository.deleteById(id);
    }
}
