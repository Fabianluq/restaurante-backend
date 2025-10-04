package com.example.restaurApp.service;

import com.example.restaurApp.entity.EstadoMesa;
import com.example.restaurApp.entity.Mesa;
import com.example.restaurApp.repository.MesaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MesaService {
    private MesaRepository mesaRepository;
    public MesaService(MesaRepository mesaRepository) {
        this.mesaRepository = mesaRepository;
    }

    public Mesa crearMesa (Mesa mesa) {
        return mesaRepository.save(mesa);
    }

    public List<Mesa> ListarMesas() {
        return mesaRepository.findAll();
    }

    public List<Mesa> ListarMesaPorEstado(Long estadoId) {
        return mesaRepository.findByEstado_Id(estadoId);
    }

    public Mesa actualizarMesa(Long id, Mesa mesa) {

        return mesaRepository.findById(id)
                .map(m -> {
                    m.setEstado(mesa.getEstado());
                    m.setCapacidad(mesa.getCapacidad());
                    m.setNumero(mesa.getNumero());
                    return mesaRepository.save(m);
                })
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));
    }

    public void eliminarMesa(Long id) {
        if (!mesaRepository.existsById(id)){
            throw new RuntimeException("Empleado no encontrado con id: " + id);
        }
        mesaRepository.deleteById(id);
    }

}
