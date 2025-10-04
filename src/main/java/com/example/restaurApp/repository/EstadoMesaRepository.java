package com.example.restaurApp.repository;

import com.example.restaurApp.entity.Empleado;
import com.example.restaurApp.entity.EstadoMesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstadoMesaRepository extends JpaRepository<EstadoMesa, Long> {
    public Optional<EstadoMesa> findById(Long id);

}
