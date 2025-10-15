package com.example.restaurApp.repository;

import com.example.restaurApp.dto.DetallePedidoRequest;
import com.example.restaurApp.dto.DetallePedidoResponse;
import com.example.restaurApp.entity.DetallePedido;
import com.example.restaurApp.entity.Pedido;
import com.example.restaurApp.entity.Producto;
import com.example.restaurApp.mapper.DetallePedidoMapper;
import com.example.restaurApp.service.DetallePedidoService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {
    List<DetallePedido> findByPedido_Id(Long pedidoId);
    List<DetallePedido> findByProducto_Id(Long productoId);
    Optional<DetallePedido> findByPedido_IdAndProducto_Id(Long pedidoId, Long id);

}
