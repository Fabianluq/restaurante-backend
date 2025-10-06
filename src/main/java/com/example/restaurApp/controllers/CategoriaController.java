package com.example.restaurApp.controllers;

import com.example.restaurApp.dto.CategoriaRequest;
import com.example.restaurApp.dto.CategoriaResponse;
import com.example.restaurApp.entity.Categoria;
import com.example.restaurApp.mapper.CategoriaMapper;
import com.example.restaurApp.service.CategoriaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {
    private CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @PostMapping
    public ResponseEntity<CategoriaResponse> crearCategoria(@RequestBody CategoriaRequest categoriaRequest) {
        Categoria categoria = CategoriaMapper.toEntity(categoriaRequest);
        Categoria nuevaCategoria = categoriaService.crearCategoria(categoria);
        return ResponseEntity.ok().body(CategoriaMapper.toResponse(nuevaCategoria));
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> ListarCategorias() {
        List<CategoriaResponse> categorias = categoriaService.listarCategorias()
                .stream()
                .map(CategoriaMapper::toResponse)
                .toList();
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/categoria/{nombre}")
    public ResponseEntity<List<CategoriaResponse>> ListarPorNombre(@RequestParam String nombre) {
        List<CategoriaResponse> categorias = categoriaService.listarPorNombre(nombre)
                .stream()
                .map(CategoriaMapper::toResponse)
                .toList();
        return ResponseEntity.ok(categorias);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponse> actualizarCategoria(@RequestBody Long id,
            @RequestBody CategoriaRequest categoriaRequest) {
        Categoria categoria = CategoriaMapper.toEntity(categoriaRequest);
        try {
            Categoria actualizada = categoriaService.actualizarCategoria(id, categoria);
            return ResponseEntity.ok(CategoriaMapper.toResponse(actualizada));

        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> eliminarCtegoria(@PathVariable Long id) {
        try {
            categoriaService.eliminarCategoria(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }

    }

}
