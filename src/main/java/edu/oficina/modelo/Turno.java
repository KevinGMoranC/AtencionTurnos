package edu.oficina.modelo;

import java.time.LocalDateTime;
import java.util.Objects;

/** Datos de un turno. No genera números ni implementa cambios de estado. */
public final class Turno {
    public enum Estado { PENDIENTE, ATENDIDO, CANCELADO }

    private final long numero;
    private final int codigoCliente;
    private final int codigoServicio;
    private final LocalDateTime fechaGeneracion;
    private final Estado estado;
    private final LocalDateTime fechaFinalizacion;

    /** El número deberá provenir de SQLite al implementar la generación de turnos. */
    public Turno(long numero, int codigoCliente, int codigoServicio,
                 LocalDateTime fechaGeneracion, Estado estado,
                 LocalDateTime fechaFinalizacion) {
        if (numero <= 0 || codigoCliente <= 0 || codigoServicio <= 0) {
            throw new IllegalArgumentException("El número y los códigos deben ser positivos.");
        }
        this.fechaGeneracion = Objects.requireNonNull(
                fechaGeneracion, "La fecha de generación es obligatoria.");
        this.estado = Objects.requireNonNull(estado, "El estado es obligatorio.");
        if (estado == Estado.PENDIENTE && fechaFinalizacion != null) {
            throw new IllegalArgumentException("Un pendiente no tiene fecha de finalización.");
        }
        if (estado != Estado.PENDIENTE && fechaFinalizacion == null) {
            throw new IllegalArgumentException("Un turno finalizado necesita su fecha de atención o cancelación.");
        }
        this.numero = numero;
        this.codigoCliente = codigoCliente;
        this.codigoServicio = codigoServicio;
        this.fechaFinalizacion = fechaFinalizacion;
    }

    public long getNumero() { return numero; }
    public int getCodigoCliente() { return codigoCliente; }
    public int getCodigoServicio() { return codigoServicio; }
    public LocalDateTime getFechaGeneracion() { return fechaGeneracion; }
    public Estado getEstado() { return estado; }
    public LocalDateTime getFechaFinalizacion() { return fechaFinalizacion; }
}
