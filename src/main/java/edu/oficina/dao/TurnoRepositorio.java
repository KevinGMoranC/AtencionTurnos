package edu.oficina.dao;
import edu.oficina.modelo.Turno;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
public interface TurnoRepositorio {
    List<Turno> listarPendientes() throws SQLException;
    boolean tienePendienteCliente(int codigo) throws SQLException;
    boolean tienePendienteServicio(int codigo) throws SQLException;
    Turno crearPendiente(int cliente, int servicio) throws SQLException;
    Turno finalizar(long numero, Turno.Estado estado, LocalDateTime fecha) throws SQLException;
}
