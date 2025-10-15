package com.example.restaurApp.repository;

import com.example.restaurApp.entity.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstadoReservaRepository extends JpaRepository<EstadoReserva, Long> {
    Optional<EstadoReserva> findById(Long id);
    Optional<EstadoReserva> findByDescripcionIgnoreCase(String estado);
}
