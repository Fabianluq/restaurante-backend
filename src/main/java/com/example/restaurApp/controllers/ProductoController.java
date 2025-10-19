package com.example.restaurApp.controllers;

import com.example.restaurApp.dto.CartaResponse;
import com.example.restaurApp.dto.ProductoRequest;
import com.example.restaurApp.dto.ProductoResponse;
import com.example.restaurApp.entity.Categoria;
import com.example.restaurApp.entity.EstadoProducto;
import com.example.restaurApp.entity.Producto;
import com.example.restaurApp.mapper.ProductoMapper;
import com.example.restaurApp.repository.CategoriaRepository;
import com.example.restaurApp.repository.EstadoProductoRepository;
import com.example.restaurApp.service.ProductoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/productos")
public class ProductoController {
    private ProductoService productoService;
    private CategoriaRepository categoriaRepository;
    private EstadoProductoRepository estadoProductoRepository;

    public ProductoController(ProductoService productoService, CategoriaRepository categoriaRepository,
                              EstadoProductoRepository estadoProductoRepository) {
        this.productoService = productoService;
        this.categoriaRepository = categoriaRepository;
        this.estadoProductoRepository = estadoProductoRepository;
    }

    @PostMapping
    public ResponseEntity<ProductoResponse> crearProducto(@RequestBody ProductoRequest productoRequest){
        Categoria categoria = categoriaRepository.findById(productoRequest.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada"));
        EstadoProducto estadoProducto = estadoProductoRepository.findById(productoRequest.getEstadoId())
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
        Producto producto = ProductoMapper.toEntity(productoRequest, categoria, estadoProducto);
        Producto nuevoProducto = productoService.crearProducto(producto);
        return ResponseEntity.ok().body(ProductoMapper.toResponse(nuevoProducto));
    }

    @GetMapping
    public ResponseEntity<List<ProductoResponse>> ListarProductos(){
        List<ProductoResponse> productos = productoService.ListarProductos()
                .stream()
                .map(ProductoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> buscarProductoPorId(@PathVariable Long id){
        return productoService.buscarProductoPorId(id).map(ProductoMapper::toResponse).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<ProductoResponse>> ListarProductosPorCategoria(@PathVariable Long categoriaId){
        List<ProductoResponse> productos = productoService.ListarProductosPorCategoria(categoriaId)
                .stream()
                .map(ProductoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(productos);
    }
    @GetMapping("/estadoProducto/{estadoId}")
    public ResponseEntity<List<ProductoResponse>> ListarProductosPorEstado(Long estadoId){
        List<ProductoResponse> productos = productoService.ListarProductosPorEstado(estadoId)
                .stream()
                .map(ProductoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/carta")
    public ResponseEntity<List<CartaResponse>> verCarta() {
        List<CartaResponse> carta = productoService.listarDisponibles()
                .stream()
                .map(ProductoMapper::toCartaResponse)
                .toList();
        return ResponseEntity.ok(carta);
    }

    @PutMapping("/{id}")
    public  ResponseEntity<ProductoResponse> actualizarProducto(@PathVariable Long id,
                                                                @RequestBody ProductoRequest productoRequest){
        Categoria categoria = categoriaRepository.findById(productoRequest.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada"));
        EstadoProducto estadoProducto = estadoProductoRepository.findById(productoRequest.getEstadoId())
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
        Producto producto = ProductoMapper.toEntity(productoRequest, categoria, estadoProducto);
        try {
            productoService.actualizarProducto(id, producto);
            return ResponseEntity.ok().body(ProductoMapper.toResponse(producto));
        }catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id){
        try{
            productoService.eliminarProducto(id);
            return ResponseEntity.noContent().build();
        }catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }

    }

}
