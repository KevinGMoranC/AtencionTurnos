package edu.oficina.dao;
import edu.oficina.persistencia.ProveedorConexion;
import java.util.Objects;
/** Comparte únicamente el proveedor de conexiones, no reglas de negocio. */
abstract class DaoBase {
    protected final ProveedorConexion conexiones;
    protected DaoBase(ProveedorConexion conexiones) {
        this.conexiones = Objects.requireNonNull(conexiones);
    }
}
