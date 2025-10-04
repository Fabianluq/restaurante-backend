package com.example.restaurApp.mapper;

import com.example.restaurApp.dto.CartaResponse;
import com.example.restaurApp.dto.ProductoRequest;
import com.example.restaurApp.dto.ProductoResponse;
import com.example.restaurApp.entity.Categoria;
import com.example.restaurApp.entity.EstadoMesa;
import com.example.restaurApp.entity.EstadoProducto;
import com.example.restaurApp.entity.Producto;

public class ProductoMapper {
    public static ProductoResponse toResponse (Producto producto) {
        return new ProductoResponse(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getCategoria().getNombre(),
                producto.getEstadoProducto().getNombre()
        );
    }

    public static CartaResponse toCartaResponse(Producto producto) {
        return new CartaResponse(
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getCategoria().getNombre()
        );
    }

    public static Producto toEntity(ProductoRequest request, Categoria categoria, EstadoProducto estadoProducto) {
        Producto producto = new Producto();
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setCategoria(categoria);
        producto.setEstadoProducto(estadoProducto);
        return producto;
    }
}
