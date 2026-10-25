package edu.oficina.servicios;

import edu.oficina.dao.ServicioRepositorio;
import edu.oficina.dao.TurnoRepositorio;
import edu.oficina.modelo.Servicio;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/** Reglas de servicios; no genera turnos ni implementa reportes. */
public final class ServicioService {
    private final ServicioRepositorio repositorio;
    private final TurnoRepositorio turnos;

    public ServicioService(ServicioRepositorio repositorio, TurnoRepositorio turnos) {
        this.repositorio = Objects.requireNonNull(repositorio);
        this.turnos = Objects.requireNonNull(turnos);
    }

    public Servicio registrar(int codigo, String nombre) throws SQLException {
        Servicio servicio = new Servicio(codigo, nombre, true);
        if (repositorio.buscar(codigo) != null) throw new IllegalArgumentException("Código de servicio duplicado.");
        repositorio.insertar(servicio);
        return servicio;
    }

    public Servicio buscar(int codigo) throws SQLException {
        if (codigo <= 0) throw new IllegalArgumentException("El código debe ser positivo.");
        Servicio servicio = repositorio.buscar(codigo);
        if (servicio == null) throw new IllegalArgumentException("Servicio no registrado.");
        return servicio;
    }

    public List<Servicio> listar() throws SQLException { return repositorio.listar(); }

    public Servicio actualizar(int codigo, String nombre) throws SQLException {
        Servicio anterior = buscar(codigo);
        Servicio actualizado = new Servicio(codigo, nombre, anterior.isActivo());
        repositorio.actualizar(actualizado);
        return actualizado;
    }

    public Servicio cambiarEstado(int codigo, boolean activo) throws SQLException {
        Servicio anterior = buscar(codigo);
        if (!activo && turnos.tienePendienteServicio(codigo)) {
            throw new IllegalArgumentException("No se puede desactivar un servicio con turnos pendientes.");
        }
        Servicio actualizado = new Servicio(codigo, anterior.getNombre(), activo);
        repositorio.actualizar(actualizado);
        return actualizado;
    }
}
