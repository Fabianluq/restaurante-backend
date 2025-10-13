package com.example.restaurApp.service;

import com.example.restaurApp.entity.Reserva;
import com.example.restaurApp.repository.ReservaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {
    private ReservaRepository  reservaRepository;
    public ReservaService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    public Reserva crearReserva(Reserva reserva){
        return reservaRepository.save(reserva);
    }

    public List<Reserva> listarReservas () {
        return reservaRepository.findAll();
    }

    public Optional<Reserva> buscarReservaPorId(Long id){

        return reservaRepository.findById(id);
    }

    public List<Reserva> ListarReservaPorEstado(Long estadoId){
        return reservaRepository.findByEstadoReserva_Id(estadoId);
    }

    public List<Reserva> ListarReservaPorCliente(Long estadoId){

        return reservaRepository.findByCliente_Id(estadoId);
    }

    public List<Reserva> ListarPorHoraOFecha(LocalDate fecha, LocalTime  hora) {
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

    public  Reserva actualizarReserva(Long id, Reserva reserva){
        return reservaRepository.findById(id)
                .map(r -> {
                    r.setFechaReserva(reserva.getFechaReserva());
                    r.setHoraReserva(reserva.getHoraReserva());
                    r.setCantidadPersonas(reserva.getCantidadPersonas());
                    r.setCliente(reserva.getCliente());
                    r.setMesa(reserva.getMesa());
                    r.setEstadoReserva(reserva.getEstadoReserva());
                    return reservaRepository.save(r);
                })
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
    }

    public void eliminarReserva(Long id){
        if(!reservaRepository.existsById(id)){
            throw new RuntimeException("Reserva no encontrada con id: " + id);
        }
        reservaRepository.deleteById(id);
    }
}
