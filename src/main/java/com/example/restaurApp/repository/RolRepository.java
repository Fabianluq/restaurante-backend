package com.example.restaurApp.repository;

import com.example.restaurApp.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findById(Long id);
    Optional<Rol> findByNombre(String nombre);

}
