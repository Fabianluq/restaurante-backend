package com.example.restaurApp.repository;

import com.example.restaurApp.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByEstadoPedido_Id(Long estadoId);
    List<Pedido> findByEmpleado_Id(Long empleadoId);
    List<Pedido> findByMesa_Id(Long mesaId);
    List<Pedido> findByFechaPedido(LocalDate fechaPedido);
    List<Pedido> findByHoraPedido(LocalTime horaPedido);
    List<Pedido> findByFechaPedidoAndHoraPedidoAndEstadoPedido_Id(LocalDate fechaPedido,LocalTime horaPedido, Long estadoId);
    
    // Validar si existe un pedido activo para una mesa específica
    boolean existsByMesa_IdAndEstadoPedido_DescripcionIn(Long mesaId, List<String> estadosActivos);
    
    // Buscar pedidos por estados específicos, ordenados por hora del pedido
    List<Pedido> findByEstadoPedido_DescripcionInOrderByHoraPedidoAsc(List<String> estados);

}
