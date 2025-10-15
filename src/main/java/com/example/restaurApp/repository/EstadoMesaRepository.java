package com.example.restaurApp.repository;

import com.example.restaurApp.entity.EstadoMesa;
import com.example.restaurApp.entity.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstadoMesaRepository extends JpaRepository<EstadoMesa, Long> {
    public Optional<EstadoMesa> findById(Long id);
    Optional<EstadoMesa> findByDescripcionIgnoreCase(String estado);

}
