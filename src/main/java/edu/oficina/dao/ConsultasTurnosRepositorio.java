package edu.oficina.dao;

import edu.oficina.modelo.Turno;
import edu.oficina.reportes.ResumenServicio;
import java.sql.SQLException;
import java.util.List;

/** Consultas nuevas sin alterar el contrato de escritura del día 2. */
public interface ConsultasTurnosRepositorio {
    Turno buscar(long numero) throws SQLException;
    List<Turno> listarPorEstado(Turno.Estado estado) throws SQLException;
    List<Turno> historialCliente(int codigoCliente) throws SQLException;
    List<ResumenServicio> resumenPorServicio() throws SQLException;
}
