package edu.oficina.persistencia;

import java.sql.*;
import java.util.Objects;

/** Crea el esquema y carga los datos iniciales una sola vez, sin borrar información. */
public final class InicializadorBD {
    private final ProveedorConexion conexiones;
    private static final String[] ESQUEMA = {
        """
CREATE TABLE IF NOT EXISTS clientes (
 codigo INTEGER PRIMARY KEY CHECK(codigo > 0),
 nombre_completo TEXT NOT NULL CHECK(length(trim(nombre_completo)) > 0),
 telefono TEXT NOT NULL CHECK(length(telefono) = 8 AND telefono NOT GLOB '*[^0-9]*'),
 activo INTEGER NOT NULL CHECK(activo IN (0,1)))
        """,
        """
CREATE TABLE IF NOT EXISTS servicios (
 codigo INTEGER PRIMARY KEY CHECK(codigo > 0),
 nombre TEXT NOT NULL CHECK(length(trim(nombre)) > 0),
 activo INTEGER NOT NULL CHECK(activo IN (0,1)))
        """,
        """
CREATE TABLE IF NOT EXISTS turnos (
 numero INTEGER PRIMARY KEY AUTOINCREMENT,
 codigo_cliente INTEGER NOT NULL REFERENCES clientes(codigo),
 codigo_servicio INTEGER NOT NULL REFERENCES servicios(codigo),
 fecha_generacion TEXT NOT NULL,
 estado TEXT NOT NULL CHECK(estado IN ('PENDIENTE','ATENDIDO','CANCELADO')),
 fecha_finalizacion TEXT,
 CHECK((estado = 'PENDIENTE' AND fecha_finalizacion IS NULL)
 OR (estado <> 'PENDIENTE' AND fecha_finalizacion IS NOT NULL)))
        """,
        """
CREATE UNIQUE INDEX IF NOT EXISTS un_pendiente_por_cliente
 ON turnos(codigo_cliente) WHERE estado = 'PENDIENTE' 
        """,
        """
CREATE TRIGGER IF NOT EXISTS impedir_desactivar_cliente BEFORE UPDATE OF activo ON clientes
 WHEN NEW.activo = 0 AND EXISTS(SELECT 1 FROM turnos WHERE codigo_cliente = OLD.codigo AND estado = 'PENDIENTE')
 BEGIN SELECT RAISE(ABORT, 'Cliente con turno pendiente'); END
        """,
        """
CREATE TRIGGER IF NOT EXISTS impedir_desactivar_servicio BEFORE UPDATE OF activo ON servicios
 WHEN NEW.activo = 0 AND EXISTS(SELECT 1 FROM turnos WHERE codigo_servicio = OLD.codigo AND estado = 'PENDIENTE')
 BEGIN SELECT RAISE(ABORT, 'Servicio con turnos pendientes'); END
        """,
        """
CREATE TRIGGER IF NOT EXISTS exigir_entidades_activas BEFORE INSERT ON turnos
 WHEN COALESCE((SELECT activo FROM clientes WHERE codigo = NEW.codigo_cliente),0) <> 1
 OR COALESCE((SELECT activo FROM servicios WHERE codigo = NEW.codigo_servicio),0) <> 1
 BEGIN SELECT RAISE(ABORT, 'Cliente y servicio deben existir y estar activos'); END
        """
    };

    public InicializadorBD() { this(ConexionBD::obtenerConexion); }
    public InicializadorBD(ProveedorConexion conexiones) {
        this.conexiones = Objects.requireNonNull(conexiones);
    }

    public void inicializar() throws SQLException {
        try (Connection conexion = conexiones.abrir()) {
            conexion.setAutoCommit(false);
            try {
                int version;
                try (Statement s = conexion.createStatement(); ResultSet r = s.executeQuery("PRAGMA user_version")) {
                    r.next(); version = r.getInt(1);
                }
                if (version == 1) { conexion.commit(); return; }
                if (version != 0) throw new SQLException("Versión de base no compatible: " + version);
                try (Statement s = conexion.createStatement()) {
                    for (String sql : ESQUEMA) s.execute(sql);
                }
                cargarClientes(conexion);
                cargarServicios(conexion);
                try (Statement s = conexion.createStatement()) { s.execute("PRAGMA user_version = 1"); }
                conexion.commit();
            } catch (SQLException | RuntimeException e) {
                conexion.rollback();
                throw e;
            }
        }
    }

    private void cargarClientes(Connection conexion) throws SQLException {
        String[][] datos = {
            {"1001", "Ana López", "55501001"},
            {"1002", "Carlos Pérez", "55501002"},
            {"1003", "María García", "55501003"},
            {"1004", "José Hernández", "55501004"},
            {"1005", "Luisa Morales", "55501005"}
        };
        try (PreparedStatement s = conexion.prepareStatement(
                "INSERT INTO clientes(codigo,nombre_completo,telefono,activo) VALUES(?,?,?,1)")) {
            for (String[] fila : datos) {
                s.setInt(1, Integer.parseInt(fila[0])); s.setString(2, fila[1]); s.setString(3, fila[2]);
                s.executeUpdate();
            }
        }
    }

    private void cargarServicios(Connection conexion) throws SQLException {
        String[][] datos = {
            {"1", "Información"},
            {"2", "Entrega de documentos"},
            {"3", "Actualización de datos"}
        };
        try (PreparedStatement s = conexion.prepareStatement(
                "INSERT INTO servicios(codigo,nombre,activo) VALUES(?,?,1)")) {
            for (String[] fila : datos) {
                s.setInt(1, Integer.parseInt(fila[0])); s.setString(2, fila[1]); s.executeUpdate();
            }
        }
    }
}
