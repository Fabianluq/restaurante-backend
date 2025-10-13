package com.example.restaurApp.service;

import com.example.restaurApp.entity.DetallePedido;
import com.example.restaurApp.repository.DetallePedidoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DetallePedidoService {
    private final DetallePedidoRepository detallePedidoRepository;

    public DetallePedidoService(DetallePedidoRepository detallePedidoRepository) {
        this.detallePedidoRepository = detallePedidoRepository;
    }

    public DetallePedido crearDetalle(DetallePedido detallePedido) {
        return detallePedidoRepository.save(detallePedido);
    }

    public List<DetallePedido> listarDetalles() {
        return detallePedidoRepository.findAll();
    }

    public Optional<DetallePedido> buscarPorId(Long id) {
        return detallePedidoRepository.findById(id);
    }

    public List<DetallePedido> listarPorPedido(Long pedidoId) {
        return detallePedidoRepository.findByPedido_Id(pedidoId);
    }

    public DetallePedido actualizarDetalle(Long id, DetallePedido detallePedido) {
        return detallePedidoRepository.findById(id)
                .map(d -> {
                    d.setCantidad(detallePedido.getCantidad());
                    d.setPrecioUnitario(detallePedido.getPrecioUnitario());
                    d.setProducto(detallePedido.getProducto());
                    d.setPedido(detallePedido.getPedido());
                    return detallePedidoRepository.save(d);
                })
                .orElseThrow(() -> new RuntimeException("Detalle de pedido no encontrado"));
    }

    public void eliminarDetalle(Long id) {
        if (!detallePedidoRepository.existsById(id)) {
            throw new RuntimeException("Detalle no encontrado con id: " + id);
        }
        detallePedidoRepository.deleteById(id);
    }
}
