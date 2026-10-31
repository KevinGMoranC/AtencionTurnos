package edu.oficina.reportes;

/** Fila del resumen por servicio; incluye también servicios sin turnos. */
public record ResumenServicio(int codigoServicio, String nombreServicio,
        long pendientes, long atendidos, long cancelados, long total) { }
