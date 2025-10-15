package com.example.restaurApp;

import com.example.restaurApp.entity.*;
import com.example.restaurApp.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
public abstract class TestDataLoader {

    @Autowired protected RolRepository rolRepository;
    @Autowired protected EmpleadoRepository empleadoRepository;
    @Autowired protected ClienteRepository clienteRepository;
    @Autowired protected MesaRepository mesaRepository;
    @Autowired protected EstadoPedidoRepository estadoPedidoRepository;
    @Autowired protected EstadoMesaRepository estadoMesaRepository;
    @Autowired protected EstadoDetalleRepository estadoDetalleRepository;
    @Autowired protected EstadoProductoRepository estadoProductoRepository;
    @Autowired protected ProductoRepository productoRepository;
    @Autowired protected PedidoRepository pedidoRepository;
    @Autowired protected DetallePedidoRepository detallePedidoRepository;

    @BeforeEach
    void seed() {
        if (rolRepository.count() == 0) {
            Rol r1 = new Rol(); r1.setNombre("ADMIN"); r1.setDescripcion("Administrador");
            Rol r2 = new Rol(); r2.setNombre("MESERO"); r2.setDescripcion("Mesero");
            Rol r3 = new Rol(); r3.setNombre("COCINERO"); r3.setDescripcion("Cocinero");
            rolRepository.saveAll(List.of(r1, r2, r3));
        }

        if (estadoMesaRepository.count() == 0) {
            estadoMesaRepository.saveAll(List.of(
                new EstadoMesa("Disponible"),
                new EstadoMesa("Ocupada"),
                new EstadoMesa("Reservada")
            ));
        }

        if (estadoPedidoRepository.count() == 0) {
            estadoPedidoRepository.saveAll(List.of(
                new EstadoPedido("Pendiente"),
                new EstadoPedido("En preparación"),
                new EstadoPedido("Listo"),
                new EstadoPedido("Entregado"),
                new EstadoPedido("Pagado"),
                new EstadoPedido("Cancelado")
            ));
        }

        if (empleadoRepository.count() == 0) {
            Rol mesero = rolRepository.findByNombre("MESERO").orElseThrow();
            Rol admin = rolRepository.findByNombre("ADMIN").orElseThrow();
            empleadoRepository.saveAll(List.of(
                crearEmpleado("Ana", "Lopez", "ana@rest.com", mesero),
                crearEmpleado("Luis", "Diaz", "luis@rest.com", mesero),
                crearEmpleado("Carlos", "Ruiz", "carlos@rest.com", mesero),
                crearEmpleado("Marta", "Gil", "marta@rest.com", admin),
                crearEmpleado("Jorge", "Soto", "jorge@rest.com", admin)
            ));
        }

        if (clienteRepository.count() == 0) {
            clienteRepository.saveAll(List.of(
                new Cliente("Juan","Perez","juan@mail.com","111111"),
                new Cliente("Maria","Gomez","maria@mail.com","222222"),
                new Cliente("Pedro","Lopez","pedro@mail.com","333333"),
                new Cliente("Laura","Diaz","laura@mail.com","444444"),
                new Cliente("Sofia","Ruiz","sofia@mail.com","555555")
            ));
        }

        if (mesaRepository.count() == 0) {
            EstadoMesa disp = estadoMesaRepository.findByDescripcionIgnoreCase("Disponible").orElseThrow();
            for (int i=1;i<=5;i++) {
                Mesa m = new Mesa();
                m.setNumero(i);
                m.setCapacidad(4);
                m.setEstado(disp);
                mesaRepository.save(m);
            }
        }

        if (productoRepository.count() == 0) {
            // Seed estados de producto si faltan
            if (estadoProductoRepository.count() == 0) {
                estadoProductoRepository.saveAll(List.of(
                        new EstadoProducto("DISPONIBLE"),
                        new EstadoProducto("NO DISPONIBLE"),
                        new EstadoProducto("AGOTADO"),
                        new EstadoProducto("EN MANTENIMIENTO"),
                        new EstadoProducto("ELIMINADO")
                ));
            }
            productoRepository.saveAll(List.of(
                crearProducto("Sopa", 10.00),
                crearProducto("Jugo", 5.50),
                crearProducto("Postre", 7.25),
                crearProducto("Ensalada", 8.00),
                crearProducto("Cafe", 3.00)
            ));
        }

        if (pedidoRepository.count() == 0) {
            Empleado mesero = empleadoRepository.findByCorreo("ana@rest.com").orElseThrow();
            EstadoPedido pendiente = estadoPedidoRepository.findByDescripcionIgnoreCase("Pendiente").orElseThrow();
            if (estadoDetalleRepository.count() == 0) {
                estadoDetalleRepository.saveAll(List.of(
                    new EstadoDetalle("Pendiente"),
                    new EstadoDetalle("En preparación"),
                    new EstadoDetalle("Listo")
                ));
            }
            Mesa mesa1 = mesaRepository.findAll().get(0);
            for (int i=0;i<5;i++) {
                Pedido p = new Pedido();
                p.setEmpleado(mesero);
                p.setFechaPedido(LocalDate.now());
                p.setHoraPedido(LocalTime.now());
                p.setEstadoPedido(pendiente);
                p.setMesa(mesa1);
                pedidoRepository.save(p);

                // detalles
                Producto prod = productoRepository.findAll().get(i%5);
                DetallePedido d = new DetallePedido();
                d.setPedido(p);
                d.setProducto(prod);
                d.setCantidad(2);
                d.setPrecioUnitario(prod.getPrecio());
                d.setEstadoDetalle(estadoDetalleRepository.findByDescripcionIgnoreCase("Pendiente").orElseThrow());
                detallePedidoRepository.save(d);
            }
        }
    }

    private Empleado crearEmpleado(String n, String a, String correo, Rol rol) {
        Empleado e = new Empleado();
        e.setNombre(n);
        e.setApellido(a);
        e.setCorreo(correo);
        e.setTelefono("000000");
        e.setContrasenia("$2a$10$hashdemo");
        e.setRol(rol);
        return e;
    }

    private Producto crearProducto(String nombre, double precio) {
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setPrecio(precio);
        EstadoProducto disp = estadoProductoRepository.findByDescripcionIgnoreCase("DISPONIBLE").orElseThrow();
        p.setEstadoProducto(disp);
        return p;
    }
}


