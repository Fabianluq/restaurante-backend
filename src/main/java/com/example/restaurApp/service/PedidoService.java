package com.example.restaurApp.service;

import com.example.restaurApp.entity.Pedido;
import com.example.restaurApp.repository.PedidoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class PedidoService {
    private PedidoRepository pedidoRepository;
    public PedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public Pedido crearPedido (Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    public List<Pedido> ListarPedidos(){
        return pedidoRepository.findAll();
    }

    public List<Pedido> ListarPedidosPorEstado(Long esteadoId){
        return pedidoRepository.findByEstadoPedido_Id(esteadoId);
    }

    public List<Pedido> ListarPedidosPorEmpleado(Long empleadoId){
        return pedidoRepository.findByEmpleado_Id(empleadoId);
    }
    public List<Pedido> ListarPedidosPorMesa(Long mesaId){
        return pedidoRepository.findByMesa_Id(mesaId);
    }

    public List<Pedido> ListarPorFechaOhoraOEstado( LocalDate fecha,LocalTime hora, Long estadoId){
        if (fecha != null && hora != null && estadoId != null)  {
            return pedidoRepository.findByFechaPedidoAndHoraPedidoAndEstadoPedido_Id(fecha, hora, estadoId);
        }else if (fecha != null ){
            return pedidoRepository.findByFechaPedido(fecha);
        }else if (hora != null ){
            return pedidoRepository.findByHoraPedido(hora);
        }else if (estadoId != null){
            return pedidoRepository.findByEstadoPedido_Id(estadoId);
        }else {
            return pedidoRepository.findAll();
        }
    }

    public Pedido actualizarPedido(Long id, Pedido pedido){
        return pedidoRepository.findById(id)
                .map(p ->{
                    p.setFechaPedido(pedido.getFechaPedido());
                    p.setHoraPedido(pedido.getHoraPedido());
                    p.setEstadoPedido(pedido.getEstadoPedido());
                    p.setEmpleado(pedido.getEmpleado());
                    p.setCliente(pedido.getCliente());
                    p.setMesa(pedido.getMesa());
                    return pedidoRepository.save(p);
                })
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

    }

    public void eliminarProducto(Long id){
        if(!pedidoRepository.existsById(id)){
            throw new RuntimeException("Pedido no encontrado con id: " + id);
        }
        pedidoRepository.deleteById(id);
    }
}
