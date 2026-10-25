package edu.oficina.dao;

import edu.oficina.modelo.Turno;
import edu.oficina.persistencia.ConexionBD;
import edu.oficina.persistencia.ProveedorConexion;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class TurnoDAO extends DaoBase implements TurnoRepositorio {
    public TurnoDAO() { this(ConexionBD::obtenerConexion); }
    public TurnoDAO(ProveedorConexion conexiones) { super(conexiones); }

    private Turno leer(ResultSet r) throws SQLException {
        String finalizacion = r.getString("fecha_finalizacion");
        return new Turno(r.getLong("numero"), r.getInt("codigo_cliente"), r.getInt("codigo_servicio"),
                LocalDateTime.parse(r.getString("fecha_generacion")), Turno.Estado.valueOf(r.getString("estado")),
                finalizacion == null ? null : LocalDateTime.parse(finalizacion));
    }

    public List<Turno> listarPendientes() throws SQLException {
        List<Turno> resultado = new ArrayList<>();
        try (Connection c = conexiones.abrir();
             PreparedStatement s = c.prepareStatement("SELECT * FROM turnos WHERE estado='PENDIENTE' ORDER BY numero");
             ResultSet r = s.executeQuery()) {
            while (r.next()) resultado.add(leer(r));
        }
        return resultado;
    }

    public boolean tienePendienteCliente(int codigo) throws SQLException {
        return pendiente("SELECT 1 FROM turnos WHERE codigo_cliente=? AND estado='PENDIENTE'", codigo);
    }

    public boolean tienePendienteServicio(int codigo) throws SQLException {
        return pendiente("SELECT 1 FROM turnos WHERE codigo_servicio=? AND estado='PENDIENTE'", codigo);
    }

    private boolean pendiente(String sql, int codigo) throws SQLException {
        try (Connection c = conexiones.abrir(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, codigo);
            try (ResultSet r = s.executeQuery()) { return r.next(); }
        }
    }

    private void exigirActivo(Connection c, String sql, int codigo, String nombre) throws SQLException {
        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, codigo);
            try (ResultSet r = s.executeQuery()) {
                if (!r.next()) throw new IllegalArgumentException(nombre + " no registrado.");
                if (!r.getBoolean("activo")) throw new IllegalArgumentException(nombre + " inactivo.");
            }
        }
    }

    public Turno crearPendiente(int cliente, int servicio) throws SQLException {
        if (cliente <= 0 || servicio <= 0) throw new IllegalArgumentException("Los códigos deben ser positivos.");
        try (Connection c = conexiones.abrir()) {
            c.setAutoCommit(false);
            try {
                exigirActivo(c, "SELECT activo FROM clientes WHERE codigo=?", cliente, "Cliente");
                exigirActivo(c, "SELECT activo FROM servicios WHERE codigo=?", servicio, "Servicio");
                try (PreparedStatement s = c.prepareStatement(
                        "SELECT 1 FROM turnos WHERE codigo_cliente=? AND estado='PENDIENTE'")) {
                    s.setInt(1, cliente);
                    try (ResultSet r = s.executeQuery()) {
                        if (r.next()) throw new IllegalArgumentException("El cliente ya tiene un turno pendiente.");
                    }
                }
                LocalDateTime fecha = LocalDateTime.now();
                try (PreparedStatement s = c.prepareStatement(
                        "INSERT INTO turnos(codigo_cliente,codigo_servicio,fecha_generacion,estado) VALUES(?,?,?,'PENDIENTE')")) {
                    s.setInt(1, cliente); s.setInt(2, servicio); s.setString(3, fecha.toString()); s.executeUpdate();
                }
                long numero;
                try (Statement s = c.createStatement(); ResultSet r = s.executeQuery("SELECT last_insert_rowid()")) {
                    r.next(); numero = r.getLong(1);
                }
                Turno turno = new Turno(numero, cliente, servicio, fecha, Turno.Estado.PENDIENTE, null);
                c.commit();
                return turno;
            } catch (SQLException | RuntimeException e) {
                c.rollback(); throw e;
            }
        }
    }

    public Turno finalizar(long numero, Turno.Estado estado, LocalDateTime fecha) throws SQLException {
        Objects.requireNonNull(estado, "El estado es obligatorio.");
        Objects.requireNonNull(fecha, "La fecha de finalización es obligatoria.");
        if (numero <= 0 || estado == Turno.Estado.PENDIENTE) {
            throw new IllegalArgumentException("Se necesita un turno positivo y un estado final.");
        }
        try (Connection c = conexiones.abrir()) {
            c.setAutoCommit(false);
            try {
                Turno anterior;
                try (PreparedStatement s = c.prepareStatement("SELECT * FROM turnos WHERE numero=?")) {
                    s.setLong(1, numero);
                    try (ResultSet r = s.executeQuery()) {
                        if (!r.next()) throw new IllegalArgumentException("Turno no registrado.");
                        anterior = leer(r);
                    }
                }
                if (anterior.getEstado() != Turno.Estado.PENDIENTE) {
                    throw new IllegalArgumentException("El turno ya fue atendido o cancelado.");
                }
                try (PreparedStatement s = c.prepareStatement(
                        "UPDATE turnos SET estado=?,fecha_finalizacion=? WHERE numero=? AND estado='PENDIENTE'")) {
                    s.setString(1, estado.name()); s.setString(2, fecha.toString()); s.setLong(3, numero);
                    if (s.executeUpdate() != 1) throw new SQLException("El turno dejó de estar pendiente.");
                }
                Turno finalizado = new Turno(numero, anterior.getCodigoCliente(), anterior.getCodigoServicio(),
                        anterior.getFechaGeneracion(), estado, fecha);
                c.commit();
                return finalizado;
            } catch (SQLException | RuntimeException e) {
                c.rollback(); throw e;
            }
        }
    }
}
