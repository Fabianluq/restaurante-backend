package com.example.restaurApp.service;

import com.example.restaurApp.entity.Categoria;
import com.example.restaurApp.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {
    private CategoriaRepository categoriaRepository;
    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public Categoria crearCategoria(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    public List<Categoria> listarCategorias() {
        return categoriaRepository.findAll();
    }

    public List<Categoria> listarPorNombre(String nombre) {
        return  categoriaRepository.findByNombre(nombre);
    }

    public Categoria actualizarCategoria(Long id, Categoria categoria) {
        return categoriaRepository.findById(id)
                .map(c ->{
                 c.setNombre(categoria.getNombre());
                 c.setDescripcion(categoria.getDescripcion());
                 return categoriaRepository.save(c);
        }).orElseThrow(() -> new RuntimeException("Categoria no encontrada"));
    }

    public void eliminarCategoria(Long id) {
        if(!categoriaRepository.existsById(id)){
            throw new RuntimeException("Categoria no encontrada" + id);
        }
        categoriaRepository.deleteById(id);
    }
}
