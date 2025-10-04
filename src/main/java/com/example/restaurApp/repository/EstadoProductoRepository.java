package com.example.restaurApp.repository;

import com.example.restaurApp.entity.EstadoProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstadoProductoRepository  extends JpaRepository<EstadoProducto, Long> {
    Optional<EstadoProducto> findById(Long id);
}
