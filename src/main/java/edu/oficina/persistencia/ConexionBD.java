package edu.oficina.persistencia;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/** Abre conexiones SQLite; no crea tablas ni carga registros. */
public final class ConexionBD {
    private static final Path ARCHIVO = Path.of("data", "turnos.db");

    private ConexionBD() {}

    /** Quien llama debe cerrar la conexión, preferentemente con try-with-resources. */
    public static Connection obtenerConexion() throws SQLException {
        try {
            Files.createDirectories(ARCHIVO.getParent());
        } catch (IOException e) {
            throw new SQLException("No se pudo crear la carpeta relativa data.", e);
        }
        Connection conexion = DriverManager.getConnection("jdbc:sqlite:" + ARCHIVO);
        try (Statement instruccion = conexion.createStatement()) {
            instruccion.execute("PRAGMA foreign_keys = ON");
        } catch (SQLException e) {
            try {
                conexion.close();
            } catch (SQLException cierre) {
                e.addSuppressed(cierre);
            }
            throw e;
        }
        return conexion;
    }
}
