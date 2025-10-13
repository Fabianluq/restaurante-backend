package com.example.restaurApp.service;

import com.example.restaurApp.entity.EstadoPedido;
import com.example.restaurApp.repository.EstadoPedidoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstadoPedidoService {
    private EstadoPedidoRepository estadoPedidoRepository;
    public EstadoPedidoService(EstadoPedidoRepository estadoPedidoRepository) {
        this.estadoPedidoRepository = estadoPedidoRepository;
    }

    public EstadoPedido crearEstadoPedido(EstadoPedido estadoPedido) {
        return estadoPedidoRepository.save(estadoPedido);
    }

    public List<EstadoPedido> ListarEstadoPedido() {
        return estadoPedidoRepository.findAll();
    }

    public Optional<EstadoPedido> listasEstadoPedidoPorId(Long id) {
        return estadoPedidoRepository.findById(id);
    }

    public EstadoPedido actualizarEstadoPedido(Long id, EstadoPedido estadoPedido) {
        return estadoPedidoRepository.findById(id)
                .map (em ->{
                    em.setDescripcion(estadoPedido.getDescripcion());
                    return estadoPedidoRepository.save(em);
                }).orElseThrow(() -> new RuntimeException("Estado de pedido no encontrao"));
    }

    public void eliminarEstadoPedido(Long id){
        if(!estadoPedidoRepository.existsById(id)){
            throw new RuntimeException("Estado de pedido no encontrado con id: " + id);
        }
        estadoPedidoRepository.deleteById(id);
    }
}
