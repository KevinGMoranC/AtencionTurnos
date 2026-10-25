package edu.oficina.dao;

import edu.oficina.modelo.Cliente;
import edu.oficina.persistencia.ConexionBD;
import edu.oficina.persistencia.ProveedorConexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class ClienteDAO extends DaoBase implements ClienteRepositorio {
    public ClienteDAO() { this(ConexionBD::obtenerConexion); }
    public ClienteDAO(ProveedorConexion conexiones) { super(conexiones); }

    private Cliente leer(ResultSet r) throws SQLException {
        return new Cliente(r.getInt("codigo"), r.getString("nombre_completo"), r.getString("telefono"), r.getBoolean("activo"));
    }

    public List<Cliente> listar() throws SQLException {
        List<Cliente> resultado = new ArrayList<>();
        try (Connection c = conexiones.abrir();
             PreparedStatement s = c.prepareStatement("SELECT * FROM clientes ORDER BY codigo");
             ResultSet r = s.executeQuery()) {
            while (r.next()) resultado.add(leer(r));
        }
        return resultado;
    }

    public Cliente buscar(int codigo) throws SQLException {
        try (Connection c = conexiones.abrir();
             PreparedStatement s = c.prepareStatement("SELECT * FROM clientes WHERE codigo=?")) {
            s.setInt(1, codigo);
            try (ResultSet r = s.executeQuery()) { return r.next() ? leer(r) : null; }
        }
    }

    public void insertar(Cliente entidad) throws SQLException {
        try (Connection c = conexiones.abrir();
             PreparedStatement s = c.prepareStatement("INSERT INTO clientes(codigo,nombre_completo,telefono,activo) VALUES(?,?,?,?)")) {
            s.setInt(1, entidad.getCodigo());
            s.setString(2, entidad.getNombreCompleto());
            s.setString(3, entidad.getTelefono());
            s.setBoolean(4, entidad.isActivo());
            s.executeUpdate();
        }
    }

    public void actualizar(Cliente entidad) throws SQLException {
        try (Connection c = conexiones.abrir();
             PreparedStatement s = c.prepareStatement("UPDATE clientes SET nombre_completo=?,telefono=?,activo=? WHERE codigo=?")) {
            s.setString(1, entidad.getNombreCompleto());
            s.setString(2, entidad.getTelefono());
            s.setBoolean(3, entidad.isActivo());
            s.setInt(4, entidad.getCodigo());
            if (s.executeUpdate() != 1) throw new SQLException("Cliente no registrado; no se guardaron cambios.");
        }
    }
}
