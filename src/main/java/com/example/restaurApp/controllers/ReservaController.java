package com.example.restaurApp.controllers;

import com.example.restaurApp.dto.ReservaRequest;
import com.example.restaurApp.dto.ReservaResponse;
import com.example.restaurApp.entity.Cliente;
import com.example.restaurApp.entity.EstadoReserva;
import com.example.restaurApp.entity.Mesa;
import com.example.restaurApp.entity.Reserva;
import com.example.restaurApp.mapper.ReservaMapper;
import com.example.restaurApp.repository.ClienteRepository;
import com.example.restaurApp.repository.EstadoReservaRepository;
import com.example.restaurApp.repository.MesaRepository;
import com.example.restaurApp.service.ReservaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


@RestController
@RequestMapping("/reservas")
public class ReservaController {
    private ReservaService reservaService;
    private ClienteRepository clienteRepository;
    private MesaRepository mesaRepository;
    private EstadoReservaRepository estadoReservaRepository;

    public ReservaController(EstadoReservaRepository estadoReservaRepository, MesaRepository mesaRepository,
                             ClienteRepository clienteRepository, ReservaService reservaService) {
        this.estadoReservaRepository = estadoReservaRepository;
        this.mesaRepository = mesaRepository;
        this.clienteRepository = clienteRepository;
        this.reservaService = reservaService;
    }

    @PostMapping
    public ResponseEntity <ReservaResponse> crearReserva(@RequestBody ReservaRequest reservaRequest){
        Cliente cliente = clienteRepository.findById(reservaRequest.getClienteId())
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        Mesa mesa = mesaRepository.findById(reservaRequest.getMesaId())
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));
        EstadoReserva estadoReserva = estadoReservaRepository.findById(reservaRequest.getEstadoId())
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
        Reserva reserva = ReservaMapper.toEntity(reservaRequest, cliente, mesa, estadoReserva);
        Reserva nuevaReserva = reservaService.crearReserva(reserva);
        return ResponseEntity.ok(ReservaMapper.toResponse(reserva));
    }

    @GetMapping
    public ResponseEntity <List<ReservaResponse>> listarReservas(){
        List<ReservaResponse> reservas = reservaService.listarReservas()
                .stream()
                .map(ReservaMapper::toResponse)
                .toList();
        return ResponseEntity.ok(reservas);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponse> buscarReservaPorId(@PathVariable Long id){
        return reservaService.buscarReservaPorId(id).map(ReservaMapper::toResponse).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/estado/{estadoId}")
    public ResponseEntity<List<ReservaResponse>> buscarReservaPorEstado(@PathVariable Long estadoId){
        List<ReservaResponse> reservas = reservaService.ListarReservaPorEstado(estadoId)
                .stream()
                .map(ReservaMapper::toResponse)
                .toList();
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<ReservaResponse>> buscarReservaPorCliente(@PathVariable Long clienteId){
        List<ReservaResponse> reservas = reservaService.ListarReservaPorCliente(clienteId)
                .stream()
                .map(ReservaMapper::toResponse)
                .toList();
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/buscar")
    public ResponseEntity <List<ReservaResponse>> ListarPorHoraOFecha(@RequestParam(required = false) LocalDate fecha,
                                                                      @RequestParam(required = false) LocalTime hora) {
        List<ReservaResponse> reservas = reservaService.ListarPorHoraOFecha(fecha,hora)
                .stream()
                .map(ReservaMapper::toResponse)
                .toList();
        return ResponseEntity.ok(reservas);
    }

    @PutMapping("/{id}")
    public  ResponseEntity<ReservaResponse> actualizarReserva(@PathVariable Long id,
                                                                @RequestBody ReservaRequest reservaRequest){
        Cliente cliente = clienteRepository.findById(reservaRequest.getClienteId())
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        Mesa mesa = mesaRepository.findById(reservaRequest.getMesaId())
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));
        EstadoReserva estadoReserva = estadoReservaRepository.findById(reservaRequest.getEstadoId())
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
        Reserva reserva = ReservaMapper.toEntity(reservaRequest, cliente, mesa, estadoReserva);
        try {
            reservaService.actualizarReserva(id, reserva);
            return ResponseEntity.ok().body(ReservaMapper.toResponse(reserva));
        }catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> eliminarReserva(@PathVariable Long id){
        try{
            reservaService.eliminarReserva(id);
            return ResponseEntity.noContent().build();
        }catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }

    }

}
