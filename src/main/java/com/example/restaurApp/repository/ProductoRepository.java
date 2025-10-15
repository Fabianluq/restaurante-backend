package com.example.restaurApp.repository;

import com.example.restaurApp.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByCategoria_Id(Long categoriaId);
    List<Producto> findByEstadoProducto_Id(Long estadoId);
    List<Producto> findByEstadoProducto_DescripcionIgnoreCaseOrderByCategoria_NombreAsc(String descripcion);
    List<Producto> findByIdIn(List<Long> ids);

}
