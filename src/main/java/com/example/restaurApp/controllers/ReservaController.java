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
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
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
    @PreAuthorize("hasAnyRole('ADMIN','MESERO')")
    public ResponseEntity<ReservaResponse> crearReserva(@Valid @RequestBody ReservaRequest request) {
        Reserva nuevaReserva = reservaService.crearReserva(request);
        return ResponseEntity.status(201).body(ReservaMapper.toResponse(nuevaReserva));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MESERO')")
    public ResponseEntity <List<ReservaResponse>> listarReservas(){
        List<ReservaResponse> reservas = reservaService.listarReservas()
                .stream()
                .map(ReservaMapper::toResponse)
                .toList();
        return ResponseEntity.ok(reservas);
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MESERO')")
    public ResponseEntity<ReservaResponse> buscarReservaPorId(@PathVariable Long id){
        return reservaService.buscarReservaPorId(id).map(ReservaMapper::toResponse).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/estado/{estadoId}")
    @PreAuthorize("hasAnyRole('ADMIN','MESERO')")
    public ResponseEntity<List<ReservaResponse>> buscarReservaPorEstado(@PathVariable Long estadoId){
        List<ReservaResponse> reservas = reservaService.listarReservaPorEstado(estadoId)
                .stream()
                .map(ReservaMapper::toResponse)
                .toList();
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasAnyRole('ADMIN','MESERO')")
    public ResponseEntity<List<ReservaResponse>> buscarReservaPorCliente(@PathVariable Long clienteId){
        List<ReservaResponse> reservas = reservaService.listarReservaPorCliente(clienteId)
                .stream()
                .map(ReservaMapper::toResponse)
                .toList();
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('ADMIN','MESERO')")
    public ResponseEntity <List<ReservaResponse>> listarPorHoraOFecha(@RequestParam(required = false) LocalDate fecha,
                                                                      @RequestParam(required = false) LocalTime hora) {
        List<ReservaResponse> reservas = reservaService.listarPorHoraOFecha(fecha,hora)
                .stream()
                .map(ReservaMapper::toResponse)
                .toList();
        return ResponseEntity.ok(reservas);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MESERO')")
    public ResponseEntity<ReservaResponse> actualizarReserva(@PathVariable Long id, @Valid @RequestBody ReservaRequest request) {
        try {
            Reserva reservaActualizada = reservaService.actualizarReserva(id, request);
            return ResponseEntity.ok(ReservaMapper.toResponse(reservaActualizada));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN','MESERO')")
    public ResponseEntity<ReservaResponse> cancelarReserva(@PathVariable Long id){
        try{
            Reserva reservaCancelada = reservaService.cancelarReserva(id);
            return ResponseEntity.ok(ReservaMapper.toResponse(reservaCancelada));
        }catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }catch (Exception e){
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarReserva(@PathVariable Long id){
        try{
            reservaService.eliminarReserva(id);
            return ResponseEntity.noContent().build();
        }catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

}
