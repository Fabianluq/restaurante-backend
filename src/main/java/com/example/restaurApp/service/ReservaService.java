package com.example.restaurApp.service;

import com.example.restaurApp.dto.DisponibilidadResponse;
import com.example.restaurApp.dto.ReservaRequest;
import com.example.restaurApp.entity.*;
import com.example.restaurApp.excepciones.Validacion;
import com.example.restaurApp.repository.*;
import com.example.restaurApp.util.HorarioRestauranteUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {
    private static final Logger logger = LoggerFactory.getLogger(ReservaService.class);
    
    private ReservaRepository  reservaRepository;
    private EstadoReservaRepository estadoReservaRepository;
    private ClienteRepository clienteRepository;
    private MesaRepository mesaRepository;
    private EstadoMesaRepository estadoMesaRepository;
    private NotificacionService notificacionService;

    public ReservaService(ReservaRepository reservaRepository,  EstadoReservaRepository estadoReservaRepository,
                          ClienteRepository clienteRepository, MesaRepository mesaRepository, 
                          EstadoMesaRepository estadoMesaRepository, NotificacionService notificacionService) {
        this.reservaRepository = reservaRepository;
        this.estadoReservaRepository = estadoReservaRepository;
        this.clienteRepository = clienteRepository;
        this.mesaRepository = mesaRepository;
        this.estadoMesaRepository = estadoMesaRepository;
        this.notificacionService = notificacionService;
    }

    @Scheduled(fixedRate = 600000) // 10 minutos
    public void actualizarMesasPorReservasProximas() {
        LocalDateTime ahora = LocalDateTime.now();

        // Buscar reservas confirmadas para hoy
        List<Reserva> reservasConfirmadas = reservaRepository
                .findByEstadoReserva_DescripcionIgnoreCase("Confirmada");

        for (Reserva reserva : reservasConfirmadas) {
            LocalDateTime fechaHoraReserva = LocalDateTime.of(
                    reserva.getFechaReserva(), reserva.getHoraReserva());

            long minutosRestantes = java.time.Duration.between(ahora, fechaHoraReserva).toMinutes();

            // Solo si faltan 10 min o menos
            if (minutosRestantes <= 10 && minutosRestantes >= 0) {
                Mesa mesa = reserva.getMesa();

                // Solo si la mesa aún está disponible
                if (mesa.getEstado().getDescripcion().equalsIgnoreCase("Disponible")) {
                    EstadoMesa estadoReservada = estadoMesaRepository.findByDescripcionIgnoreCase("Reservada")
                            .orElseThrow(() -> new RuntimeException("No se encontró el estado 'Reservada'."));
                    mesa.setEstado(estadoReservada);
                    mesaRepository.save(mesa);
                }
            }
        }
    }

    public Reserva crearReserva(ReservaRequest request) {

        // Validar horarios de funcionamiento del restaurante
        if (!HorarioRestauranteUtil.puedeHacerReserva(request.getFechaReserva(), request.getHoraReserva())) {
            if (!HorarioRestauranteUtil.estaAbierto(request.getFechaReserva(), request.getHoraReserva())) {
                throw new Validacion("El restaurante no está abierto en ese horario. " + 
                    HorarioRestauranteUtil.getMensajeHorarioFuncionamiento());
            } else {
                throw new Validacion("Las reservas deben hacerse con al menos 2 horas de anticipación y máximo 30 días antes.");
            }
        }

        // No permite realizar reservas fuera de los horarios permitidos (validación adicional)
        if (request.getHoraReserva().isBefore(LocalTime.of(11, 0)) ||
                request.getHoraReserva().isAfter(LocalTime.of(22, 0))) {
            throw new Validacion("El horario de reservas es de 11:00 a 22:00.");
        }

        // Buscar si el cliente ya existe por correo
        Cliente cliente = clienteRepository.findByCorreo(request.getCorreoCliente())
                .orElseGet(() -> {
                    Cliente nuevo = new Cliente(
                            request.getNombreCliente(),
                            request.getApellidoCliente(),
                            request.getCorreoCliente(),
                            request.getTelefonoCliente()
                    );
                    return clienteRepository.save(nuevo);
                });

        //Válida que el cliente no tenga otra reserva activa ese día

        boolean tieneReservaActiva = reservaRepository.existsByCliente_IdAndFechaReservaAndEstadoReserva_DescripcionIn(
                cliente.getId(),
                request.getFechaReserva(),
                List.of("Pendiente", "Confirmada")
        );

        if (tieneReservaActiva) {
            throw new Validacion("Ya tienes una reserva activa para esa fecha.");
        }

        // Validar cantidad máxima de personas por reserva
        if (request.getCantidadPersonas() <= 0) {
            throw new Validacion("La cantidad de personas debe ser mayor a 0.");
        }
        
        if (request.getCantidadPersonas() > 12) {
            throw new Validacion("No se pueden hacer reservas para más de 12 personas. Contacte al restaurante para grupos grandes.");
        }

        // Buscar mesas disponibles con capacidad suficiente
        List<Mesa> mesasDisponibles = mesaRepository
                .findByEstado_DescripcionIgnoreCaseAndCapacidadGreaterThanEqual("Disponible", request.getCantidadPersonas());

        if (mesasDisponibles.isEmpty()) {
            throw new Validacion("No hay mesas disponibles para " + request.getCantidadPersonas() + 
                " personas. Consulte disponibilidad para grupos más pequeños.");
        }

        // Asignar la mesa más pequeña posible que cumpla la capacidad
        Mesa mesaAsignada = mesasDisponibles.stream()
                .filter(mesa -> mesa.getCapacidad() >= request.getCantidadPersonas())
                .min((m1, m2) -> Integer.compare(m1.getCapacidad(), m2.getCapacidad()))
                .orElseThrow(() -> new Validacion("No se pudo asignar una mesa adecuada."));

        boolean existeReserva = reservaRepository.existsByMesa_IdAndFechaReservaAndHoraReserva(
                mesaAsignada.getId(),
                request.getFechaReserva(),
                request.getHoraReserva()
        );

        if (existeReserva) {
            throw new RuntimeException("No hay mesas disponibles para ese horario. Intente otro.");
        }

        EstadoReserva estadoPendiente = estadoReservaRepository.findByDescripcionIgnoreCase("Pendiente")
                .orElseThrow(() -> new RuntimeException("No se encontró el estado Pendiente"));

        // Crear reserva
        Reserva reserva = new Reserva();
        reserva.setFechaReserva(request.getFechaReserva());
        reserva.setHoraReserva(request.getHoraReserva());
        reserva.setCantidadPersonas(request.getCantidadPersonas());
        reserva.setCliente(cliente);
        reserva.setMesa(mesaAsignada);
        reserva.setEstadoReserva(estadoPendiente);

        Reserva reservaGuardada = reservaRepository.save(reserva);

        //Actualiza el estado de la mesa a 'Ocupada'
        EstadoMesa estadoOcupada = estadoMesaRepository.findByDescripcionIgnoreCase("Ocupada")
                .orElseThrow(() -> new RuntimeException("No se encontró el estado 'Ocupada'."));
        mesaAsignada.setEstado(estadoOcupada);
        mesaRepository.save(mesaAsignada);

        return reservaGuardada;
    }


    public List<Reserva> listarReservas () {
        return reservaRepository.findAll();
    }

    public Optional<Reserva> buscarReservaPorId(Long id){

        return reservaRepository.findById(id);
    }

    public List<Reserva> listarReservaPorEstado(Long estadoId){
        return reservaRepository.findByEstadoReserva_Id(estadoId);
    }

    public List<Reserva> listarReservaPorCliente(Long clienteId){

        return reservaRepository.findByCliente_Id(clienteId);
    }

    public List<Reserva> listarPorHoraOFecha(LocalDate fecha, LocalTime  hora) {
        if (fecha != null && hora != null) {
            return reservaRepository.findByFechaReservaAndHoraReserva(fecha, hora);
        }else if (fecha != null) {
            return reservaRepository.findByFechaReserva(fecha);
        }else if (hora != null) {
            return reservaRepository.findByHoraReserva(hora);
        }else {
            return reservaRepository.findAll();
        }
    }

    public Reserva actualizarReserva(Long id, ReservaRequest request) {

        Reserva reservaExistente = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        // Válida estado
        if (!reservaExistente.getEstadoReserva().getDescripcion().equalsIgnoreCase("Pendiente")) {
            throw new RuntimeException("Solo se pueden modificar reservas en estado Pendiente.");
        }

        // Actualiza datos del cliente
        Cliente cliente = reservaExistente.getCliente();
        cliente.setNombre(request.getNombreCliente());
        cliente.setApellido(request.getApellidoCliente());
        cliente.setCorreo(request.getCorreoCliente());
        cliente.setTelefono(request.getTelefonoCliente());
        clienteRepository.save(cliente);

        // Validar nueva cantidad de personas
        if (request.getCantidadPersonas() <= 0) {
            throw new Validacion("La cantidad de personas debe ser mayor a 0.");
        }
        
        if (request.getCantidadPersonas() > 12) {
            throw new Validacion("No se pueden hacer reservas para más de 12 personas.");
        }

        // Revisa si cambia cantidad de personas
        if (request.getCantidadPersonas() != reservaExistente.getCantidadPersonas()) {

            // Verificar si la mesa actual sirve
            if (reservaExistente.getMesa().getCapacidad() < request.getCantidadPersonas()) {

                // Buscar nueva mesa disponible con capacidad suficiente
                List<Mesa> mesasDisponibles = mesaRepository
                        .findByEstado_DescripcionIgnoreCaseAndCapacidadGreaterThanEqual("Disponible", request.getCantidadPersonas());

                if (mesasDisponibles.isEmpty()) {
                    throw new Validacion("No hay mesas disponibles para " + request.getCantidadPersonas() + 
                        " personas. Consulte disponibilidad para grupos más pequeños.");
                }
                
                // Asignar la mesa más pequeña posible que cumpla la capacidad
                Mesa nuevaMesa = mesasDisponibles.stream()
                        .filter(mesa -> mesa.getCapacidad() >= request.getCantidadPersonas())
                        .min((m1, m2) -> Integer.compare(m1.getCapacidad(), m2.getCapacidad()))
                        .orElseThrow(() -> new Validacion("No se pudo asignar una mesa adecuada."));
                
                reservaExistente.setMesa(nuevaMesa);
            }
            reservaExistente.setCantidadPersonas(request.getCantidadPersonas());
        }

        // Verifica si cambia fecha o hora
        if (!request.getFechaReserva().equals(reservaExistente.getFechaReserva()) ||
                !request.getHoraReserva().equals(reservaExistente.getHoraReserva())) {

            boolean existeConflicto = reservaRepository.existsByMesa_IdAndFechaReservaAndHoraReserva(
                    reservaExistente.getMesa().getId(),
                    request.getFechaReserva(),
                    request.getHoraReserva()
            );

            if (existeConflicto) {
                throw new RuntimeException("La nueva fecha u hora no está disponible para esa mesa.");
            }
            reservaExistente.setFechaReserva(request.getFechaReserva());
            reservaExistente.setHoraReserva(request.getHoraReserva());
        }

        return reservaRepository.save(reservaExistente);
    }

    public void eliminarReserva(Long id){
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con id: " + id));
        
        // Validar que la reserva se pueda cancelar
        validarCancelacionReserva(reserva);
        
        // Liberar la mesa si está ocupada por esta reserva
        if (reserva.getMesa() != null && 
            reserva.getMesa().getEstado().getDescripcion().equalsIgnoreCase("Ocupada")) {
            
            EstadoMesa estadoDisponible = estadoMesaRepository.findByDescripcionIgnoreCase("Disponible")
                    .orElseThrow(() -> new RuntimeException("No se encontró el estado 'Disponible'."));
            
            reserva.getMesa().setEstado(estadoDisponible);
            mesaRepository.save(reserva.getMesa());
        }
        
        reservaRepository.deleteById(id);
    }

    /**
     * Valida si una reserva se puede cancelar según las reglas de negocio
     */
    private void validarCancelacionReserva(Reserva reserva) {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime fechaHoraReserva = LocalDateTime.of(reserva.getFechaReserva(), reserva.getHoraReserva());
        
        // No se pueden cancelar reservas que ya pasaron
        if (fechaHoraReserva.isBefore(ahora)) {
            throw new Validacion("No se pueden cancelar reservas que ya han pasado.");
        }
        
        // Calcular tiempo restante hasta la reserva
        long horasRestantes = java.time.Duration.between(ahora, fechaHoraReserva).toHours();
        
        // Regla: Se pueden cancelar hasta 2 horas antes
        if (horasRestantes < 2) {
            throw new Validacion("Las reservas solo se pueden cancelar con al menos 2 horas de anticipación. " +
                "Tiempo restante: " + horasRestantes + " horas.");
        }
        
        // Validar estado de la reserva
        String estadoReserva = reserva.getEstadoReserva().getDescripcion();
        if (estadoReserva.equalsIgnoreCase("Cancelada")) {
            throw new Validacion("Esta reserva ya ha sido cancelada.");
        }
        
        if (estadoReserva.equalsIgnoreCase("Completada")) {
            throw new Validacion("No se pueden cancelar reservas que ya han sido completadas.");
        }
    }

    /**
     * Cancela una reserva cambiando su estado a "Cancelada"
     */
    public Reserva cancelarReserva(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con id: " + id));
        
        // Validar que la reserva se pueda cancelar
        validarCancelacionReserva(reserva);
        
        // Buscar estado "Cancelada"
        EstadoReserva estadoCancelada = estadoReservaRepository.findByDescripcionIgnoreCase("Cancelada")
                .orElseThrow(() -> new RuntimeException("No se encontró el estado 'Cancelada'."));
        
        reserva.setEstadoReserva(estadoCancelada);
        
        // Liberar la mesa si está ocupada por esta reserva
        if (reserva.getMesa() != null && 
            reserva.getMesa().getEstado().getDescripcion().equalsIgnoreCase("Ocupada")) {
            
            EstadoMesa estadoDisponible = estadoMesaRepository.findByDescripcionIgnoreCase("Disponible")
                    .orElseThrow(() -> new RuntimeException("No se encontró el estado 'Disponible'."));
            
            reserva.getMesa().setEstado(estadoDisponible);
            mesaRepository.save(reserva.getMesa());
        }
        
        Reserva reservaCancelada = reservaRepository.save(reserva);
        
        // Enviar notificación por email/SMS/WhatsApp
        try {
            notificacionService.enviarCancelacionReserva(reservaCancelada);
        } catch (Exception e) {
            // Log error pero no fallar la operación
            logger.error("Error al enviar notificación de cancelación: {}", e.getMessage());
        }
        
        return reservaCancelada;
    }

    /**
     * Lista todas las reservas de un cliente por su correo electrónico (público)
     */
    public List<Reserva> listarReservasPorCorreo(String correo) {
        return reservaRepository.findByCliente_Correo(correo);
    }

    /**
     * Busca una reserva por ID y valida que pertenezca al correo proporcionado (público)
     */
    public Reserva buscarReservaPorIdYCorreo(Long id, String correo) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new Validacion("Reserva no encontrada."));
        
        if (!reserva.getCliente().getCorreo().equalsIgnoreCase(correo)) {
            throw new Validacion("No tienes permiso para ver esta reserva.");
        }
        
        return reserva;
    }

    /**
     * Cancela una reserva pública validando que pertenezca al correo proporcionado
     */
    public Reserva cancelarReservaPublica(Long id, String correo) {
        Reserva reserva = buscarReservaPorIdYCorreo(id, correo);
        return cancelarReserva(reserva.getId());
    }

    /**
     * Confirma una reserva pública validando que pertenezca al correo proporcionado
     */
    public Reserva confirmarReservaPublica(Long id, String correo) {
        Reserva reserva = buscarReservaPorIdYCorreo(id, correo);
        
        // Validar que la reserva esté en estado pendiente
        if (!reserva.getEstadoReserva().getDescripcion().equalsIgnoreCase("Pendiente")) {
            throw new Validacion("Solo se pueden confirmar reservas pendientes. Estado actual: " + 
                reserva.getEstadoReserva().getDescripcion());
        }
        
        // Cambiar estado a Confirmada
        EstadoReserva estadoConfirmada = estadoReservaRepository.findByDescripcionIgnoreCase("Confirmada")
                .orElseThrow(() -> new Validacion("No se encontró el estado 'Confirmada'."));
        
        reserva.setEstadoReserva(estadoConfirmada);
        Reserva reservaGuardada = reservaRepository.save(reserva);
        
        // Enviar notificación por email/SMS/WhatsApp
        try {
            notificacionService.enviarConfirmacionReserva(reservaGuardada);
        } catch (Exception e) {
            // Log error pero no fallar la operación
            logger.error("Error al enviar notificación de confirmación: {}", e.getMessage());
        }
        
        return reservaGuardada;
    }

    /**
     * Verifica disponibilidad de mesas para una fecha y hora específicas
     * Retorna true si hay mesas disponibles, false en caso contrario
     */
    public DisponibilidadResponse verificarDisponibilidad(LocalDate fecha, LocalTime hora, int cantidadPersonas, Long mesaId) {
        // Validar horarios de funcionamiento
        if (!HorarioRestauranteUtil.estaAbierto(fecha, hora)) {
            return new DisponibilidadResponse(false, "El restaurante no está abierto en ese horario. " + 
                HorarioRestauranteUtil.getMensajeHorarioFuncionamiento());
        }

        if (!HorarioRestauranteUtil.puedeHacerReserva(fecha, hora)) {
            return new DisponibilidadResponse(false, "Las reservas deben hacerse con al menos 2 horas de anticipación y máximo 30 días antes.");
        }

        // Si se especifica una mesa específica, verificar solo esa mesa
        if (mesaId != null) {
            Mesa mesa = mesaRepository.findById(mesaId)
                    .orElseThrow(() -> new Validacion("Mesa no encontrada."));
            
            // Verificar que la mesa esté disponible
            if (!mesa.getEstado().getDescripcion().equalsIgnoreCase("Disponible")) {
                return new DisponibilidadResponse(false, "La mesa #" + mesa.getNumero() + " no está disponible.");
            }
            
            // Verificar capacidad
            if (mesa.getCapacidad() < cantidadPersonas) {
                return new DisponibilidadResponse(false, "La mesa #" + mesa.getNumero() + " no tiene capacidad suficiente para " + cantidadPersonas + " personas. Capacidad máxima: " + mesa.getCapacidad());
            }
            
            // Verificar si ya hay una reserva en esa fecha y hora
            boolean existeReserva = reservaRepository.existsByMesa_IdAndFechaReservaAndHoraReserva(
                    mesaId, fecha, hora);
            
            if (existeReserva) {
                return new DisponibilidadResponse(false, "La mesa #" + mesa.getNumero() + " ya tiene una reserva para ese horario.");
            }
            
            return new DisponibilidadResponse(true, "La mesa #" + mesa.getNumero() + " está disponible.", mesa.getNumero(), mesa.getCapacidad());
        }

        // Si no se especifica mesa, buscar cualquier mesa disponible
        List<Mesa> mesasDisponibles = mesaRepository
                .findByEstado_DescripcionIgnoreCaseAndCapacidadGreaterThanEqual("Disponible", cantidadPersonas);

        if (mesasDisponibles.isEmpty()) {
            return new DisponibilidadResponse(false, "No hay mesas disponibles para " + cantidadPersonas + " personas.");
        }

        // Verificar si hay mesas sin reservas en esa fecha/hora
        List<Mesa> mesasSinReserva = mesasDisponibles.stream()
                .filter(mesa -> !reservaRepository.existsByMesa_IdAndFechaReservaAndHoraReserva(
                        mesa.getId(), fecha, hora))
                .filter(mesa -> mesa.getCapacidad() >= cantidadPersonas)
                .toList();

        if (mesasSinReserva.isEmpty()) {
            return new DisponibilidadResponse(false, "No hay mesas disponibles para ese horario. Intenta otro horario.");
        }

        // Retornar la mesa más pequeña disponible
        Mesa mesaAsignada = mesasSinReserva.stream()
                .min((m1, m2) -> Integer.compare(m1.getCapacidad(), m2.getCapacidad()))
                .orElse(null);

        if (mesaAsignada != null) {
            return new DisponibilidadResponse(true, "Hay mesas disponibles para " + cantidadPersonas + " personas.", 
                    mesaAsignada.getNumero(), mesaAsignada.getCapacidad());
        }

        return new DisponibilidadResponse(false, "No se pudo verificar disponibilidad.");
    }
}
