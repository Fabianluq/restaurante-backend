package com.example.restaurApp.service;

import com.example.restaurApp.entity.EstadoProducto;
import com.example.restaurApp.repository.EstadoProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstadoProductoService {
    private EstadoProductoRepository estadoProductoRepository;
    public EstadoProductoService(EstadoProductoRepository estadoMesaRepository) {
        this.estadoProductoRepository = estadoMesaRepository;
    }

    public EstadoProducto crearEstadoProducto(EstadoProducto estadoProducto) {
        return estadoProductoRepository.save(estadoProducto);
    }

    public List<EstadoProducto> ListarEstadoProductos() {
        return estadoProductoRepository.findAll();
    }

    public Optional<EstadoProducto> ListarEstadoPrductosPorId(Long id) {
        return estadoProductoRepository.findById(id);
    }

    public EstadoProducto actualizarEstadoProducto(Long id,EstadoProducto estadoProducto) {
        return estadoProductoRepository.findById(id)
            .map( ep -> {
                ep.setDescripcion(estadoProducto.getDescripcion());
                return  estadoProductoRepository.save(ep);

            }).orElseThrow(() -> new RuntimeException("Estado de producto no encontrao"));
    }

    public void eliminarEstadoProducto(Long id) {

        if (!estadoProductoRepository.existsById(id)) {
            throw new RuntimeException("Estado de prodcuto no encontrado con id: " + id);
        }
        estadoProductoRepository.deleteById(id);
    }

}
