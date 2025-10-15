package com.example.restaurApp.repository;

import com.example.restaurApp.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    
    // Buscar pagos por pedido
    List<Pago> findByPedido_Id(Long pedidoId);
    
    // Verificar si ya existe un pago para un pedido
    boolean existsByPedido_Id(Long pedidoId);
}
