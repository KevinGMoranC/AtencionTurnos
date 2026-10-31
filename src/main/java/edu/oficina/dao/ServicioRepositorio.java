package edu.oficina.dao;
import edu.oficina.modelo.Servicio;
import java.sql.SQLException;
import java.util.List;
public interface ServicioRepositorio {
    List<Servicio> listar() throws SQLException;
    Servicio buscar(int codigo) throws SQLException;
    void insertar(Servicio servicio) throws SQLException;
    void actualizar(Servicio servicio) throws SQLException;
}
