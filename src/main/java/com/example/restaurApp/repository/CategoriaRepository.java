package com.example.restaurApp.repository;

import com.example.restaurApp.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria,Long> {
    List<Categoria> findByNombre(String nombre);
}
