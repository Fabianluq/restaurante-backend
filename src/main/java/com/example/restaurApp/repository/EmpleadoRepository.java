package com.example.restaurApp.repository;

import com.example.restaurApp.entity.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface  EmpleadoRepository extends JpaRepository<Empleado,Long> {
    Optional<Empleado> findById(Long id);
    List<Empleado> findByNombre(String nombre);
    Optional<Empleado> findByCorreo(String correo);
    List<Empleado> findByRol_Id(Long rolId);
}
