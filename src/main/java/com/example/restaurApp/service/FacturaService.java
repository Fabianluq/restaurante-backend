package com.example.restaurApp.service;

import com.example.restaurApp.dto.FacturaItemResponse;
import com.example.restaurApp.dto.FacturaResponse;
import com.example.restaurApp.entity.DetallePedido;
import com.example.restaurApp.entity.Pedido;
import com.example.restaurApp.excepciones.Validacion;
import com.example.restaurApp.repository.PedidoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FacturaService {

    private final PedidoRepository pedidoRepository;

    public FacturaService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public FacturaResponse generarFactura(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new Validacion("Pedido no encontrado."));

        FacturaResponse factura = new FacturaResponse();
        factura.setPedidoId(pedido.getId());
        factura.setNumeroMesa(pedido.getMesa() != null ? String.valueOf(pedido.getMesa().getNumero()) : "Para llevar");
        factura.setMesero(pedido.getEmpleado().getNombre() + " " + pedido.getEmpleado().getApellido());
        factura.setFechaPedido(pedido.getFechaPedido());
        factura.setHoraPedido(pedido.getHoraPedido().atDate(pedido.getFechaPedido()));

        List<FacturaItemResponse> items = pedido.getDetalles().stream()
                .map(this::mapItem)
                .collect(Collectors.toList());
        factura.setItems(items);

        BigDecimal subtotal = items.stream()
                .map(FacturaItemResponse::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        factura.setSubtotal(subtotal);

        BigDecimal impuestos = BigDecimal.ZERO; // configurable futuro
        BigDecimal propina = BigDecimal.ZERO;   // configurable futuro
        factura.setImpuestos(impuestos);
        factura.setPropina(propina);
        factura.setTotal(subtotal.add(impuestos).add(propina));

        return factura;
    }

    private FacturaItemResponse mapItem(DetallePedido detalle) {
        FacturaItemResponse item = new FacturaItemResponse();
        item.setDetalleId(detalle.getId());
        item.setProducto(detalle.getProducto().getNombre());
        item.setCantidad(detalle.getCantidad());
        item.setPrecioUnitario(BigDecimal.valueOf(detalle.getPrecioUnitario()));
        item.setTotal(BigDecimal.valueOf(detalle.getPrecioUnitario())
                .multiply(BigDecimal.valueOf(detalle.getCantidad())));
        return item;
    }
}


