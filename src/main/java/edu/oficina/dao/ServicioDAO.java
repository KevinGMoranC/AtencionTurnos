package edu.oficina.dao;

import edu.oficina.modelo.Servicio;
import edu.oficina.persistencia.ConexionBD;
import edu.oficina.persistencia.ProveedorConexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class ServicioDAO extends DaoBase implements ServicioRepositorio {
    public ServicioDAO() { this(ConexionBD::obtenerConexion); }
    public ServicioDAO(ProveedorConexion conexiones) { super(conexiones); }

    private Servicio leer(ResultSet r) throws SQLException {
        return new Servicio(r.getInt("codigo"), r.getString("nombre"), r.getBoolean("activo"));
    }

    public List<Servicio> listar() throws SQLException {
        List<Servicio> resultado = new ArrayList<>();
        try (Connection c = conexiones.abrir();
             PreparedStatement s = c.prepareStatement("SELECT * FROM servicios ORDER BY codigo");
             ResultSet r = s.executeQuery()) {
            while (r.next()) resultado.add(leer(r));
        }
        return resultado;
    }

    public Servicio buscar(int codigo) throws SQLException {
        try (Connection c = conexiones.abrir();
             PreparedStatement s = c.prepareStatement("SELECT * FROM servicios WHERE codigo=?")) {
            s.setInt(1, codigo);
            try (ResultSet r = s.executeQuery()) { return r.next() ? leer(r) : null; }
        }
    }

    public void insertar(Servicio entidad) throws SQLException {
        try (Connection c = conexiones.abrir();
             PreparedStatement s = c.prepareStatement("INSERT INTO servicios(codigo,nombre,activo) VALUES(?,?,?)")) {
            s.setInt(1, entidad.getCodigo());
            s.setString(2, entidad.getNombre());
            s.setBoolean(3, entidad.isActivo());
            s.executeUpdate();
        }
    }

    public void actualizar(Servicio entidad) throws SQLException {
        try (Connection c = conexiones.abrir();
             PreparedStatement s = c.prepareStatement("UPDATE servicios SET nombre=?,activo=? WHERE codigo=?")) {
            s.setString(1, entidad.getNombre());
            s.setBoolean(2, entidad.isActivo());
            s.setInt(3, entidad.getCodigo());
            if (s.executeUpdate() != 1) throw new SQLException("Servicio no registrado; no se guardaron cambios.");
        }
    }
}
