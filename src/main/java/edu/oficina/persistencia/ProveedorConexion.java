package edu.oficina.persistencia;
import java.sql.Connection;
import java.sql.SQLException;
/** Contrato usado por los DAO; permite conexiones aisladas durante las pruebas. */
@FunctionalInterface
public interface ProveedorConexion {
    Connection abrir() throws SQLException;
}
