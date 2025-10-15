package com.example.restaurApp.repository;

import com.example.restaurApp.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByEstadoReserva_Id(Long estadoId);
    List<Reserva> findByCliente_Id(Long clienteId);
    List<Reserva> findByFechaReserva(LocalDate fechaReserva);
    List<Reserva> findByHoraReserva(LocalTime horaReserva);
    List<Reserva> findByFechaReservaAndHoraReserva(LocalDate fechaReserva, LocalTime horaReserva);
    boolean existsByMesa_IdAndFechaReservaAndHoraReserva(Long mesaId, LocalDate fechaReserva, LocalTime horaReserva);
    boolean existsByCliente_IdAndFechaReservaAndEstadoReserva_DescripcionIn(Long clienteId, LocalDate fecha, List<String> estados);
    List<Reserva> findByEstadoReserva_DescripcionIgnoreCase(String descripcion);
}
