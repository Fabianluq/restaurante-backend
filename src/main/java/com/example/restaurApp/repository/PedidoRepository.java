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

}
