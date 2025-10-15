package com.example.restaurApp.repository;

import com.example.restaurApp.entity.EstadoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstadoDetalleRepository extends JpaRepository<EstadoDetalle, Long> {
    Optional<EstadoDetalle> findByDescripcionIgnoreCase(String descripcion);
}
