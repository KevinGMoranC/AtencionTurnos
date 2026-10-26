package edu.oficina.servicios;

import edu.oficina.dao.ClienteRepositorio;
import edu.oficina.dao.ConsultasTurnosRepositorio;
import edu.oficina.modelo.Turno;
import edu.oficina.reportes.ResumenServicio;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/** Valida las consultas antes de delegar SQL al repositorio. No modifica BST ni cola. */
public final class ConsultaService {
    private final ConsultasTurnosRepositorio consultas;
    private final ClienteRepositorio clientes;

    public ConsultaService(ConsultasTurnosRepositorio consultas, ClienteRepositorio clientes) {
        this.consultas = Objects.requireNonNull(consultas);
        this.clientes = Objects.requireNonNull(clientes);
    }

    public List<Turno> listarPorEstado(Turno.Estado estado) throws SQLException {
        return consultas.listarPorEstado(Objects.requireNonNull(estado));
    }

    public List<Turno> historialCliente(int codigo) throws SQLException {
        if (codigo <= 0) throw new IllegalArgumentException("El código debe ser positivo.");
        if (clientes.buscar(codigo) == null) throw new IllegalArgumentException("Cliente no registrado.");
        return consultas.historialCliente(codigo);
    }

    public List<ResumenServicio> resumenPorServicio() throws SQLException {
        return consultas.resumenPorServicio();
    }
}
