package com.example.restaurApp.mapper;

import com.example.restaurApp.dto.CategoriaRequest;
import com.example.restaurApp.dto.CategoriaResponse;
import com.example.restaurApp.entity.Categoria;

public class CategoriaMapper {
    public static CategoriaResponse toResponse (Categoria categoria) {
        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getDescripcion()
        );
    }

    public static Categoria toEntity(CategoriaRequest request) {
        Categoria categoria = new Categoria();
        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());
        return categoria;
    }
}
