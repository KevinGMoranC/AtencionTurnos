package edu.oficina.pruebas;

import edu.oficina.dao.*;
import edu.oficina.persistencia.*;
import java.nio.file.*;
import java.sql.*;

/** Bases temporales: nunca utiliza data/turnos.db del proyecto real. */
public final class PreparacionPruebas implements AutoCloseable {
    public final Path carpeta;
    public final Path archivo;
    public final ProveedorConexion conexiones;
    public final ClienteDAO clientes;
    public final ServicioDAO servicios;
    public final TurnoDAO turnos;

    public PreparacionPruebas() throws Exception {
        carpeta = Files.createTempDirectory("atencion-dia2-");
        archivo = carpeta.resolve("prueba.db");
        conexiones = () -> ConexionBD.obtenerConexion(archivo);
        new InicializadorBD(conexiones).inicializar();
        clientes = new ClienteDAO(conexiones);
        servicios = new ServicioDAO(conexiones);
        turnos = new TurnoDAO(conexiones);
    }

    @FunctionalInterface public interface Accion { void ejecutar() throws Exception; }
    public static void verificar(boolean condicion, String mensaje) {
        if (!condicion) throw new AssertionError(mensaje);
        System.out.println("OK: " + mensaje);
    }
    public static void rechazar(Accion accion, String mensaje) throws Exception {
        try { accion.ejecutar(); }
        catch (IllegalArgumentException | SQLException e) {
            System.out.println("OK rechazo: " + mensaje); return;
        }
        throw new AssertionError("No se rechazó: " + mensaje);
    }
    public void sql(String instruccion) throws SQLException {
        try (Connection c = conexiones.abrir(); Statement s = c.createStatement()) { s.execute(instruccion); }
    }
    public long escalar(String consulta) throws SQLException {
        try (Connection c = conexiones.abrir(); Statement s = c.createStatement(); ResultSet r = s.executeQuery(consulta)) {
            r.next(); return r.getLong(1);
        }
    }
    public void close() throws Exception {
        Files.deleteIfExists(archivo);
        Files.deleteIfExists(carpeta);
    }
}
