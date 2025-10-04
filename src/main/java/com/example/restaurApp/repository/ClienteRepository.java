package com.example.restaurApp.repository;

import com.example.restaurApp.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente,Long> {
    Optional<Cliente> findById(Long id);
    List<Cliente> findByNombre(String nombre);
    Optional<Cliente> findByCorreo(String correo);
}
