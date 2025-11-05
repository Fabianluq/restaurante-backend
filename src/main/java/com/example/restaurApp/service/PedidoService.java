package com.example.restaurApp.service;

import com.example.restaurApp.dto.PedidoRequest;
import com.example.restaurApp.dto.PedidoResponse;
import com.example.restaurApp.entity.*;
import com.example.restaurApp.excepciones.Validacion;
import com.example.restaurApp.mapper.PedidoMapper;
import com.example.restaurApp.repository.*;
import com.example.restaurApp.security.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.example.restaurApp.util.HorarioRestauranteUtil;
import com.example.restaurApp.util.EstadoPedidoUtil;
import com.example.restaurApp.util.EmpleadoUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class PedidoService {
    private JwtUtil jwtUtil;
    private PedidoRepository pedidoRepository;
    private EmpleadoRepository empleadoRepository;
    private EstadoPedidoRepository estadoPedidoRepository;
    private ClienteRepository clienteRepository;
    private MesaRepository mesaRepository;
    private EstadoMesaRepository estadoMesaRepository;

    public PedidoService(PedidoRepository pedidoRepository,  EmpleadoRepository empleadoRepository,
                         EstadoPedidoRepository estadoPedidoRepository, ClienteRepository clienteRepository,
                         MesaRepository mesaRepository,  EstadoMesaRepository estadoMesaRepository, JwtUtil jwtUtil) {
        this.pedidoRepository = pedidoRepository;
        this.empleadoRepository = empleadoRepository;
        this.estadoPedidoRepository = estadoPedidoRepository;
        this.clienteRepository = clienteRepository;
        this.mesaRepository = mesaRepository;
        this.estadoMesaRepository = estadoMesaRepository;
        this.jwtUtil = jwtUtil;
    }

    public Pedido crearPedido(PedidoRequest request, String token) {
        // Validar que el restaurante esté abierto para crear pedidos
        if (!HorarioRestauranteUtil.puedeCrearPedido()) {
            throw new Validacion("No se pueden crear pedidos fuera del horario de funcionamiento. " + 
                HorarioRestauranteUtil.getMensajeHorarioFuncionamiento() + 
                ". Próxima apertura: " + HorarioRestauranteUtil.getProximoHorarioApertura());
        }

        String correoEmpleado = jwtUtil.extractUsername(token);
        Empleado empleado = empleadoRepository.findByCorreo(correoEmpleado)
                .orElseThrow(() -> new Validacion("Empleado no encontrado o no autenticado."));

        // Validar que el empleado esté activo
        EmpleadoUtil.validarEmpleadoActivo(empleado);

        // Validar rol
        if (!empleado.getRol().getNombre().equalsIgnoreCase("MESERO")) {
            throw new Validacion("Solo los empleados con rol de MESERO pueden crear pedidos.");
        }

        // Asignar estado inicial "Pendiente"
        EstadoPedido estadoPendiente = estadoPedidoRepository.findByDescripcionIgnoreCase("Pendiente")
                .orElseThrow(() -> new Validacion("No se encontró el estado Pendiente."));

        // Si el pedido es para llevar, no se asigna mesa
        Mesa mesa = null;
        if (!request.isParaLlevar()) {
            mesa = mesaRepository.findById(request.getMesaId())
                    .orElseThrow(() -> new Validacion("Mesa no encontrada."));
            
            // Validar que la mesa esté disponible (no reservada u ocupada)
            String estadoMesa = mesa.getEstado().getDescripcion();
            if (!estadoMesa.equalsIgnoreCase("Disponible")) {
                throw new Validacion("La mesa " + mesa.getNumero() + " no está disponible. " +
                    "Estado actual: " + estadoMesa + ". Solo se pueden asignar mesas disponibles.");
            }
            
            // Validar que no exista un pedido activo para esta mesa
            boolean tienePedidoActivo = pedidoRepository.existsByMesa_IdAndEstadoPedido_DescripcionIn(
                    mesa.getId(), 
                    List.of("Pendiente", "En preparación")
            );
            
            if (tienePedidoActivo) {
                throw new Validacion("La mesa " + mesa.getNumero() + " ya tiene un pedido activo. " +
                    "No se puede crear otro pedido hasta que el actual esté completado o cancelado.");
            }
        }

        // Manejar cliente según el tipo de pedido
        Cliente cliente;
        
        if (request.isParaLlevar()) {
            // Para pedidos para llevar, los datos del cliente son obligatorios
            if (request.getCorreoCliente() == null || request.getCorreoCliente().trim().isEmpty()) {
                throw new Validacion("Para pedidos para llevar, el correo del cliente es obligatorio.");
            }
            
            if (request.getNombreCliente() == null || request.getNombreCliente().trim().isEmpty()) {
                throw new Validacion("Para pedidos para llevar, el nombre del cliente es obligatorio.");
            }
            
            // Buscar o crear cliente con datos
            cliente = clienteRepository.findByCorreo(request.getCorreoCliente())
                    .orElseGet(() -> {
                        Cliente nuevo = new Cliente(
                                request.getNombreCliente(),
                                request.getApellidoCliente(),
                                request.getCorreoCliente(),
                                request.getTelefonoCliente()
                        );
                        return clienteRepository.save(nuevo);
                    });
        } else {
            // Para pedidos en restaurante, crear cliente anónimo asociado a la mesa
            cliente = new Cliente(
                    "Mesa " + mesa.getId(),
                    "",
                    "mesa_" + mesa.getId() + "_" + System.currentTimeMillis() + "@restaurante.com",
                    ""
            );
            cliente = clienteRepository.save(cliente);
        }

        Pedido pedido = new Pedido();
        pedido.setFechaPedido(LocalDate.now());
        pedido.setHoraPedido(LocalTime.now());
        pedido.setEstadoPedido(estadoPendiente);
        pedido.setEmpleado(empleado);
        pedido.setMesa(mesa);
        pedido.setCliente(cliente);

        return pedidoRepository.save(pedido);
    }


    public List<Pedido> listarPedidos(){
        return pedidoRepository.findAll();
    }

    public List<Pedido> listarPedidosPorEstado(Long estadoId){
        return pedidoRepository.findByEstadoPedido_Id(estadoId);
    }

    public List<Pedido> listarPedidosPorEmpleado(Long empleadoId, String token){
        // Si se proporciona token, validar permisos
        if (token != null && !token.isEmpty()) {
            String correoEmpleado = jwtUtil.extractUsername(token);
            Empleado empleadoLogueado = empleadoRepository.findByCorreo(correoEmpleado)
                    .orElseThrow(() -> new Validacion("Empleado no encontrado."));
            
            // Validar que el empleado esté activo
            EmpleadoUtil.validarEmpleadoActivo(empleadoLogueado);
            
            String rol = empleadoLogueado.getRol().getNombre();
            
            // Si es MESERO, solo puede ver sus propios pedidos
            if (rol.equalsIgnoreCase("MESERO")) {
                if (!empleadoLogueado.getId().equals(empleadoId)) {
                    throw new Validacion("No puedes ver pedidos de otros empleados. Solo puedes ver tus propios pedidos.");
                }
            }
        }
        
        return pedidoRepository.findByEmpleado_Id(empleadoId);
    }
    public List<Pedido> listarPedidosPorMesa(Long mesaId){
        return pedidoRepository.findByMesa_Id(mesaId);
    }

    public List<Pedido> listarPorFechaOhoraOEstado(LocalDate fecha, LocalTime hora, Long estadoId){
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

    public Pedido actualizarPedido(Long id, PedidoRequest request, String token) {
        String correoEmpleado = jwtUtil.extractUsername(token);

        // Validar que el empleado exista
        Empleado empleado = empleadoRepository.findByCorreo(correoEmpleado)
                .orElseThrow(() -> new Validacion("Empleado no encontrado."));

        // Validar que el empleado esté activo
        EmpleadoUtil.validarEmpleadoActivo(empleado);

        // Buscar pedido existente
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new Validacion("Pedido no encontrado."));

        // Validar que solo el mesero creador pueda modificarlo
        if (!pedido.getEmpleado().getCorreo().equalsIgnoreCase(correoEmpleado)) {
            throw new Validacion("Solo el mesero que creó este pedido puede actualizarlo.");
        }

        // Validar que el pedido esté en un estado que permita modificación
        String estadoActual = pedido.getEstadoPedido().getDescripcion();
        if (EstadoPedidoUtil.esEstadoFinal(estadoActual)) {
            throw new Validacion("No se puede modificar un pedido que ya fue " + estadoActual + ".");
        }
        
        if (!EstadoPedidoUtil.esCancelable(estadoActual)) {
            throw new Validacion("No se puede modificar un pedido en estado '" + estadoActual + 
                "'. Solo se pueden modificar pedidos en estado Pendiente o En preparación.");
        }

        // Las fechas y horas se generan automáticamente, no se pueden modificar

        // Si cambia la mesa, validar que exista y esté activa
        if (request.getMesaId() != null) {
            Mesa mesa = mesaRepository.findById(request.getMesaId())
                    .orElseThrow(() -> new Validacion("Mesa no encontrada."));
            if (mesa.getEstado().getDescripcion().equalsIgnoreCase("Ocupada")) {
                throw new Validacion("La mesa seleccionada está ocupada.");
            }
            pedido.setMesa(mesa);
        }
        // Actualizar estado si aplica (con validación por rol)
        if (request.getEstadoId() != null) {
            EstadoPedido nuevoEstado = estadoPedidoRepository.findById(request.getEstadoId())
                    .orElseThrow(() -> new Validacion("Estado no encontrado."));

            String rol = jwtUtil.extractRol(token);

            // Validar permisos según el nuevo estado
            switch (nuevoEstado.getDescripcion().toLowerCase()) {
                case "en preparación":
                    if (!rol.equalsIgnoreCase("COCINERO")) {
                        throw new Validacion("Solo el cocinero puede cambiar el estado a 'En preparación'.");
                    }
                    break;
                case "entregado":
                    if (!rol.equalsIgnoreCase("MESERO")) {
                        throw new Validacion("Solo el mesero puede marcar el pedido como 'Entregado'.");
                    }
                    break;
                case "cancelado":
                    if (!rol.equalsIgnoreCase("ADMIN") && !rol.equalsIgnoreCase("MESERO")) {
                        throw new Validacion("Solo el administrador o el mesero pueden cancelar pedidos.");
                    }
                    // Liberar la mesa si aplica
                    if (pedido.getMesa() != null) {
                        EstadoMesa disponible = estadoMesaRepository.findByDescripcionIgnoreCase("Disponible")
                                .orElseThrow(() -> new Validacion("No se encontró el estado 'Disponible'."));
                        pedido.getMesa().setEstado(disponible);
                        mesaRepository.save(pedido.getMesa());
                    }
                    break;
            }
            pedido.setEstadoPedido(nuevoEstado);
        }
        // Registrar fecha de actualización
        pedido.setFechaPedido(pedido.getFechaPedido()); // no cambia
        pedido.setHoraPedido(pedido.getHoraPedido());   // no cambia

        // Guardar cambios
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido cambiarEstado(Long idPedido, Long idNuevoEstado, String token) {
        String correoEmpleado = jwtUtil.extractUsername(token);
        Empleado empleado = empleadoRepository.findByCorreo(correoEmpleado)
                .orElseThrow(() -> new Validacion("Empleado no encontrado."));

        // Validar que el empleado esté activo
        EmpleadoUtil.validarEmpleadoActivo(empleado);

        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new Validacion("Pedido no encontrado."));

        EstadoPedido nuevoEstado = estadoPedidoRepository.findById(idNuevoEstado)
                .orElseThrow(() -> new Validacion("Estado no encontrado."));

        String estadoActual = pedido.getEstadoPedido().getDescripcion();
        String estadoDestino = nuevoEstado.getDescripcion();
        String rol = empleado.getRol().getNombre();

        // Validar que no sea un estado final
        if (EstadoPedidoUtil.esEstadoFinal(estadoActual)) {
            throw new Validacion("No se puede modificar un pedido que ya fue " + estadoActual + ".");
        }

        // Validar transición de estado
        if (!EstadoPedidoUtil.esTransicionValida(estadoActual, estadoDestino)) {
            throw new Validacion("No se puede cambiar de '" + estadoActual + "' a '" + estadoDestino + 
                "'. " + EstadoPedidoUtil.getMensajeFlujoEstados());
        }

        // Validar permisos del rol
        if (!EstadoPedidoUtil.rolPuedeCambiarEstado(rol, estadoDestino)) {
            throw new Validacion("El rol '" + rol + "' no puede cambiar el estado a '" + estadoDestino + 
                "'. Estados disponibles: " + EstadoPedidoUtil.getEstadosDisponiblesParaRol(rol));
        }
        
        // Validar que el mesero solo pueda modificar sus propios pedidos
        if (rol.equalsIgnoreCase("MESERO")) {
            if (!pedido.getEmpleado().getCorreo().equalsIgnoreCase(correoEmpleado)) {
                throw new Validacion("Solo puedes modificar pedidos que hayas creado tú mismo.");
            }
        }
        // Validar contenido del pedido antes de preparación
        if (estadoDestino.equalsIgnoreCase("En preparación") && pedido.getDetalles().isEmpty()) {
            throw new Validacion("No se puede poner en preparación un pedido sin productos.");
        }

        pedido.setEstadoPedido(nuevoEstado);

        // Solo liberar la mesa cuando se cancela o se paga (NO cuando se entrega)
        if ((estadoDestino.equalsIgnoreCase("Cancelado") || estadoDestino.equalsIgnoreCase("Pagado"))
                && pedido.getMesa() != null) {
            EstadoMesa disponible = estadoMesaRepository.findByDescripcionIgnoreCase("Disponible")
                    .orElseThrow(() -> new Validacion("No se encontró el estado 'Disponible'."));
            
            boolean otrosPedidosActivos = pedidoRepository.existsByMesa_IdAndEstadoPedido_DescripcionIn(
            pedido.getMesa().getId(),
            List.of("Pendiente", "En preparación"));

            if (!otrosPedidosActivos) {
                pedido.getMesa().setEstado(disponible);
                mesaRepository.save(pedido.getMesa());
            }
        }

        return pedidoRepository.save(pedido);
    }

    public void eliminarPedido(Long id){
        if(!pedidoRepository.existsById(id)){
            throw new RuntimeException("Pedido no encontrado con id: " + id);
        }
        pedidoRepository.deleteById(id);
    }

    /**
     * Método para que los cocineros vean los pedidos pendientes y en preparación
     */
    public List<PedidoResponse> listarPedidosParaCocina(String token) {
        // Validar que el usuario autenticado tenga el rol COCINERO
        String correoEmpleado = jwtUtil.extractUsername(token);
        Empleado empleado = empleadoRepository.findByCorreo(correoEmpleado)
                .orElseThrow(() -> new Validacion("Empleado no encontrado."));

        // Validar que el empleado esté activo
        EmpleadoUtil.validarEmpleadoActivo(empleado);

        String rol = empleado.getRol().getNombre();
        if (!rol.equalsIgnoreCase("COCINERO") && !rol.equalsIgnoreCase("ADMIN")) {
            throw new Validacion("Solo los cocineros o administradores pueden acceder a esta vista.");
        }

        // Listar todos los pedidos cuyo estado sea "Pendiente" o "En preparación"
        // Ordenados por hora del pedido ascendente (los más antiguos primero)
        List<String> estadosCocina = List.of("Pendiente", "En preparación");
        List<Pedido> pedidos = pedidoRepository.findByEstadoPedido_DescripcionInOrderByHoraPedidoAsc(estadosCocina);
        
        // Mapear a PedidoResponse para devolver la información necesaria
        return pedidos.stream()
                .map(PedidoMapper::toResponse)
                .toList();
    }

    /**
     * Calcula el total de un pedido sumando todos sus detalles
     */
    public BigDecimal calcularTotalPedido(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new Validacion("Pedido no encontrado."));

        return pedido.getDetalles().stream()
            .map(detalle -> BigDecimal.valueOf(detalle.getPrecioUnitario()).multiply(BigDecimal.valueOf(detalle.getCantidad())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
