package com.example.restaurApp.service;

import com.example.restaurApp.dto.DetallePedidoRequest;
import com.example.restaurApp.entity.*;
import com.example.restaurApp.excepciones.Validacion;
import com.example.restaurApp.repository.*;
import com.example.restaurApp.security.JwtUtil;
import com.example.restaurApp.util.EstadoPedidoUtil;
import com.example.restaurApp.util.EstadoDetalleUtil;
import com.example.restaurApp.util.EmpleadoUtil;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DetallePedidoService {
    private JwtUtil jwtUtil;
    private DetallePedidoRepository detallePedidoRepository;
    private PedidoRepository pedidoRepository;
    private ProductoRepository productoRepository;
    private EmpleadoRepository empleadoRepository;
    private EstadoPedidoRepository estadoPedidoRepository;
    private EstadoDetalleRepository estadoDetalleRepository;


    public DetallePedidoService(DetallePedidoRepository detallePedidoRepository,PedidoRepository pedidoRepository,
                                ProductoRepository productoRepository,EmpleadoRepository empleadoRepository,
                                EstadoPedidoRepository estadoPedidoRepository, EstadoDetalleRepository estadoDetalleRepository,
                                JwtUtil jwtUtil) {
        this.detallePedidoRepository = detallePedidoRepository;
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.empleadoRepository = empleadoRepository;
        this.estadoPedidoRepository = estadoPedidoRepository;
        this.estadoDetalleRepository = estadoDetalleRepository;
        this.jwtUtil = jwtUtil;
    }
    @Transactional
    public DetallePedido agregarProducto(Long pedidoId, DetallePedidoRequest request, String token) {
        //Obtener empleado desde token ANTES de cualquier operación de BD
        String correoEmpleado = jwtUtil.extractUsername(token);
        Empleado empleado = empleadoRepository.findByCorreo(correoEmpleado)
                .orElseThrow(() -> new Validacion("Empleado no encontrado."));

        //Validar rol
        if (!empleado.getRol().getNombre().equalsIgnoreCase("Mesero")) {
            throw new Validacion("Solo los meseros pueden agregar productos a un pedido.");
        }

        //Obtener pedido
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new Validacion("Pedido no encontrado."));

        String estadoPedido = pedido.getEstadoPedido().getDescripcion();
        if (EstadoPedidoUtil.esEstadoFinal(estadoPedido)) {
            throw new Validacion("No se pueden agregar productos a un pedido " + estadoPedido + ".");
        }
        
        // Se puede agregar productos cuando está en preparación, pero los nuevos detalles quedan en Pendiente
        // Esto permite que el cliente agregue más productos sin afectar los que ya están en preparación
        
        // Solo se pueden agregar productos si el pedido no está en estado final
        // Los estados finales son: Cancelado, Pagado
        if (EstadoPedidoUtil.esEstadoFinal(estadoPedido)) {
            throw new Validacion("No se pueden agregar productos a un pedido en estado '" + estadoPedido + 
                "'. Los pedidos finalizados no pueden ser modificados.");
        }

        // Obtener producto y validar disponibilidad
        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new Validacion("Producto no encontrado."));

        // Validar que el producto esté disponible
        if (!producto.getEstadoProducto().getDescripcion().equalsIgnoreCase("Disponible")) {
            throw new Validacion("El producto '" + producto.getNombre() + "' no está disponible actualmente. Estado: " + 
                producto.getEstadoProducto().getDescripcion());
        }

        // Validar cantidad mínima y máxima
        if (request.getCantidad() <= 0) {
            throw new Validacion("La cantidad debe ser mayor a 0.");
        }
        
        if (request.getCantidad() > 10) {
            throw new Validacion("No se pueden pedir más de 10 unidades del mismo producto por pedido.");
        }

        //Buscar detalle existente (mismo pedido + mismo producto)
        Optional<DetallePedido> existente = detallePedidoRepository
                .findByPedido_IdAndProducto_Id(pedidoId, producto.getId());

        // Estado "Pendiente" para el detalle nuevo
        EstadoPedido estadoPendiente = estadoPedidoRepository.findByDescripcionIgnoreCase("Pendiente")
                .orElseThrow(() -> new Validacion("No se encontró el estado 'Pendiente'."));
        
        EstadoDetalle estadoDetallePendiente = estadoDetalleRepository.findByDescripcionIgnoreCase("Pendiente")
                .orElseThrow(() -> new Validacion("No se encontró el estado detalle 'Pendiente'."));

        DetallePedido detalle;
        if (existente.isPresent()) {
            // Actualizar cantidad del detalle existente (no crear nuevo)
            detalle = existente.get();
            detalle.setCantidad(detalle.getCantidad() + request.getCantidad());

        } else {
            // Crear nuevo detalle: precioUnitario viene del producto, estadoDetalle = Pendiente
            detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setProducto(producto);
            detalle.setCantidad(request.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setEstadoPedido(estadoPendiente); // cada detalle tiene su propio estado
            detalle.setEstadoDetalle(estadoDetallePendiente); // estado del detalle individual
        }

        // Guardar detalle
        DetallePedido saved = detallePedidoRepository.save(detalle);

        return saved;
    }


    public List<DetallePedido> listarDetalles() {
        return detallePedidoRepository.findAll();
    }

    public Optional<DetallePedido> buscarPorId(Long id) {
        return detallePedidoRepository.findById(id);
    }

    public List<DetallePedido> listarPorPedido(Long pedidoId) {
        return detallePedidoRepository.findByPedido_Id(pedidoId);
    }

    @Transactional
    public DetallePedido actualizarDetalle(Long detalleId, DetallePedidoRequest request, String token) {
        // Extraer datos del token ANTES de cualquier operación de BD
        String correoEmpleado = jwtUtil.extractUsername(token);
        Empleado empleado = empleadoRepository.findByCorreo(correoEmpleado)
                .orElseThrow(() -> new Validacion("Empleado no encontrado."));

        if (!empleado.getRol().getNombre().equalsIgnoreCase("Mesero")) {
            throw new Validacion("Solo los meseros pueden actualizar detalles de pedidos.");
        }

        DetallePedido detalle = detallePedidoRepository.findById(detalleId)
                .orElseThrow(() -> new Validacion("Detalle de pedido no encontrado."));

        Pedido pedido = detalle.getPedido();

        // Validar propietario del pedido
        if (!pedido.getEmpleado().getCorreo().equalsIgnoreCase(correoEmpleado)) {
            throw new Validacion("Solo el mesero que creó el pedido puede modificarlo.");
        }

        // Validar estado del pedido
        String estadoPedido = pedido.getEstadoPedido().getDescripcion();
        if (EstadoPedidoUtil.esEstadoFinal(estadoPedido)) {
            throw new Validacion("No se puede modificar un pedido " + estadoPedido + ".");
        }
        
        // No se puede modificar detalles cuando está en preparación (para evitar confusiones en cocina)
        if (estadoPedido.equalsIgnoreCase("En preparación")) {
            throw new Validacion("No se puede modificar productos de un pedido que está en preparación. " +
                "Para evitar confusiones en cocina, solo se pueden modificar detalles en estado Pendiente.");
        }
        
        if (!EstadoPedidoUtil.esCancelable(estadoPedido)) {
            throw new Validacion("No se puede modificar un pedido en estado '" + estadoPedido + 
                "'. Solo se pueden modificar detalles en estado Pendiente.");
        }

        // Validar estado del detalle
        if (!detalle.getPedido().getEstadoPedido().getDescripcion().equalsIgnoreCase("Pendiente")) {
            throw new Validacion("Solo se pueden modificar los detalles en estado Pendiente.");
        }

        // Validar disponibilidad del producto
        Producto producto = detalle.getProducto();
        if (!producto.getEstadoProducto().getDescripcion().equalsIgnoreCase("Disponible")) {
            throw new Validacion("El producto '" + producto.getNombre() + "' no está disponible. Estado: " + 
                producto.getEstadoProducto().getDescripcion());
        }

        // Validar cantidad
        if (request.getCantidad() <= 0) {
            throw new Validacion("La cantidad debe ser mayor a cero.");
        }
        
        if (request.getCantidad() > 10) {
            throw new Validacion("No se pueden pedir más de 10 unidades del mismo producto por pedido.");
        }

        detalle.setCantidad(request.getCantidad());

        return detallePedidoRepository.save(detalle);
    }

    @Transactional
    public void eliminarDetalle(Long detalleId, String token) {
        // Obtener el correo del empleado autenticado ANTES de cualquier operación de BD
        String correoEmpleado = jwtUtil.extractUsername(token);

        Empleado empleado = empleadoRepository.findByCorreo(correoEmpleado)
                .orElseThrow(() -> new Validacion("Empleado no encontrado."));

        // Buscar el detalle de pedido
        DetallePedido detalle = detallePedidoRepository.findById(detalleId)
                .orElseThrow(() -> new Validacion("Detalle de pedido no encontrado."));

        Pedido pedido = detalle.getPedido();

        // Validar que el mesero dueño del pedido sea quien intenta eliminarlo
        if (!pedido.getEmpleado().getCorreo().equalsIgnoreCase(correoEmpleado)) {
            throw new Validacion("Solo el mesero que creó el pedido puede eliminar un detalle.");
        }

        // Validar estado del pedido
        String estado = pedido.getEstadoPedido().getDescripcion();
        if (EstadoPedidoUtil.esEstadoFinal(estado)) {
            throw new Validacion("No se puede eliminar productos de un pedido " + estado + ".");
        }
        
        // No se puede eliminar detalles cuando está en preparación (para evitar confusiones en cocina)
        if (estado.equalsIgnoreCase("En preparación")) {
            throw new Validacion("No se puede eliminar productos de un pedido que está en preparación. " +
                "Para evitar confusiones en cocina, solo se pueden eliminar detalles en estado Pendiente.");
        }
        
        if (!EstadoPedidoUtil.esCancelable(estado)) {
            throw new Validacion("No se puede eliminar productos de un pedido en estado '" + estado + 
                "'. Solo se pueden eliminar detalles en estado Pendiente.");
        }

        // Eliminar el detalle
        detallePedidoRepository.delete(detalle);

        pedidoRepository.save(pedido);
    }

    /**
     * Método para que los cocineros cambien el estado de los detalles individuales
     */
    @Transactional
    public DetallePedido cambiarEstadoDetalle(Long detalleId, Long nuevoEstadoDetalleId, String token) {
        // Validar que el usuario autenticado tenga el rol COCINERO
        String correoEmpleado = jwtUtil.extractUsername(token);
        Empleado empleado = empleadoRepository.findByCorreo(correoEmpleado)
                .orElseThrow(() -> new Validacion("Empleado no encontrado."));

        // Validar que el empleado esté activo
        EmpleadoUtil.validarEmpleadoActivo(empleado);

        String rol = empleado.getRol().getNombre();
        if (!rol.equalsIgnoreCase("COCINERO") && !rol.equalsIgnoreCase("ADMINISTRADOR")) {
            throw new Validacion("Solo los cocineros o administradores pueden cambiar el estado de los detalles.");
        }

        // Buscar el detalle
        DetallePedido detalle = detallePedidoRepository.findById(detalleId)
                .orElseThrow(() -> new Validacion("Detalle de pedido no encontrado."));

        Pedido pedido = detalle.getPedido();

        // Solo se pueden modificar productos de pedidos "Pendientes" o "En preparación"
        String estadoPedido = pedido.getEstadoPedido().getDescripcion();
        if (!estadoPedido.equalsIgnoreCase("Pendiente") && !estadoPedido.equalsIgnoreCase("En preparación")) {
            throw new Validacion("Solo se pueden modificar detalles de pedidos en estado 'Pendiente' o 'En preparación'.");
        }

        // Obtener el nuevo estado del detalle
        EstadoDetalle nuevoEstadoDetalle = estadoDetalleRepository.findById(nuevoEstadoDetalleId)
                .orElseThrow(() -> new Validacion("Estado de detalle no encontrado."));

        String estadoActual = detalle.getEstadoDetalle().getDescripcion();
        String estadoDestino = nuevoEstadoDetalle.getDescripcion();

        // Validar transición de estado
        if (!EstadoDetalleUtil.esTransicionValidaDetalle(estadoActual, estadoDestino)) {
            throw new Validacion("No se puede cambiar de '" + estadoActual + "' a '" + estadoDestino + 
                "'. " + EstadoDetalleUtil.getMensajeFlujoEstadosDetalle());
        }

        // Cambiar el estado del detalle
        detalle.setEstadoDetalle(nuevoEstadoDetalle);
        DetallePedido detalleActualizado = detallePedidoRepository.save(detalle);

        // Verificar si todos los detalles están "Listo" para cambiar el pedido a "Listo"
        verificarYActualizarEstadoPedido(pedido);

        return detalleActualizado;
    }

    /**
     * Verifica si todos los detalles están "Listo" y actualiza el pedido a "Listo"
     */
    private void verificarYActualizarEstadoPedido(Pedido pedido) {
        List<DetallePedido> detalles = detallePedidoRepository.findByPedido_Id(pedido.getId());
        
        // Verificar si todos los detalles están "Listo"
        boolean todosListos = detalles.stream()
                .allMatch(detalle -> detalle.getEstadoDetalle().getDescripcion().equalsIgnoreCase("Listo"));

        if (todosListos && pedido.getEstadoPedido().getDescripcion().equalsIgnoreCase("En preparación")) {
            // Cambiar el pedido a "Listo"
            EstadoPedido estadoListo = estadoPedidoRepository.findByDescripcionIgnoreCase("Listo")
                    .orElseThrow(() -> new Validacion("No se encontró el estado 'Listo'."));
            pedido.setEstadoPedido(estadoListo);
            pedidoRepository.save(pedido);
        } else if (!todosListos && pedido.getEstadoPedido().getDescripcion().equalsIgnoreCase("Listo")) {
            // Si algún detalle se devuelve a "Pendiente" o "En preparación", el pedido regresa a "En preparación"
            EstadoPedido estadoEnPreparacion = estadoPedidoRepository.findByDescripcionIgnoreCase("En preparación")
                    .orElseThrow(() -> new Validacion("No se encontró el estado 'En preparación'."));
            pedido.setEstadoPedido(estadoEnPreparacion);
            pedidoRepository.save(pedido);
        }
    }
}
