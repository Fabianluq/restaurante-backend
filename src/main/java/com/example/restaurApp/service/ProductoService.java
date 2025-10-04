package com.example.restaurApp.service;

import com.example.restaurApp.entity.Producto;
import com.example.restaurApp.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {
    private ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {

        this.productoRepository = productoRepository;
    }

    public Producto crearProducto(Producto producto) {

        return productoRepository.save(producto);
    }

    public List<Producto> ListarProductos() {

        return productoRepository.findAll();
    }

    public Optional<Producto> buscarProductoPorId(Long id){

        return productoRepository.findById(id);
    }

    public List<Producto> ListarProductosPorCategoria(Long estadoId){
        return productoRepository.findByCategoria_Id(estadoId);
    }

    public List<Producto> ListarProductosPorEstado(Long estadoId){
        return productoRepository.findByEstadoProducto_Id(estadoId);
    }

    public List<Producto> listarDisponibles() {
        return productoRepository.findByEstadoProducto_NombreIgnoreCaseOrderByCategoria_NombreAsc("DISPONIBLE");
    }

    public Producto actualizarProducto(Long id, Producto producto){
        return productoRepository.findById(id)
           .map(p -> {
           p.setNombre(producto.getNombre());
           p.setDescripcion(producto.getDescripcion());
           p.setPrecio(producto.getPrecio());
           p.setCategoria(producto.getCategoria());
           p.setEstadoProducto(producto.getEstadoProducto());
           return productoRepository.save(p);
           })
           .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    public void eliminarProducto(Long id){
        if(!productoRepository.existsById(id)){
            throw new RuntimeException("Producto no encontrado con id: " + id);
        }
        productoRepository.deleteById(id);
    }
}
