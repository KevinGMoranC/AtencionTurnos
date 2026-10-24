package edu.oficina.servicios;

import edu.oficina.dao.TurnoRepositorio;
import edu.oficina.estructuras.ColaTurnos;
import edu.oficina.modelo.Turno;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/** Coordina persistencia y cola: nunca retira ni agrega antes de guardar correctamente. */
public final class TurnoService {
    private final TurnoRepositorio repositorio;
    private final ColaTurnos cola = new ColaTurnos();

    public TurnoService(TurnoRepositorio repositorio) throws SQLException {
        this.repositorio = Objects.requireNonNull(repositorio);
        for (Turno turno : repositorio.listarPendientes()) cola.encolar(turno);
    }

    public Turno generar(int cliente, int servicio) throws SQLException {
        if (cliente <= 0 || servicio <= 0) throw new IllegalArgumentException("Los códigos deben ser positivos.");
        if (cola.tienePendienteCliente(cliente)) {
            throw new IllegalArgumentException("El cliente ya tiene un turno pendiente.");
        }
        // El DAO también comprueba existencia, estado activo y unicidad en SQLite.
        Turno turno = repositorio.crearPendiente(cliente, servicio);
        cola.encolar(turno);
        return turno;
    }

    public Turno consultarSiguiente() { return cola.consultarSiguiente(); }
    public List<Turno> listarPendientes() { return cola.listarPendientes(); }

    public Turno atenderSiguiente() throws SQLException {
        Turno turno = cola.consultarSiguiente();
        if (turno == null) return null;
        Turno atendido = repositorio.finalizar(turno.getNumero(), Turno.Estado.ATENDIDO, LocalDateTime.now());
        cola.desencolar();
        return atendido;
    }

    public Turno cancelar(long numero) throws SQLException {
        if (numero <= 0) throw new IllegalArgumentException("El número debe ser positivo.");
        boolean existe = cola.listarPendientes().stream().anyMatch(t -> t.getNumero() == numero);
        if (!existe) throw new IllegalArgumentException("Solo se puede cancelar un turno pendiente existente.");
        Turno cancelado = repositorio.finalizar(numero, Turno.Estado.CANCELADO, LocalDateTime.now());
        cola.cancelar(numero);
        return cancelado;
    }
}
