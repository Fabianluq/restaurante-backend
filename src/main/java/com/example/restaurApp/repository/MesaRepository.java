package com.example.restaurApp.repository;

import com.example.restaurApp.entity.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Long> {
    Optional<Mesa> findById(Long id);
    List<Mesa> findByEstado_Id(Long estadoId);

}
