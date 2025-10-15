package com.example.restaurApp.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class HorarioRestauranteUtil {
    
    // Horarios de funcionamiento
    private static final LocalTime HORA_APERTURA_DIARIA = LocalTime.of(11, 0); // 11:00 AM
    private static final LocalTime HORA_CIERRE_DIARIA = LocalTime.of(22, 0);  // 10:00 PM
    
    // Horarios especiales para fines de semana
    private static final LocalTime HORA_APERTURA_FIN_SEMANA = LocalTime.of(10, 0); // 10:00 AM
    private static final LocalTime HORA_CIERRE_FIN_SEMANA = LocalTime.of(23, 0);   // 11:00 PM
    
    // Tiempo mínimo para reservas (2 horas antes)
    private static final int HORAS_MINIMAS_RESERVA = 2;
    
    // Tiempo máximo para reservas (30 días antes)
    private static final int DIAS_MAXIMOS_RESERVA = 30;

    /**
     * Valida si el restaurante está abierto en la fecha y hora especificadas
     */
    public static boolean estaAbierto(LocalDate fecha, LocalTime hora) {
        LocalDateTime fechaHora = LocalDateTime.of(fecha, hora);
        return estaAbierto(fechaHora);
    }

    /**
     * Valida si el restaurante está abierto en el momento especificado
     */
    public static boolean estaAbierto(LocalDateTime fechaHora) {
        DayOfWeek diaSemana = fechaHora.getDayOfWeek();
        LocalTime hora = fechaHora.toLocalTime();
        
        // Verificar si es fin de semana (sábado o domingo)
        boolean esFinSemana = diaSemana == DayOfWeek.SATURDAY || diaSemana == DayOfWeek.SUNDAY;
        
        LocalTime horaApertura = esFinSemana ? HORA_APERTURA_FIN_SEMANA : HORA_APERTURA_DIARIA;
        LocalTime horaCierre = esFinSemana ? HORA_CIERRE_FIN_SEMANA : HORA_CIERRE_DIARIA;
        
        return !hora.isBefore(horaApertura) && !hora.isAfter(horaCierre);
    }

    /**
     * Valida si se puede hacer una reserva en la fecha y hora especificadas
     */
    public static boolean puedeHacerReserva(LocalDate fecha, LocalTime hora) {
        LocalDateTime fechaHora = LocalDateTime.of(fecha, hora);
        LocalDateTime ahora = LocalDateTime.now();
        
        // Verificar que no sea en el pasado
        if (fechaHora.isBefore(ahora)) {
            return false;
        }
        
        // Verificar que no sea muy lejos en el futuro
        if (fechaHora.isAfter(ahora.plusDays(DIAS_MAXIMOS_RESERVA))) {
            return false;
        }
        
        // Verificar que sea con suficiente anticipación
        if (fechaHora.isBefore(ahora.plusHours(HORAS_MINIMAS_RESERVA))) {
            return false;
        }
        
        // Verificar que el restaurante esté abierto
        return estaAbierto(fecha, hora);
    }

    /**
     * Valida si se puede crear un pedido en el momento actual
     */
    public static boolean puedeCrearPedido() {
        return estaAbierto(LocalDateTime.now());
    }

    /**
     * Obtiene el próximo horario de apertura
     */
    public static LocalDateTime getProximoHorarioApertura() {
        LocalDateTime ahora = LocalDateTime.now();
        DayOfWeek diaSemana = ahora.getDayOfWeek();
        
        // Si es fin de semana
        boolean esFinSemana = diaSemana == DayOfWeek.SATURDAY || diaSemana == DayOfWeek.SUNDAY;
        LocalTime horaApertura = esFinSemana ? HORA_APERTURA_FIN_SEMANA : HORA_APERTURA_DIARIA;
        
        LocalDateTime proximaApertura = LocalDateTime.of(ahora.toLocalDate(), horaApertura);
        
        // Si ya pasó la hora de apertura de hoy, buscar la de mañana
        if (ahora.isAfter(proximaApertura)) {
            proximaApertura = proximaApertura.plusDays(1);
            
            // Verificar si mañana es fin de semana
            DayOfWeek diaManana = proximaApertura.getDayOfWeek();
            boolean esFinSemanaManana = diaManana == DayOfWeek.SATURDAY || diaManana == DayOfWeek.SUNDAY;
            
            if (esFinSemanaManana != esFinSemana) {
                horaApertura = esFinSemanaManana ? HORA_APERTURA_FIN_SEMANA : HORA_APERTURA_DIARIA;
                proximaApertura = LocalDateTime.of(proximaApertura.toLocalDate(), horaApertura);
            }
        }
        
        return proximaApertura;
    }

    /**
     * Obtiene el horario de cierre del día actual
     */
    public static LocalDateTime getHorarioCierreActual() {
        LocalDateTime ahora = LocalDateTime.now();
        DayOfWeek diaSemana = ahora.getDayOfWeek();
        
        boolean esFinSemana = diaSemana == DayOfWeek.SATURDAY || diaSemana == DayOfWeek.SUNDAY;
        LocalTime horaCierre = esFinSemana ? HORA_CIERRE_FIN_SEMANA : HORA_CIERRE_DIARIA;
        
        return LocalDateTime.of(ahora.toLocalDate(), horaCierre);
    }

    /**
     * Obtiene un mensaje descriptivo del horario de funcionamiento
     */
    public static String getMensajeHorarioFuncionamiento() {
        return String.format(
            "Horarios de funcionamiento: Lunes a Viernes %s - %s, Sábados y Domingos %s - %s",
            HORA_APERTURA_DIARIA,
            HORA_CIERRE_DIARIA,
            HORA_APERTURA_FIN_SEMANA,
            HORA_CIERRE_FIN_SEMANA
        );
    }
}
