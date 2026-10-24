package edu.oficina;

import edu.oficina.estructuras.ArbolBST;
import edu.oficina.estructuras.ColaTurnos;
import edu.oficina.modelo.Cliente;
import edu.oficina.modelo.Servicio;
import edu.oficina.modelo.Turno;
import edu.oficina.persistencia.ConexionBD;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

/** Ejecución mínima de comprobación; todavía no es el menú final. */
public final class Main {
    private Main() {}

    public static void main(String[] args) {
        Cliente cliente = new Cliente(1001, "Ana López", "55501001", true);
        Servicio servicio = new Servicio(1, "Información", true);
        ArbolBST arbol = new ArbolBST();
        ColaTurnos cola = new ColaTurnos();

        System.out.println("AtencionTurnos - Primera entrega en desarrollo");
        System.out.println("Cliente de comprobación en memoria: " + cliente.getNombreCompleto());
        System.out.println("Servicio de comprobación en memoria: " + servicio.getNombre());
        System.out.println("BST inicialmente vacío: " + arbol.estaVacio());
        System.out.println("Cola inicialmente vacía: " + cola.estaVacia());
        System.out.println("Estados definidos: " + Arrays.toString(Turno.Estado.values()));

        try (Connection conexion = ConexionBD.obtenerConexion()) {
            System.out.println("Conexión JDBC correcta: "
                    + conexion.getMetaData().getDatabaseProductName());
            System.out.println("Archivo relativo: data/turnos.db");
            System.out.println("Esta etapa no crea tablas ni registra los datos de comprobación.");
        } catch (SQLException e) {
            System.err.println("No se pudo comprobar SQLite: " + e.getMessage());
            System.exit(1);
        }
    }
}
