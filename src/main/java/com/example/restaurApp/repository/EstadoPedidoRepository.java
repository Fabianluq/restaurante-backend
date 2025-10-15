package com.example.restaurApp.repository;

import com.example.restaurApp.entity.EstadoPedido;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EstadoPedidoRepository extends JpaRepository<EstadoPedido, Long> {
    public Optional<EstadoPedido> findById(Long id);
    Optional<EstadoPedido> findByDescripcionIgnoreCase(String estado);
}
