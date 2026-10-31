package edu.oficina.dao;
import edu.oficina.modelo.Cliente;
import java.sql.SQLException;
import java.util.List;
public interface ClienteRepositorio {
    List<Cliente> listar() throws SQLException;
    Cliente buscar(int codigo) throws SQLException;
    void insertar(Cliente cliente) throws SQLException;
    void actualizar(Cliente cliente) throws SQLException;
}
